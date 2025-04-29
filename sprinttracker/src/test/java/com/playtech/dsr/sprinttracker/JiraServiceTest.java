package com.playtech.dsr.sprinttracker;

import com.playtech.dsr.sprinttracker.dto.IssueDto;
import com.playtech.dsr.sprinttracker.dto.SprintDto;
import com.playtech.dsr.sprinttracker.model.Sprint;
import com.playtech.dsr.sprinttracker.model.TeamMember;
import com.playtech.dsr.sprinttracker.repository.JiraIssueRepository;
import com.playtech.dsr.sprinttracker.repository.TeamMemberRepository;
import com.playtech.dsr.sprinttracker.service.JiraService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class JiraServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private TeamMemberRepository teamMemberRepository;

    @Mock
    private JiraIssueRepository jiraIssueRepository;

    @InjectMocks
    private JiraService jiraService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(jiraService, "jiraApiUrl", "http://mock-jira-url/");
    }

    @Test
    void testGetActiveSprints() {
        String url = "http://mock-jira-url/rest/agile/1.0/board/2010192/sprint?state=active";
        SprintDto mockResponse = new SprintDto();
        when(restTemplate.getForObject(url, SprintDto.class)).thenReturn(mockResponse);

        SprintDto result = jiraService.getActiveSprints();

        assertEquals(mockResponse, result);
        verify(restTemplate, times(1)).getForObject(url, SprintDto.class);
    }

    @Test
    void testGetSprintDetails() {
        String sprintId = "123";
        String url = "http://mock-jira-url/rest/agile/1.0/sprint/" + sprintId;
        SprintDto.SprintDetails mockResponse = new SprintDto.SprintDetails();
        mockResponse.setId(123L);
        mockResponse.setName("Test Sprint");
        mockResponse.setState("active");

        when(restTemplate.getForObject(url, SprintDto.SprintDetails.class)).thenReturn(mockResponse);

        SprintDto.SprintDetails result = jiraService.getSprintDetails(sprintId);

        assertEquals(mockResponse, result);
        verify(restTemplate, times(1)).getForObject(url, SprintDto.SprintDetails.class);
    }

    @Test
    void testGetIssuesForSprint() {
        Long sprintId = 123L;
        String url = "http://mock-jira-url/rest/agile/1.0/sprint/" + sprintId + "/issue?maxResults=5000";
        IssueDto mockResponse = new IssueDto();
        when(restTemplate.getForObject(url, IssueDto.class)).thenReturn(mockResponse);

        IssueDto result = jiraService.getIssuesForSprint(sprintId);

        assertEquals(mockResponse, result);
        verify(restTemplate, times(1)).getForObject(url, IssueDto.class);
    }

    @Test
    void testGetTeamTimeLoggedForIssues() {
        Sprint mockSprint = new Sprint();
        mockSprint.setStartDate("2023-01-01T00:00:00");
        mockSprint.setEndDate("2023-01-15T00:00:00");

        TeamMember mockTeamMember = new TeamMember();
        mockTeamMember.setJiraUsername("testUser");
        mockTeamMember.setTeam("QA");

        when(teamMemberRepository.findAll()).thenReturn(List.of(mockTeamMember));

        var result = jiraService.getTeamTimeLoggedForIssues(mockSprint);

        assertEquals(0, result.size()); // No issues mocked, so result should be empty
        verify(teamMemberRepository, times(1)).findAll();
    }
}