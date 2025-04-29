package com.playtech.dsr.sprinttracker.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class JiraIssueDto {
    private String id;
    private String title;
    private String team;
    private String issueType;
    private String status;
    private Epic epic;
    private String priority;
    private Integer EVT_estimated;
    private Boolean EVT_late;
    private Integer EVT_estimated_new;
    private Integer SE_estimated;
    private Boolean SE_late;
    private Integer SE_estimated_new;
    private Integer RE_estimated;
    private Boolean RE_late;
    private Integer RE_estimated_new;
    private Integer DM_estimated;
    private Boolean DM_late;
    private Integer DM_estimated_new;
    private Integer QA_estimated;
    private Boolean QA_late;
    private Integer QA_estimated_new;
    private Integer EVT_logged;
    private Integer SE_logged;
    private Integer RE_logged;
    private Integer DM_logged;
    private Integer QA_logged;
    private Boolean addedLater;

    @Data
    @Builder
    public static class Epic {
        private String key;
        private String name;
    }
}