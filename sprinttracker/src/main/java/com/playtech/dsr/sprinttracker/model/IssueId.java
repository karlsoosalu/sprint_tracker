package com.playtech.dsr.sprinttracker.model;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IssueId implements Serializable {
    private String issueKey;
    private Long sprintId;

    // Override equals and hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IssueId issueId = (IssueId) o;
        return Objects.equals(issueKey, issueId.issueKey) && Objects.equals(sprintId, issueId.sprintId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(issueKey, sprintId);
    }
}
