package com.playtech.dsr.sprinttracker.repository;

import com.playtech.dsr.sprinttracker.model.Sprint;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SprintRepository extends JpaRepository<Sprint, Long> {
    Sprint findBySprintId(Long sprintId);
}
