
package com.careeriq.controller;

import com.careeriq.model.RoadmapProgress;
import com.careeriq.model.TestResult;
import com.careeriq.repository.RoadmapProgressRepository;
import com.careeriq.repository.TestResultRepository;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Controller
public class RoadmapController {

    private final TestResultRepository testResultRepository;
    private final RoadmapProgressRepository roadmapProgressRepository;

    public RoadmapController(
            TestResultRepository testResultRepository,
            RoadmapProgressRepository roadmapProgressRepository) {

        this.testResultRepository = testResultRepository;
        this.roadmapProgressRepository = roadmapProgressRepository;
    }

    // =========================================================
    // PERSONALIZED ROADMAP
    // =========================================================

    @GetMapping("/roadmap")
    public String showRoadmap(
            @RequestParam(
                    required = false,
                    defaultValue = "Java Full Stack Developer"
            ) String career,
            Model model,
            HttpSession session) {

        // GET REQUIRED SKILLS FOR THE SELECTED CAREER

        Map<String, Integer> requiredSkills =
                getCareerRequirements(career);

        // CALCULATE USER SKILL SCORES

        Map<String, Integer> userScores = new LinkedHashMap<>();

        for (String skill : requiredSkills.keySet()) {
            userScores.put(skill, getUserSkillScore(skill));
        }

        // CREATE PRIORITY LIST

        List<SkillPriority> priorities = new ArrayList<>();

        for (String skill : requiredSkills.keySet()) {

            int required = requiredSkills.get(skill);
            int current = userScores.getOrDefault(skill, 0);
            int gap = Math.max(0, required - current);

            priorities.add(
                    new SkillPriority(skill, current, required, gap)
            );
        }

        // SORT BY LARGEST SKILL GAP

        priorities.sort(
                Comparator.comparingInt(SkillPriority::getGap)
                        .reversed()
        );

        // GENERATE PERSONALIZED ROADMAP

        List<RoadmapStep> roadmap = new ArrayList<>();
        int stepNumber = 1;

        // MAJOR SKILL GAPS

        for (SkillPriority priority : priorities) {
            if (priority.getGap() >= 25) {
                roadmap.add(
                        createSkillStep(
                                stepNumber++,
                                priority,
                                "High Priority"
                        )
                );
            }
        }

        // MODERATE SKILL GAPS

        for (SkillPriority priority : priorities) {
            if (priority.getGap() > 0 &&
                    priority.getGap() < 25) {

                roadmap.add(
                        createSkillStep(
                                stepNumber++,
                                priority,
                                "Improve"
                        )
                );
            }
        }

        // PROJECT BUILDING

        roadmap.add(
                new RoadmapStep(
                        stepNumber++,
                        "Build a Career-Focused Project",
                        "Apply your skills by building a practical project related to your target career.",
                        "Project Development",
                        getProjectForCareer(career),
                        "Project",
                        "2–3 weeks"
                )
        );

        // DSA AND PROBLEM SOLVING

        roadmap.add(
                new RoadmapStep(
                        stepNumber++,
                        "Practice DSA & Problem Solving",
                        "Solve coding problems regularly to strengthen logical thinking and interview performance.",
                        "DSA",
                        "Coding Practice Tracker",
                        "Interview",
                        "2 weeks"
                )
        );

        // INTERVIEW PREPARATION

        roadmap.add(
                new RoadmapStep(
                        stepNumber++,
                        "Prepare for Technical Interviews",
                        "Revise important concepts, practice interview questions and learn to explain your solutions.",
                        "Interview Preparation",
                        "Technical Interview Preparation",
                        "Interview",
                        "1–2 weeks"
                )
        );

        // JOB READINESS

        roadmap.add(
                new RoadmapStep(
                        stepNumber,
                        "Become Job Ready",
                        "Complete your projects, update your resume, build your portfolio and apply for relevant roles.",
                        "Career Readiness",
                        "Professional Portfolio",
                        "Career",
                        "1 week"
                )
        );

        // LOAD PREVIOUSLY SAVED PROGRESS

        List<RoadmapProgress> savedProgress =
                roadmapProgressRepository.findBySessionKeyAndCareer(
                        session.getId(),
                        career
                );

        Set<String> completedTitles = new HashSet<>();

        for (RoadmapProgress item : savedProgress) {
            if (item.isCompleted()) {
                completedTitles.add(item.getStepTitle());
            }
        }

        // MARK COMPLETED ROADMAP STEPS

        for (RoadmapStep step : roadmap) {
            step.setCompleted(
                    completedTitles.contains(step.getTitle())
            );
        }

        // COUNT COMPLETED STEPS

        int completedSteps = 0;

        for (RoadmapStep step : roadmap) {
            if (step.isCompleted()) {
                completedSteps++;
            }
        }

        // CALCULATE PROGRESS PERCENTAGE

        int progress = roadmap.isEmpty()
                ? 0
                : completedSteps * 100 / roadmap.size();

        // CALCULATE CAREER READINESS

        int totalCurrent = 0;
        int totalRequired = 0;

        for (String skill : requiredSkills.keySet()) {
            totalCurrent += userScores.getOrDefault(skill, 0);
            totalRequired += requiredSkills.get(skill);
        }

        int readiness = 0;

        if (totalRequired > 0) {
            readiness = totalCurrent * 100 / totalRequired;
        }

        readiness = Math.min(100, readiness);

        // FIND TOP PRIORITY

        String topPriority = priorities.isEmpty()
                ? "No major gaps"
                : priorities.get(0).getSkill();

        int topGap = priorities.isEmpty()
                ? 0
                : priorities.get(0).getGap();

        // SEND DATA TO THYMELEAF

        model.addAttribute("career", career);
        model.addAttribute("roadmap", roadmap);
        model.addAttribute("totalSteps", roadmap.size());
        model.addAttribute("completedSteps", completedSteps);
        model.addAttribute("progress", progress);
        model.addAttribute("readiness", readiness);
        model.addAttribute("topPriority", topPriority);
        model.addAttribute("topGap", topGap);
        model.addAttribute("userScores", userScores);
        model.addAttribute("requiredSkills", requiredSkills);

        return "roadmap";
    }

    // =========================================================
    // SAVE ROADMAP PROGRESS
    // =========================================================

    @PostMapping("/roadmap/progress")
    public String updateRoadmapProgress(
            @RequestParam String career,
            @RequestParam String stepTitle,
            @RequestParam(defaultValue = "false") boolean completed,
            HttpSession session) {

        Optional<RoadmapProgress> existing =
                roadmapProgressRepository
                        .findBySessionKeyAndCareerAndStepTitle(
                                session.getId(),
                                career,
                                stepTitle
                        );

        RoadmapProgress item = existing.orElseGet(
                RoadmapProgress::new
        );

        item.setSessionKey(session.getId());
        item.setCareer(career);
        item.setStepTitle(stepTitle);
        item.setCompleted(completed);

        roadmapProgressRepository.save(item);

        String encodedCareer =
                URLEncoder.encode(career, StandardCharsets.UTF_8);

        return "redirect:/roadmap?career=" + encodedCareer;
    }

    // =========================================================
    // CREATE A SKILL ROADMAP STEP
    // =========================================================

    private RoadmapStep createSkillStep(
            int stepNumber,
            SkillPriority priority,
            String priorityType) {

        String skill = priority.getSkill();
        int gap = priority.getGap();

        String description = getSkillDescription(skill);
        String project = getProjectForSkill(skill);
        String duration = calculateDuration(gap);

        return new RoadmapStep(
                stepNumber,
                "Improve " + skill,
                description,
                skill,
                project,
                priorityType,
                duration
        );
    }

    // =========================================================
    // DYNAMIC DURATION
    // =========================================================

    private String calculateDuration(int gap) {

        if (gap >= 40) {
            return "3–4 weeks";
        } else if (gap >= 25) {
            return "2–3 weeks";
        } else if (gap > 0) {
            return "1–2 weeks";
        }

        return "1 week";
    }

    // =========================================================
    // SKILL DESCRIPTIONS
    // =========================================================

    private String getSkillDescription(String skill) {

        if (skill.equalsIgnoreCase("Java")) {
            return "Strengthen Java fundamentals, OOP, collections, exception handling, multithreading and modern Java programming.";
        }

        if (skill.equalsIgnoreCase("Spring Boot")) {
            return "Learn Spring Boot, REST APIs, dependency injection, JPA, database integration and backend development.";
        }

        if (skill.equalsIgnoreCase("SQL")) {
            return "Practice SQL joins, subqueries, grouping, functions, constraints and complex database queries.";
        }

        if (skill.equalsIgnoreCase("JavaScript")) {
            return "Improve JavaScript fundamentals, DOM manipulation, events, asynchronous programming and API integration.";
        }

        if (skill.equalsIgnoreCase("HTML/CSS")) {
            return "Build responsive interfaces using semantic HTML, CSS layouts, Flexbox, Grid and modern design.";
        }

        if (skill.equalsIgnoreCase("Python")) {
            return "Strengthen Python programming, functions, collections, OOP, file handling and problem solving.";
        }

        if (skill.equalsIgnoreCase("DSA")) {
            return "Practice arrays, strings, linked lists, stacks, queues, trees, sorting, searching and algorithms.";
        }

        if (skill.equalsIgnoreCase("C/C++")) {
            return "Strengthen programming fundamentals, pointers, memory concepts, OOP and C/C++ problem solving.";
        }

        return "Strengthen this skill through structured learning, coding practice and practical projects.";
    }

    // =========================================================
    // PROJECT RECOMMENDATION BY SKILL
    // =========================================================

    private String getProjectForSkill(String skill) {

        if (skill.equalsIgnoreCase("Java")) {
            return "Java Expense Tracker";
        }

        if (skill.equalsIgnoreCase("Spring Boot")) {
            return "REST API Backend System";
        }

        if (skill.equalsIgnoreCase("SQL")) {
            return "Student Database Management System";
        }

        if (skill.equalsIgnoreCase("JavaScript")) {
            return "Interactive Task Management App";
        }

        if (skill.equalsIgnoreCase("HTML/CSS")) {
            return "Responsive Portfolio Website";
        }

        if (skill.equalsIgnoreCase("Python")) {
            return "Python Data Analysis Application";
        }

        if (skill.equalsIgnoreCase("DSA")) {
            return "Algorithm Visualizer";
        }

        if (skill.equalsIgnoreCase("C/C++")) {
            return "C++ Problem Solving Toolkit";
        }

        return "Practical Skill-Based Project";
    }

    // =========================================================
    // PROJECT RECOMMENDATION BY CAREER
    // =========================================================

    private String getProjectForCareer(String career) {

        if (career.equalsIgnoreCase("Java Full Stack Developer")) {
            return "Full Stack Job Portal";
        }

        if (career.equalsIgnoreCase("Backend Developer")) {
            return "Spring Boot E-Commerce Backend";
        }

        if (career.equalsIgnoreCase("Software Engineer")) {
            return "Online Coding Platform";
        }

        if (career.equalsIgnoreCase("Python Developer")) {
            return "Python Automation Platform";
        }

        if (career.equalsIgnoreCase("Data Analyst")) {
            return "Business Data Analytics Dashboard";
        }

        if (career.equalsIgnoreCase("Machine Learning Engineer")) {
            return "Machine Learning Prediction System";
        }

        return "Career-Focused Software Project";
    }

    // =========================================================
    // GET USER SKILL SCORE
    // =========================================================

    private int getUserSkillScore(String skill) {

        List<TestResult> results =
                testResultRepository.findBySkill(skill);

        if (results == null || results.isEmpty()) {
            return 0;
        }

        int total = 0;

        for (TestResult result : results) {
            total += result.getScore();
        }

        int average = total / results.size();

        return Math.max(0, Math.min(100, average));
    }

    // =========================================================
    // CAREER REQUIREMENTS
    // =========================================================

    private Map<String, Integer> getCareerRequirements(String career) {

        Map<String, Integer> skills = new LinkedHashMap<>();

        if (career.equalsIgnoreCase("Java Full Stack Developer")) {
            skills.put("Java", 75);
            skills.put("Spring Boot", 70);
            skills.put("JavaScript", 65);
            skills.put("SQL", 65);
            skills.put("HTML/CSS", 60);
            return skills;
        }

        if (career.equalsIgnoreCase("Backend Developer")) {
            skills.put("Java", 75);
            skills.put("Spring Boot", 75);
            skills.put("SQL", 65);
            skills.put("DSA", 60);
            return skills;
        }

        if (career.equalsIgnoreCase("Software Engineer")) {
            skills.put("Java", 70);
            skills.put("DSA", 75);
            skills.put("Python", 60);
            skills.put("SQL", 55);
            skills.put("C/C++", 60);
            return skills;
        }

        if (career.equalsIgnoreCase("Python Developer")) {
            skills.put("Python", 75);
            skills.put("SQL", 60);
            skills.put("DSA", 60);
            skills.put("Java", 50);
            return skills;
        }

        if (career.equalsIgnoreCase("Data Analyst")) {
            skills.put("SQL", 75);
            skills.put("Python", 65);
            skills.put("DSA", 50);
            return skills;
        }

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

    // =========================================================
    // SKILL PRIORITY MODEL
    // =========================================================

    public static class SkillPriority {

        private String skill;
        private int currentScore;
        private int requiredScore;
        private int gap;

        public SkillPriority(
                String skill,
                int currentScore,
                int requiredScore,
                int gap) {

            this.skill = skill;
            this.currentScore = currentScore;
            this.requiredScore = requiredScore;
            this.gap = gap;
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
    }

    // =========================================================
    // ROADMAP STEP MODEL
    // =========================================================

    public static class RoadmapStep {

        private int stepNumber;
        private String title;
        private String description;
        private String skill;
        private String project;
        private String category;
        private String duration;
        private boolean completed;

        public RoadmapStep(
                int stepNumber,
                String title,
                String description,
                String skill,
                String project,
                String category,
                String duration) {

            this.stepNumber = stepNumber;
            this.title = title;
            this.description = description;
            this.skill = skill;
            this.project = project;
            this.category = category;
            this.duration = duration;
            this.completed = false;
        }

        public int getStepNumber() {
            return stepNumber;
        }

        public String getTitle() {
            return title;
        }

        public String getDescription() {
            return description;
        }

        public String getSkill() {
            return skill;
        }

        public String getProject() {
            return project;
        }

        public String getCategory() {
            return category;
        }

        public String getDuration() {
            return duration;
        }

        public boolean isCompleted() {
            return completed;
        }

        public void setCompleted(boolean completed) {
            this.completed = completed;
        }
    }
}
