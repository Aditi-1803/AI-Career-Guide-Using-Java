package com.careeriq.controller;

import com.careeriq.model.TestResult;
import com.careeriq.repository.TestResultRepository;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
public class SkillGapController {

    private final TestResultRepository testResultRepository;

    public SkillGapController(
            TestResultRepository testResultRepository) {
        this.testResultRepository = testResultRepository;
    }

    // =====================================================
    // SKILL GAP PAGE
    // =====================================================

    @GetMapping("/skill-gap")
    public String showSkillGap(
            @RequestParam(
                    required = false,
                    defaultValue = "Java Full Stack Developer")
            String career,
            Model model) {

        // Get requirements for the selected career
        Map<String, Integer> requiredSkills =
                getCareerRequirements(career);

        // Get latest score for every required skill
        Map<String, Integer> userScores =
                new LinkedHashMap<>();

        for (String skill : requiredSkills.keySet()) {
            userScores.put(skill, getUserSkillScore(skill));
        }

        // Analyze individual skills
        List<SkillGap> skillGaps = new ArrayList<>();

        for (String skill : requiredSkills.keySet()) {

            int required = requiredSkills.get(skill);
            int current = userScores.getOrDefault(skill, 0);

            int gap = Math.max(0, required - current);

            String status;

            if (current >= required) {
                status = "Strong";
            } else if (current >= required * 0.60) {
                status = "Improve";
            } else {
                status = "Major Gap";
            }

            skillGaps.add(
                    new SkillGap(
                            skill,
                            current,
                            required,
                            gap,
                            status
                    )
            );
        }

        // =================================================
        // CALCULATE CAREER READINESS
        // =================================================

        /*
         * Each skill contributes a maximum of 100% of its
         * required level.
         *
         * Example:
         * Current Java score = 63
         * Required Java score = 75
         * Java readiness = 63 / 75 * 100 = 84%
         *
         * Extra points in one skill cannot compensate for
         * missing points in another skill.
         */

        double totalSkillReadiness = 0;

        for (String skill : requiredSkills.keySet()) {

            int required = requiredSkills.get(skill);
            int current = userScores.getOrDefault(skill, 0);

            if (required > 0) {
                int cappedScore = Math.min(current, required);

                double skillReadiness =
                        (cappedScore * 100.0) / required;

                totalSkillReadiness += skillReadiness;
            }
        }

        int readiness = 0;

        if (!requiredSkills.isEmpty()) {
            readiness = (int) Math.round(
                    totalSkillReadiness / requiredSkills.size()
            );
        }

        readiness = Math.max(0, Math.min(100, readiness));

        // =================================================
        // IDENTIFY SKILL CATEGORIES
        // =================================================

        List<String> strongSkills = new ArrayList<>();
        List<String> improvementSkills = new ArrayList<>();
        List<String> majorGaps = new ArrayList<>();

        for (SkillGap skillGap : skillGaps) {

            if ("Strong".equals(skillGap.getStatus())) {
                strongSkills.add(skillGap.getSkill());

            } else if ("Improve".equals(skillGap.getStatus())) {
                improvementSkills.add(skillGap.getSkill());

            } else {
                majorGaps.add(skillGap.getSkill());
            }
        }

        // =================================================
        // RECOMMEND NEXT SKILLS TO LEARN
        // =================================================

        List<String> nextFocus = new ArrayList<>();

        // Prioritize major gaps first
        nextFocus.addAll(majorGaps);

        // Then prioritize improvement areas
        nextFocus.addAll(improvementSkills);

        // Limit the recommendation list to three skills
        if (nextFocus.size() > 3) {
            nextFocus = new ArrayList<>(
                    nextFocus.subList(0, 3)
            );
        }

        // =================================================
        // SEND DATA TO THYMELEAF
        // =================================================

        model.addAttribute("career", career);
        model.addAttribute("skillGaps", skillGaps);
        model.addAttribute("readiness", readiness);
        model.addAttribute("strongSkills", strongSkills);
        model.addAttribute("improvementSkills", improvementSkills);
        model.addAttribute("majorGaps", majorGaps);
        model.addAttribute("nextFocus", nextFocus);

        return "skill-gap";
    }

    // =====================================================
    // CAREER REQUIREMENTS
    // =====================================================

    private Map<String, Integer> getCareerRequirements(
            String career) {

        Map<String, Integer> skills = new LinkedHashMap<>();

        // JAVA FULL STACK DEVELOPER
        if (career.equalsIgnoreCase("Java Full Stack Developer")) {

            skills.put("Java", 75);
            skills.put("Spring Boot", 70);
            skills.put("JavaScript", 65);
            skills.put("SQL", 65);
            skills.put("HTML/CSS", 60);

            return skills;
        }

        // BACKEND DEVELOPER
        if (career.equalsIgnoreCase("Backend Developer")) {

            skills.put("Java", 75);
            skills.put("Spring Boot", 75);
            skills.put("SQL", 65);
            skills.put("DSA", 60);

            return skills;
        }

        // SOFTWARE ENGINEER
        if (career.equalsIgnoreCase("Software Engineer")) {

            skills.put("Java", 70);
            skills.put("DSA", 75);
            skills.put("Python", 60);
            skills.put("SQL", 55);
            skills.put("C/C++", 60);

            return skills;
        }

        // PYTHON DEVELOPER
        if (career.equalsIgnoreCase("Python Developer")) {

            skills.put("Python", 75);
            skills.put("SQL", 60);
            skills.put("DSA", 60);
            skills.put("Java", 50);

            return skills;
        }

        // DATA ANALYST
        if (career.equalsIgnoreCase("Data Analyst")) {

            skills.put("SQL", 75);
            skills.put("Python", 65);
            skills.put("DSA", 50);

            return skills;
        }

        // MACHINE LEARNING ENGINEER
        if (career.equalsIgnoreCase("Machine Learning Engineer")) {

            skills.put("Python", 80);
            skills.put("DSA", 65);
            skills.put("SQL", 55);
            skills.put("C/C++", 60);

            return skills;
        }

        // DEFAULT CAREER REQUIREMENTS
        skills.put("Java", 75);
        skills.put("SQL", 65);
        skills.put("JavaScript", 65);

        return skills;
    }

    // =====================================================
    // GET LATEST USER SCORE FROM DATABASE
    // =====================================================

    private int getUserSkillScore(String skill) {

        TestResult latestResult =
                testResultRepository
                        .findFirstBySkillOrderByIdDesc(skill);

        // No assessment exists for this skill
        if (latestResult == null) {
            return 0;
        }

        // Use only the latest saved assessment score
        return Math.max(
                0,
                Math.min(100, latestResult.getScore())
        );
    }

    // =====================================================
    // SKILL GAP MODEL
    // =====================================================

    public static class SkillGap {

        private String skill;
        private int currentScore;
        private int requiredScore;
        private int gap;
        private String status;

        public SkillGap(
                String skill,
                int currentScore,
                int requiredScore,
                int gap,
                String status) {

            this.skill = skill;
            this.currentScore = currentScore;
            this.requiredScore = requiredScore;
            this.gap = gap;
            this.status = status;
        }

        public String getSkill() {
            return skill;
        }

        public int getCurrentScore() {
            return currentScore;
        }

        public int getRequiredScore() {
            return requiredScore;
        }

        public int getGap() {
            return gap;
        }

        public String getStatus() {
            return status;
        }
    }
}