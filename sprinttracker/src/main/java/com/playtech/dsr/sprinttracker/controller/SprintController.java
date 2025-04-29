package com.playtech.dsr.sprinttracker.controller;

import com.playtech.dsr.sprinttracker.model.Sprint;
import com.playtech.dsr.sprinttracker.repository.SprintRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class SprintController {

    @Autowired
    private SprintRepository sprintRepository;

    @GetMapping("/sprints")
    public List<Sprint> getAllSprints() {
        return sprintRepository.findAll();
    }
}