package com.playtech.dsr.sprinttracker.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class IssueDto {

    @JsonProperty("expand")
    private String expand;

    @JsonProperty("startAt")
    private Integer startAt;

    @JsonProperty("maxResults")
    private Integer maxResults;

    @JsonProperty("total")
    private Integer total;

    @JsonProperty("issues")
    private List<JiraIssueDto> issues;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class JiraIssueDto {

        @JsonProperty("expand")
        private String expand;

        @JsonProperty("id")
        private String id;

        @JsonProperty("self")
        private String self;

        @JsonProperty("key")
        private String key;

        @JsonProperty("fields")
        private Fields fields;

        private Integer timeAdded;

        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Fields {
            @JsonProperty("summary")
            private String summary;

            @JsonProperty("customfield_19500")
            private Team customfield_19500;

            @JsonProperty("customfield_13125")
            private String customfield_13125;

            @JsonProperty("timetracking")
            private TimeTracking timetracking;

            @JsonProperty("timespent")
            private String timespent;

            @JsonProperty("subtasks")
            private List<JiraIssueDto> subtasks;

            @JsonProperty("parent")
            private JiraIssueDto parent;

            @JsonProperty("issuetype")
            private IssueType issuetype;

            @JsonProperty("worklog")
            private WorklogDto worklog;

            @JsonProperty("status")
            private Status status;

            @JsonProperty("epic")
            private Epic epic;

            @JsonProperty("priority")
            private Priority priority;

            @Data
            @JsonIgnoreProperties(ignoreUnknown = true)
            public static class TimeTracking {
                @JsonProperty("timeSpent")
                private String timeSpent;

                @JsonProperty("timeSpentSeconds")
                private Integer timeSpentSeconds;
            }

            @Data
            @JsonIgnoreProperties(ignoreUnknown = true)
            public static class Team {
                @JsonProperty("id")
                private String id;

                @JsonProperty("name")
                private String name;
            }

            @Data
            @JsonIgnoreProperties(ignoreUnknown = true)
            public static class IssueType {
                @JsonProperty("id")
                private String id;

                @JsonProperty("description")
                private String description;

                @JsonProperty("name")
                private String name;

                @JsonProperty("subtask")
                private Boolean subtask;
            }

            @Data
            @JsonIgnoreProperties(ignoreUnknown = true)
            public static class Status {
                @JsonProperty("name")
                private String name;
            }

            @Data
            @JsonIgnoreProperties(ignoreUnknown = true)
            public static class Epic {
                @JsonProperty("id")
                private Long id;

                @JsonProperty("key")
                private String key;

                @JsonProperty("self")
                private String self;

                @JsonProperty("name")
                private String name;

                @JsonProperty("summary")
                private String summary;

                @JsonProperty("color")
                private Color color;

                @JsonProperty("done")
                private Boolean done;

                @Data
                @JsonIgnoreProperties(ignoreUnknown = true)
                public static class Color {
                    @JsonProperty("key")
                    private String key;
                }
            }

            @Data
            @JsonIgnoreProperties(ignoreUnknown = true)
            public static class Priority {
                @JsonProperty("self")
                private String self;

                @JsonProperty("iconUrl")
                private String iconUrl;

                @JsonProperty("name")
                private String name;

                @JsonProperty("id")
                private String id;
            }
        }
    }
}