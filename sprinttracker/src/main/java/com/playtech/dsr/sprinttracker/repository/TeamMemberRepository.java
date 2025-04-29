package com.playtech.dsr.sprinttracker.repository;

import com.playtech.dsr.sprinttracker.model.TeamMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {
}