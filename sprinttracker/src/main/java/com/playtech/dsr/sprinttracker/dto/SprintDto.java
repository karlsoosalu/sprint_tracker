package com.playtech.dsr.sprinttracker.dto;

import lombok.Data;
import lombok.Getter;

import java.util.List;

@Data
@Getter
public class SprintDto {
    private Integer maxResults;
    private Integer startAt;
    private Boolean isLast;
    private List<SprintDetails> values;

    @Data
    public static class SprintDetails {
        private Long id;
        private String self;
        private String state;
        private String name;
        private String startDate;
        private String endDate;
        private String activatedDate;
        private Integer originBoardId;
        private String goal;
        private Boolean synced;
        private Boolean autoStartStop;
    }
}