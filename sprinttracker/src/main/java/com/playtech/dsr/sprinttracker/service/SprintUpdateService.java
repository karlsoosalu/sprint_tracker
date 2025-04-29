package com.playtech.dsr.sprinttracker.service;

import com.playtech.dsr.sprinttracker.dto.IssueDto;
import com.playtech.dsr.sprinttracker.dto.SprintDto;
import com.playtech.dsr.sprinttracker.model.IssueId;
import com.playtech.dsr.sprinttracker.model.JiraIssue;
import com.playtech.dsr.sprinttracker.model.Sprint;
import com.playtech.dsr.sprinttracker.repository.JiraIssueRepository;
import com.playtech.dsr.sprinttracker.repository.SprintRepository;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.Objects.nonNull;

@Slf4j
@Service
public class SprintUpdateService {

    private final JiraService jiraService;
    private final SprintRepository sprintRepository;
    private final JiraIssueRepository issueRepository;

    public SprintUpdateService(JiraService jiraService, SprintRepository sprintRepository, JiraIssueRepository issueRepository) {
        this.jiraService = jiraService;
        this.sprintRepository = sprintRepository;
        this.issueRepository = issueRepository;
    }

    @Scheduled(fixedRate = 3600000) // Runs every hour
    public void updateActiveSprint() {
        Optional<Sprint> currentSprint = sprintRepository.findAll()
                .stream()
                .filter(sprint -> sprint.getState().equals("active"))
                .findFirst();

        if (currentSprint.isPresent()) {
            Sprint sprint = currentSprint.get();
            SprintDto.SprintDetails sprintInfo = jiraService.getSprintDetails(String.valueOf(sprint.getSprintId()));
            boolean isSameSprint = sprintInfo.getState().equals("active");
            if (!isSameSprint) {
                sprint.setState(sprintInfo.getState());
                sprintRepository.save(sprint);
                addNewActiveSprint();
            }
        } else {
            addNewActiveSprint();
        }
    }

    private void addNewActiveSprint() {
        Optional<Sprint> newSprint = jiraService.getActiveSprints()
                .getValues()
                .stream().filter(sprintDto -> sprintDto.getName().startsWith("DS&R Sprint"))
                .map(sprintDetails ->
                        Sprint.builder()
                                .sprintId(sprintDetails.getId())
                                .name(sprintDetails.getName())
                                .state(sprintDetails.getState())
                                .startDate(sprintDetails.getStartDate())
                                .endDate(sprintDetails.getEndDate())
                                .build()
                )
                .findFirst();

        if (newSprint.isPresent()) {
            sprintRepository.save(newSprint.get());
        } else {
            log.error("No active DSR sprint found");
        }
    }


    @Scheduled(fixedRate = 3600000)  // Runs every hour
    public void updateSprintData() {
        Optional<Sprint> currentSprintOptional = sprintRepository.findAll()
                .stream()
                .filter(sprint -> sprint.getState().equals("active"))
                .findFirst();
        if (currentSprintOptional.isEmpty()) {
            log.error("No active sprint to update");
            updateActiveSprint();
            return;
        }
        Sprint currentSprint = currentSprintOptional.get();
        log.info("Updating sprint data for {}", currentSprint.getName());

        Long sprintId = currentSprint.getSprintId();
        IssueDto issues = jiraService.getIssuesForSprint(sprintId);

        List<IssueDto.JiraIssueDto> mainSprintIssues = new ArrayList<>(issues.getIssues().stream()
                .filter(issue -> !issue.getFields().getIssuetype().getSubtask())
                .toList());

        //needed for a case where issue is removed from sprint, for example when it is moved to the next one in the end of the sprint
        List<JiraIssue> issuesInDb = issueRepository.findBySprintId(currentSprint.getId());
        boolean isInitialLoad = issuesInDb.isEmpty();
        issuesInDb.stream()
                .filter(issue -> mainSprintIssues.stream().noneMatch(issueDto -> issueDto.getKey().equals(issue.getId().getIssueKey())))
                .forEach(issueInDb -> mainSprintIssues.add(jiraService.getIssueDetails(issueInDb.getId().getIssueKey())));

        Map<String, Map<String, Integer>> timeLoggedForIssue = jiraService.getTeamTimeLoggedForIssues(currentSprint);

        List<JiraIssue> issuesToSave = new ArrayList<>();

        for (IssueDto.JiraIssueDto issueDto : mainSprintIssues) {
            IssueId issueId = new IssueId(issueDto.getKey(), currentSprint.getId());
            JiraIssue issue = issuesToSave.stream()
                    .filter(i -> i.getId().equals(issueId))
                    .findFirst()
                    .orElseGet(() -> {
                        JiraIssue newIssue = new JiraIssue();
                        newIssue.setId(issueId);
                        newIssue.setSprint(currentSprint);
                        issuesToSave.add(newIssue);
                        return newIssue;
                    });

            IssueDto.JiraIssueDto.Fields.Team team = issueDto.getFields().getCustomfield_19500();
            issue.setTeam(nonNull(team) ? team.getName() : null);
            issue.setIssueType(issueDto.getFields().getIssuetype().getName());

            Estimations estimations = parseEstimations(issueDto.getFields().getCustomfield_13125());
            issue.setEVT_estimated(estimations.getEVT_estimated());
            issue.setEVT_estimated_new(estimations.getEVT_estimated());
            issue.setEVT_late(estimations.getEVT_late());
            issue.setSE_estimated(estimations.getSE_estimated());
            issue.setSE_estimated_new(estimations.getSE_estimated());
            issue.setSE_late(estimations.getSE_late());
            issue.setRE_estimated(estimations.getRE_estimated());
            issue.setRE_estimated_new(estimations.getRE_estimated());
            issue.setRE_late(estimations.getRE_late());
            issue.setDM_estimated(estimations.getDM_estimated());
            issue.setDM_estimated_new(estimations.getDM_estimated());
            issue.setDM_late(estimations.getDM_late());
            issue.setQA_estimated(estimations.getQA_estimated());
            issue.setQA_estimated_new(estimations.getQA_estimated());
            issue.setQA_late(estimations.getQA_late());
            issue.setAddedLater(!isInitialLoad);

            issue.setTitle(issueDto.getFields().getSummary());
            issue.setStatus(issueDto.getFields().getStatus().getName());
            issue.setPriority(issueDto.getFields().getPriority().getName());
            issue.setEpic(
                    JiraIssue.Epic.builder()
                            .epic_key(nonNull(issueDto.getFields().getEpic()) ? issueDto.getFields().getEpic().getKey() : null)
                            .epic_name(nonNull(issueDto.getFields().getEpic()) ? issueDto.getFields().getEpic().getName() : null)
                            .build()
            );
        }

        timeLoggedForIssue.forEach((team1, issues1) -> issues1.forEach((issueKey, loggedTime) -> {
            IssueId issueId = new IssueId(issueKey, currentSprint.getId());
            JiraIssue issue = issuesToSave.stream()
                    .filter(i -> i.getId().equals(issueId))
                    .findFirst()
                    .orElseGet(() -> {
                        JiraIssue newIssue = new JiraIssue();
                        newIssue.setId(issueId);
                        newIssue.setSprint(currentSprint);

                        IssueDto.JiraIssueDto issueDetails = jiraService.getIssueDetails(issueId.getIssueKey());
                        newIssue.setTitle(issueDetails.getFields().getSummary());
                        newIssue.setTeam(nonNull(issueDetails.getFields().getCustomfield_19500()) ? issueDetails.getFields().getCustomfield_19500().getName() : null);
                        newIssue.setIssueType(issueDetails.getFields().getIssuetype().getName());
                        newIssue.setStatus(issueDetails.getFields().getStatus().getName());
                        newIssue.setPriority(issueDetails.getFields().getPriority().getName());
                        newIssue.setEpic(
                                JiraIssue.Epic.builder()
                                        .epic_key(nonNull(issueDetails.getFields().getEpic()) ? issueDetails.getFields().getEpic().getKey() : null)
                                        .epic_name(nonNull(issueDetails.getFields().getEpic()) ? issueDetails.getFields().getEpic().getName() : null)
                                        .build()
                        );
                        newIssue.setAddedLater(!isInitialLoad);

                        issuesToSave.add(newIssue);
                        return newIssue;
                    });

            switch (team1) {
                case "evt" -> issue.setEVT_logged(loggedTime);
                case "se" -> issue.setSE_logged(loggedTime);
                case "re" -> issue.setRE_logged(loggedTime);
                case "dm" -> issue.setDM_logged(loggedTime);
                case "qa" -> issue.setQA_logged(loggedTime);
            }
        }));

        log.info("Saving {} issues", issuesToSave.size());
        issueRepository.saveAll(issuesToSave);

        updatePreviousSprintWorkLogs(currentSprint.getId());
    }

    private void updatePreviousSprintWorkLogs(Long currentSprint) {
        Optional<Sprint> previousSprintOptional = sprintRepository.findAll()
                .stream()
                .filter(sprint -> sprint.getId().equals(currentSprint - 1))
                .findFirst();

        if (previousSprintOptional.isEmpty()) {
            log.error("No previous sprint found");
            return;
        }
        Sprint previousSprint = previousSprintOptional.get();
        List<JiraIssue> issuesInDb = issueRepository.findBySprintId(previousSprint.getId());
        Map<String, Map<String, Integer>> timeLoggedForIssue = jiraService.getTeamTimeLoggedForIssues(previousSprint);
        for (JiraIssue jiraIssue : issuesInDb) {
            IssueId issueId = jiraIssue.getId();
            String issueKey = issueId.getIssueKey();

            for (Map.Entry<String, Map<String, Integer>> entry : timeLoggedForIssue.entrySet()) {
                String team = entry.getKey();
                Map<String, Integer> issues = entry.getValue();
                if (issues.containsKey(issueKey)) {
                    int loggedTime = issues.get(issueKey);
                    switch (team) {
                        case "evt" -> jiraIssue.setEVT_logged(loggedTime);
                        case "se" -> jiraIssue.setSE_logged(loggedTime);
                        case "re" -> jiraIssue.setRE_logged(loggedTime);
                        case "dm" -> jiraIssue.setDM_logged(loggedTime);
                        case "qa" -> jiraIssue.setQA_logged(loggedTime);
                    }
                }
            }
            issueRepository.save(jiraIssue);
        }

    }

    public Estimations parseEstimations(String estimationsString) {
        Estimations estimations = new Estimations();
        if (estimationsString != null && !estimationsString.isEmpty()) {
            estimationsString = estimationsString.replaceAll("\\(.*?\\)", "").trim();

            String[] parts = estimationsString.split("[;,]");
            for (String part : parts) {
                String[] keyValue = part.split(":");
                if (keyValue.length == 2) {
                    String key = keyValue[0].trim();
                    int value;
                    boolean isLate = false;
                    String trimmedValue = keyValue[1].trim();
                    try {
                        value = Integer.parseInt(trimmedValue);
                    } catch (NumberFormatException e) {
                        if (trimmedValue.contains("LATE")) {
                            isLate = true;
                            try {
                                value = Integer.parseInt(trimmedValue.replace("LATE", "").trim());
                            } catch (NumberFormatException e2) {
                                log.error("Failed to parse estimation value: {}. Setting it to 0", trimmedValue);
                                value = 0;
                            }
                        } else {
                            log.error("Failed to parse estimation value: {}. Setting it to 0", trimmedValue);
                            value = 0;
                        }
                    }

                    switch (key) {
                        case "EVT" -> estimations.setEVT_estimated(value);
                        case "SE" -> estimations.setSE_estimated(value);
                        case "RE" -> estimations.setRE_estimated(value);
                        case "DM" -> estimations.setDM_estimated(value);
                        case "QA" -> estimations.setQA_estimated(value);
                    }
                    estimations.setLate(key, isLate);
                }
            }
        }
        return estimations;
    }

    @Data
    public static class Estimations {
        private Integer EVT_estimated;
        private Boolean EVT_late;
        private Integer SE_estimated;
        private Boolean SE_late;
        private Integer RE_estimated;
        private Boolean RE_late;
        private Integer DM_estimated;
        private Boolean DM_late;
        private Integer QA_estimated;
        private Boolean QA_late;

        public void setLate(String key, Boolean isLate) {
            switch (key) {
                case "EVT" -> this.EVT_late = isLate;
                case "SE" -> this.SE_late = isLate;
                case "RE" -> this.RE_late = isLate;
                case "DM" -> this.DM_late = isLate;
                case "QA" -> this.QA_late = isLate;
            }
        }
    }
}
