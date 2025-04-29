package com.playtech.dsr.sprinttracker;

import com.playtech.dsr.sprinttracker.dto.IssueDto;
import com.playtech.dsr.sprinttracker.dto.SprintDto;
import com.playtech.dsr.sprinttracker.model.Sprint;
import com.playtech.dsr.sprinttracker.repository.JiraIssueRepository;
import com.playtech.dsr.sprinttracker.repository.SprintRepository;
import com.playtech.dsr.sprinttracker.service.JiraService;
import com.playtech.dsr.sprinttracker.service.SprintUpdateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SprintUpdateServiceTest {

    @Mock
    private JiraService jiraService;

    @Mock
    private SprintRepository sprintRepository;

    @Mock
    private JiraIssueRepository issueRepository;

    @InjectMocks
    private SprintUpdateService sprintUpdateService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testUpdateActiveSprint_WhenActiveSprintExists() {
        Sprint activeSprint = new Sprint();
        activeSprint.setSprintId(1L);
        activeSprint.setState("active");

        SprintDto activeSprints = new SprintDto();
        SprintDto.SprintDetails sprintDetails = new SprintDto.SprintDetails();
        sprintDetails.setId(2L);
        sprintDetails.setName("DS&R Sprint 2");
        sprintDetails.setState("closed");
        activeSprints.setValues(List.of(sprintDetails));

        when(sprintRepository.findAll()).thenReturn(List.of(activeSprint));
        when(jiraService.getSprintDetails(String.valueOf(activeSprint.getSprintId()))).thenReturn(sprintDetails);
        when(jiraService.getActiveSprints()).thenReturn(activeSprints);

        sprintUpdateService.updateActiveSprint();

        verify(sprintRepository, times(1)).save(activeSprint);
        verify(jiraService, times(1)).getActiveSprints();
    }

    @Test
    void testUpdateActiveSprint_WhenNoActiveSprintExists() {
        SprintDto activeSprints = getActiveSprint();

        when(sprintRepository.findAll()).thenReturn(List.of());
        when(jiraService.getActiveSprints()).thenReturn(activeSprints);

        sprintUpdateService.updateActiveSprint();

        verify(sprintRepository, times(1)).save(any(Sprint.class));
    }

    @Test
    void testUpdateSprintData_WhenActiveSprintExists() {
        Sprint activeSprint = new Sprint();
        activeSprint.setSprintId(2L);
        activeSprint.setId(2L);
        activeSprint.setState("active");
        Sprint previousSprint = new Sprint();
        previousSprint.setSprintId(1L);
        previousSprint.setId(1L);
        previousSprint.setState("closed");

        IssueDto issueDto = getIssueDto();

        when(sprintRepository.findAll()).thenReturn(List.of(previousSprint, activeSprint));
        when(jiraService.getIssuesForSprint(activeSprint.getSprintId())).thenReturn(issueDto);
        when(issueRepository.findBySprintId(activeSprint.getId())).thenReturn(List.of());

        sprintUpdateService.updateSprintData();

        verify(issueRepository, times(1)).saveAll(anyList());
    }

    @Test
    void testUpdateSprintData_WhenNoActiveSprintExists() {
        SprintDto activeSprints = getActiveSprint();

        when(sprintRepository.findAll()).thenReturn(List.of());
        when(jiraService.getActiveSprints()).thenReturn(activeSprints);

        sprintUpdateService.updateSprintData();

        verify(jiraService, times(1)).getActiveSprints();
        verify(issueRepository, never()).saveAll(anyList());
    }

    @Test
    void testParseEstimations_ValidInput() {
        String input = "EVT:5; SE:3; RE:2 (some note); DM:4; QA:1";
        SprintUpdateService.Estimations estimations = sprintUpdateService.parseEstimations(input);

        assertEquals(5, estimations.getEVT_estimated());
        assertEquals(3, estimations.getSE_estimated());
        assertEquals(2, estimations.getRE_estimated());
        assertEquals(4, estimations.getDM_estimated());
        assertEquals(1, estimations.getQA_estimated());
        assertFalse(estimations.getEVT_late());
        assertFalse(estimations.getSE_late());
    }

    @Test
    void testParseEstimations_WithLateFlag() {
        String input = "EVT:5 LATE (some note); SE:3; RE:2; DM:4 LATE (some note); QA:1";
        SprintUpdateService.Estimations estimations = sprintUpdateService.parseEstimations(input);

        assertEquals(5, estimations.getEVT_estimated());
        assertTrue(estimations.getEVT_late());
        assertEquals(3, estimations.getSE_estimated());
        assertFalse(estimations.getSE_late());
        assertEquals(4, estimations.getDM_estimated());
        assertTrue(estimations.getDM_late());
    }

    @Test
    void testParseEstimations_InvalidInput() {
        String input = "EVT:abc; SE:3; RE:xyz; DM:4; QA:1";
        SprintUpdateService.Estimations estimations = sprintUpdateService.parseEstimations(input);

        assertEquals(0, estimations.getEVT_estimated());
        assertEquals(3, estimations.getSE_estimated());
        assertEquals(0, estimations.getRE_estimated());
        assertEquals(4, estimations.getDM_estimated());
        assertEquals(1, estimations.getQA_estimated());
    }

    @Test
    void testParseEstimations_EmptyInput() {
        String input = "";
        SprintUpdateService.Estimations estimations = sprintUpdateService.parseEstimations(input);

        assertNull(estimations.getEVT_estimated());
        assertNull(estimations.getSE_estimated());
        assertNull(estimations.getRE_estimated());
        assertNull(estimations.getDM_estimated());
        assertNull(estimations.getQA_estimated());
    }

    @Test
    void testParseEstimations_NullInput() {
        SprintUpdateService.Estimations estimations = sprintUpdateService.parseEstimations(null);

        assertNull(estimations.getEVT_estimated());
        assertNull(estimations.getSE_estimated());
        assertNull(estimations.getRE_estimated());
        assertNull(estimations.getDM_estimated());
        assertNull(estimations.getQA_estimated());
    }

    private SprintDto getActiveSprint() {
        SprintDto activeSprints = new SprintDto();
        SprintDto.SprintDetails sprintDetails = new SprintDto.SprintDetails();
        sprintDetails.setId(2L);
        sprintDetails.setName("DS&R Sprint 2");
        sprintDetails.setState("active");
        activeSprints.setValues(List.of(sprintDetails));
        return activeSprints;
    }

    private IssueDto getIssueDto() {
        IssueDto issueDto = new IssueDto();
        IssueDto.JiraIssueDto jiraIssueDto = new IssueDto.JiraIssueDto();
        jiraIssueDto.setKey("ISSUE-1");
        jiraIssueDto.setId("1");
        IssueDto.JiraIssueDto.Fields fields = new IssueDto.JiraIssueDto.Fields();
        IssueDto.JiraIssueDto.Fields.IssueType issueType = new IssueDto.JiraIssueDto.Fields.IssueType();
        issueType.setSubtask(false);
        fields.setIssuetype(issueType);
        fields.setStatus(new IssueDto.JiraIssueDto.Fields.Status());
        fields.setPriority(new IssueDto.JiraIssueDto.Fields.Priority());
        fields.setEpic(new IssueDto.JiraIssueDto.Fields.Epic());
        jiraIssueDto.setFields(fields);
        issueDto.setIssues(List.of(jiraIssueDto));
        return issueDto;
    }
}