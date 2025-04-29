package com.playtech.dsr.sprinttracker.controller;

import com.playtech.dsr.sprinttracker.dto.JiraIssueDto;
import com.playtech.dsr.sprinttracker.model.JiraIssue;
import com.playtech.dsr.sprinttracker.repository.JiraIssueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

@RestController
public class JiraIssueController {

    @Autowired
    private JiraIssueRepository jiraIssueRepository;

    @GetMapping("/jira-issues")
    public List<JiraIssueDto> getJiraIssuesBySprint(@RequestParam("sprintId") Long sprintId) {
        List<JiraIssue> issues = jiraIssueRepository.findBySprintId(sprintId);
        return issues.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    private JiraIssueDto convertToDto(JiraIssue issue) {
        return JiraIssueDto.builder()
                .id(issue.getId().getIssueKey())
                .title(issue.getTitle())
                .team(issue.getTeam())
                .issueType(issue.getIssueType())
                .status(issue.getStatus())
                .epic(
                        nonNull(issue.getEpic()) ?
                                JiraIssueDto.Epic.builder()
                                .key(issue.getEpic().getEpic_key())
                                .name(issue.getEpic().getEpic_name())
                                .build()
                                : null
                )
                .priority(issue.getPriority())
                .EVT_estimated(issue.getEVT_estimated())
                .EVT_late(issue.getEVT_late())
                .EVT_estimated_new(issue.getEVT_estimated_new())
                .SE_estimated(issue.getSE_estimated())
                .SE_late(issue.getSE_late())
                .SE_estimated_new(issue.getSE_estimated_new())
                .RE_estimated(issue.getRE_estimated())
                .RE_late(issue.getRE_late())
                .RE_estimated_new(issue.getRE_estimated_new())
                .DM_estimated(issue.getDM_estimated())
                .DM_late(issue.getDM_late())
                .DM_estimated_new(issue.getDM_estimated_new())
                .QA_estimated(issue.getQA_estimated())
                .QA_late(issue.getQA_late())
                .QA_estimated_new(issue.getQA_estimated_new())
                .EVT_logged(issue.getEVT_logged())
                .SE_logged(issue.getSE_logged())
                .RE_logged(issue.getRE_logged())
                .DM_logged(issue.getDM_logged())
                .QA_logged(issue.getQA_logged())
                .addedLater(issue.getAddedLater())
                .build();
    }
}