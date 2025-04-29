package com.playtech.dsr.sprinttracker.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
public class WorklogDto {

    @JsonProperty("startAt")
    private Integer startAt;

    @JsonProperty("maxResults")
    private Integer maxResults;

    @JsonProperty("total")
    private Integer total;

    @JsonProperty("worklogs")
    private List<WorklogEntry> worklogs;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class WorklogEntry {

        @JsonProperty("self")
        private String self;

        @JsonProperty("author")
        private Author author;

        @JsonProperty("updateAuthor")
        private Author updateAuthor;

        @JsonProperty("comment")
        private String comment;

        @JsonProperty("created")
        private String created;

        @JsonProperty("updated")
        private String updated;

        @JsonProperty("started")
        private String started;

        @JsonProperty("timeSpent")
        private String timeSpent;

        @JsonProperty("timeSpentSeconds")
        private Integer timeSpentSeconds;

        @JsonProperty("id")
        private String id;

        @JsonProperty("issueId")
        private String issueId;

        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Author {

            @JsonProperty("self")
            private String self;

            @JsonProperty("name")
            private String name;

            @JsonProperty("key")
            private String key;

            @JsonProperty("emailAddress")
            private String emailAddress;

            @JsonProperty("avatarUrls")
            private AvatarUrls avatarUrls;

            @JsonProperty("displayName")
            private String displayName;

            @JsonProperty("active")
            private Boolean active;

            @JsonProperty("timeZone")
            private String timeZone;

            @Data
            @JsonIgnoreProperties(ignoreUnknown = true)
            public static class AvatarUrls {

                @JsonProperty("48x48")
                private String size48x48;

                @JsonProperty("24x24")
                private String size24x24;

                @JsonProperty("16x16")
                private String size16x16;

                @JsonProperty("32x32")
                private String size32x32;
            }
        }
    }
}
