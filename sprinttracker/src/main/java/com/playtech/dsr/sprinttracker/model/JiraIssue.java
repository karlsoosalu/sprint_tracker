package com.playtech.dsr.sprinttracker.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
public class JiraIssue {
    @EmbeddedId
    private IssueId id;

    private String title;
    private String team;
    private String issueType;
    @Column(updatable = false)
    private Integer EVT_estimated;
    @Column(updatable = false)
    private Boolean EVT_late;
    private Integer EVT_estimated_new;
    @Column(updatable = false)
    private Integer SE_estimated;
    @Column(updatable = false)
    private Boolean SE_late;
    private Integer SE_estimated_new;
    @Column(updatable = false)
    private Integer RE_estimated;
    @Column(updatable = false)
    private Boolean RE_late;
    private Integer RE_estimated_new;
    @Column(updatable = false)
    private Integer DM_estimated;
    @Column(updatable = false)
    private Boolean DM_late;
    private Integer DM_estimated_new;
    @Column(updatable = false)
    private Integer QA_estimated;
    @Column(updatable = false)
    private Boolean QA_late;
    private Integer QA_estimated_new;
    private Integer EVT_logged;
    private Integer SE_logged;
    private Integer RE_logged;
    private Integer DM_logged;
    private Integer QA_logged;
    private String status;
    private Epic epic;
    private String priority;
    @Column(updatable = false)
    private Boolean addedLater;

    @ManyToOne
    @MapsId("sprintId")
    @JoinColumn(name = "sprint_id")
    private Sprint sprint;

    @Embeddable
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Epic {
        private String epic_key;
        private String epic_name;
    }
}