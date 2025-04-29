package com.playtech.dsr.sprinttracker.repository;

import com.playtech.dsr.sprinttracker.model.IssueId;
import com.playtech.dsr.sprinttracker.model.JiraIssue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JiraIssueRepository extends JpaRepository<JiraIssue, IssueId> {
    List<JiraIssue> findBySprintId(Long sprintId);
}
