package com.playtech.dsr.sprinttracker.service;

import com.playtech.dsr.sprinttracker.dto.IssueDto;
import com.playtech.dsr.sprinttracker.dto.SprintDto;
import com.playtech.dsr.sprinttracker.dto.WorklogDto;
import com.playtech.dsr.sprinttracker.model.Sprint;
import com.playtech.dsr.sprinttracker.model.TeamMember;
import com.playtech.dsr.sprinttracker.repository.JiraIssueRepository;
import com.playtech.dsr.sprinttracker.repository.TeamMemberRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Service
public class JiraService {

    @Value("${jira.api.url}")
    private String jiraApiUrl;

    private final RestTemplate restTemplate;
    private final TeamMemberRepository teamMemberRepository;
    private final JiraIssueRepository jiraIssueRepository;

    public JiraService(RestTemplate restTemplate, TeamMemberRepository teamMemberRepository, JiraIssueRepository jiraIssueRepository) {
        this.restTemplate = restTemplate;
        this.teamMemberRepository = teamMemberRepository;
        this.jiraIssueRepository = jiraIssueRepository;
    }

    public SprintDto getActiveSprints() {
        String url = UriComponentsBuilder.fromHttpUrl(jiraApiUrl + "/rest/agile/1.0/board/2010192/sprint")
                .queryParam("state", "active")
                .toUriString();

        return restTemplate.getForObject(url, SprintDto.class);
    }

    public SprintDto.SprintDetails getSprintDetails(String sprintId) {
        String url = UriComponentsBuilder.fromHttpUrl(jiraApiUrl + "/rest/agile/1.0/sprint/" + sprintId)
                .toUriString();

        return restTemplate.getForObject(url, SprintDto.SprintDetails.class);
    }

    public IssueDto getIssuesForSprint(Long sprintId) {
        String url = UriComponentsBuilder.fromHttpUrl(jiraApiUrl + "rest/agile/1.0/sprint/" + sprintId + "/issue")
                .queryParam("maxResults", "5000")
                .toUriString();

        return restTemplate.getForObject(url, IssueDto.class);
    }

    public IssueDto.JiraIssueDto getIssueDetails(String issueKey) {
        String url = jiraApiUrl + "rest/api/2/issue/" + issueKey;
        return restTemplate.getForObject(url, IssueDto.JiraIssueDto.class);
    }

    public Map<String, Map<String, Integer>> getTeamTimeLoggedForIssues(Sprint currentSprint) {
        List<TeamMember> teamMembers = teamMemberRepository.findAll();

        String startDate = currentSprint.getStartDate().substring(0, 10); // Get only the date part of the string
        String endDate = currentSprint.getEndDate().substring(0, 10); // Get only the date part of the string

        String jqlQuery = String.format("worklogAuthor IN (%s) AND worklogDate >= %s AND worklogDate <= %s",
                teamMembers.stream().map(teamMember -> "\"" + teamMember.getJiraUsername() + "\"").collect(Collectors.joining(",")), startDate, endDate);

        String searchUrl = jiraApiUrl + "rest/api/2/search?maxResults=1000&jql=" + jqlQuery;

        IssueDto issues = restTemplate.getForObject(searchUrl, IssueDto.class);

        Map<String, Map<String, Integer>> teamIssueTimeMap = new HashMap<>();

        if (nonNull(issues)) {
            for (IssueDto.JiraIssueDto jiraIssueDto : issues.getIssues()) {
                Map<String, Integer> timeLoggedForIssue = getTimeLoggedForIssue(jiraIssueDto, startDate, endDate, teamMembers);
                IssueDto.JiraIssueDto parent = jiraIssueDto.getFields().getParent();
                String issueKey = isNull(parent) ? jiraIssueDto.getKey() : parent.getKey();

                for (Map.Entry<String, Integer> entry : timeLoggedForIssue.entrySet()) {
                    String team = entry.getKey();
                    Integer loggedTime = entry.getValue();

                    teamIssueTimeMap
                            .computeIfAbsent(team, k -> new HashMap<>())
                            .merge(issueKey, loggedTime, Integer::sum);
                }
            }
        }

        return teamIssueTimeMap;
    }

    private Map<String, Integer> getTimeLoggedForIssue(IssueDto.JiraIssueDto issueDto, String startDate, String endDate, List<TeamMember> teamMembers) {
        String worklogUrl = UriComponentsBuilder.fromHttpUrl(jiraApiUrl + "/rest/api/2/issue/" + issueDto.getKey() + "/worklog")
                .toUriString();

        WorklogDto worklogs = restTemplate.getForObject(worklogUrl, WorklogDto.class);

        Map<String, Integer> teamTimeLoggedMap = new HashMap<>();

        if (worklogs != null) {
            worklogs.getWorklogs().forEach(worklog -> {
                String worklogTime = worklog.getStarted().substring(0, 10);
                if (worklogTime.compareTo(startDate) >= 0 && worklogTime.compareTo(endDate) < 0) {
                    teamMembers.forEach(member -> {
                        if (member.getJiraUsername().equals(worklog.getAuthor().getDisplayName())) {
                            teamTimeLoggedMap.merge(member.getTeam(), worklog.getTimeSpentSeconds(), Integer::sum);
                        }
                    });
                }
            });
        }

        return teamTimeLoggedMap;
    }
}