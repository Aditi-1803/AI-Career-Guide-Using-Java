package com.careeriq.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SkillController {

    @GetMapping("/skills")
    public String showSkills() {

        return "skills";
    }
}