package com.careeriq.controller;

import com.careeriq.model.TestResult;
import com.careeriq.repository.TestResultRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Controller
public class ProjectRecommendationController {

    private final TestResultRepository testResultRepository;

    public ProjectRecommendationController(
            TestResultRepository testResultRepository) {

        this.testResultRepository = testResultRepository;
    }

    @GetMapping("/project-recommendations")
    public String showProjectRecommendations(
            @RequestParam(
                    required = false,
                    defaultValue = "Java Full Stack Developer"
            )
            String career,
            Model model) {

        List<ProjectRecommendation> projects =
                getProjectsForCareer(career);

        for (ProjectRecommendation project : projects) {

            int currentScore =
                    getUserSkillScore(project.getMainSkill());

            int requiredScore =
                    project.getRequiredScore();

            int gap =
                    Math.max(
                            0,
                            requiredScore - currentScore
                    );

            project.setCurrentScore(currentScore);
            project.setSkillGap(gap);

            if (gap >= 25) {
                project.setPriority("High Priority");
            } else if (gap > 0) {
                project.setPriority("Improve");
            } else {
                project.setPriority("Strong");
            }
        }

        /*
         * Projects with larger skill gaps
         * are shown first.
         */
        projects.sort(
                Comparator.comparingInt(
                        ProjectRecommendation::getSkillGap
                ).reversed()
        );

        ProjectRecommendation topProject =
                projects.isEmpty()
                        ? null
                        : projects.get(0);

        model.addAttribute(
                "career",
                career
        );

        model.addAttribute(
                "projects",
                projects
        );

        model.addAttribute(
                "topProject",
                topProject
        );

        return "project-recommendations";
    }

    /*
     * ================================
     * USER SKILL SCORE
     * ================================
     */

    private int getUserSkillScore(
            String skill) {

        List<TestResult> results =
                testResultRepository.findBySkill(
                        skill
                );

        if (results.isEmpty()) {
            return 0;
        }

        int total = 0;

        for (TestResult result :
                results) {

            total += result.getScore();
        }

        int average =
                total / results.size();

        return Math.max(
                0,
                Math.min(
                        100,
                        average
                )
        );
    }

    /*
     * ================================
     * PROJECT DATABASE
     * ================================
     *
     * These are product-level project
     * recommendations mapped to careers
     * and required skills.
     */

    private List<ProjectRecommendation>
    getProjectsForCareer(
            String career) {

        List<ProjectRecommendation> projects =
                new ArrayList<>();

        /*
         * JAVA FULL STACK
         */

        if (career.equalsIgnoreCase(
                "Java Full Stack Developer")) {

            projects.add(
                    new ProjectRecommendation(
                            "Full Stack Job Portal",
                            "Build a complete job portal where users can register, search jobs, apply for jobs and manage applications.",
                            "Java",
                            75,
                            "Java, Spring Boot, SQL, JavaScript",
                            "Advanced",
                            "3–4 weeks"
                    )
            );

            projects.add(
                    new ProjectRecommendation(
                            "E-Commerce Management System",
                            "Create an online shopping system with products, users, cart, orders and database integration.",
                            "Spring Boot",
                            70,
                            "Spring Boot, REST API, JPA, SQL",
                            "Advanced",
                            "3–4 weeks"
                    )
            );

            projects.add(
                    new ProjectRecommendation(
                            "Interactive Task Manager",
                            "Develop a responsive task management application with CRUD operations and an interactive frontend.",
                            "JavaScript",
                            65,
                            "JavaScript, HTML/CSS, REST API",
                            "Intermediate",
                            "2–3 weeks"
                    )
            );

            projects.add(
                    new ProjectRecommendation(
                            "Student Management Dashboard",
                            "Build a dashboard for managing students, courses, marks and academic records.",
                            "SQL",
                            65,
                            "Java, SQL, Spring Boot, HTML/CSS",
                            "Intermediate",
                            "2–3 weeks"
                    )
            );

            return projects;
        }

        /*
         * BACKEND DEVELOPER
         */

        if (career.equalsIgnoreCase(
                "Backend Developer")) {

            projects.add(
                    new ProjectRecommendation(
                            "Spring Boot E-Commerce Backend",
                            "Develop REST APIs for products, customers, carts, orders and authentication.",
                            "Spring Boot",
                            75,
                            "Spring Boot, REST API, JPA, SQL",
                            "Advanced",
                            "3–4 weeks"
                    )
            );

            projects.add(
                    new ProjectRecommendation(
                            "Banking Transaction API",
                            "Create backend APIs for accounts, deposits, withdrawals and transaction history.",
                            "Java",
                            75,
                            "Java, Spring Boot, SQL",
                            "Advanced",
                            "3 weeks"
                    )
            );

            projects.add(
                    new ProjectRecommendation(
                            "Database Management API",
                            "Build a REST API that performs secure CRUD operations on a relational database.",
                            "SQL",
                            65,
                            "SQL, JPA, REST API",
                            "Intermediate",
                            "2 weeks"
                    )
            );

            projects.add(
                    new ProjectRecommendation(
                            "Coding Problem API",
                            "Create an API that stores coding problems, submissions and user performance.",
                            "DSA",
                            60,
                            "Java, DSA, Spring Boot, SQL",
                            "Intermediate",
                            "2–3 weeks"
                    )
            );

            return projects;
        }

        /*
         * SOFTWARE ENGINEER
         */

        if (career.equalsIgnoreCase(
                "Software Engineer")) {

            projects.add(
                    new ProjectRecommendation(
                            "Online Coding Platform",
                            "Build a platform where users can solve programming problems and track their performance.",
                            "DSA",
                            75,
                            "Java, DSA, Algorithms, SQL",
                            "Advanced",
                            "4 weeks"
                    )
            );

            projects.add(
                    new ProjectRecommendation(
                            "Code Analysis Tool",
                            "Create a tool that analyzes programming solutions and provides basic performance information.",
                            "Java",
                            70,
                            "Java, Algorithms, Problem Solving",
                            "Advanced",
                            "3 weeks"
                    )
            );

            projects.add(
                    new ProjectRecommendation(
                            "Python Automation Suite",
                            "Develop useful automation utilities using Python for file and data processing.",
                            "Python",
                            60,
                            "Python, File Handling, Automation",
                            "Intermediate",
                            "2 weeks"
                    )
            );

            projects.add(
                    new ProjectRecommendation(
                            "Database Application",
                            "Develop a software application that uses SQL for structured data storage and retrieval.",
                            "SQL",
                            55,
                            "SQL, Java, Database Design",
                            "Intermediate",
                            "2 weeks"
                    )
            );

            return projects;
        }

        /*
         * PYTHON DEVELOPER
         */

        if (career.equalsIgnoreCase(
                "Python Developer")) {

            projects.add(
                    new ProjectRecommendation(
                            "Python Automation Platform",
                            "Create an application that automates repetitive file, data and reporting tasks.",
                            "Python",
                            75,
                            "Python, Automation, File Handling",
                            "Advanced",
                            "3 weeks"
                    )
            );

            projects.add(
                    new ProjectRecommendation(
                            "Python Web Application",
                            "Build a web application with user management, database connectivity and useful application features.",
                            "Python",
                            75,
                            "Python, Web Development, SQL",
                            "Advanced",
                            "3–4 weeks"
                    )
            );

            projects.add(
                    new ProjectRecommendation(
                            "Student Performance Analyzer",
                            "Analyze student records and generate performance reports.",
                            "SQL",
                            60,
                            "Python, SQL, Data Analysis",
                            "Intermediate",
                            "2 weeks"
                    )
            );

            projects.add(
                    new ProjectRecommendation(
                            "Algorithm Practice Tool",
                            "Create a Python application containing common searching, sorting and data structure algorithms.",
                            "DSA",
                            60,
                            "Python, DSA, Algorithms",
                            "Intermediate",
                            "2–3 weeks"
                    )
            );

            return projects;
        }

        /*
         * DATA ANALYST
         */

        if (career.equalsIgnoreCase(
                "Data Analyst")) {

            projects.add(
                    new ProjectRecommendation(
                            "Business Analytics Dashboard",
                            "Create a dashboard that analyzes sales, customers and business performance.",
                            "SQL",
                            75,
                            "SQL, Data Analysis, Visualization",
                            "Advanced",
                            "3 weeks"
                    )
            );

            projects.add(
                    new ProjectRecommendation(
                            "Student Data Analysis System",
                            "Analyze student performance data and generate meaningful reports and insights.",
                            "Python",
                            65,
                            "Python, Pandas, Data Analysis",
                            "Intermediate",
                            "2–3 weeks"
                    )
            );

            projects.add(
                    new ProjectRecommendation(
                            "Data Cleaning Toolkit",
                            "Build a Python tool that detects and cleans missing, duplicate and inconsistent data.",
                            "Python",
                            65,
                            "Python, Data Cleaning, Pandas",
                            "Intermediate",
                            "2 weeks"
                    )
            );

            return projects;
        }

        /*
         * MACHINE LEARNING ENGINEER
         */

        if (career.equalsIgnoreCase(
                "Machine Learning Engineer")) {

            projects.add(
                    new ProjectRecommendation(
                            "ML Prediction System",
                            "Build a machine learning application that trains a model and provides predictions from user input.",
                            "Python",
                            80,
                            "Python, Machine Learning, Statistics",
                            "Advanced",
                            "4 weeks"
                    )
            );

            projects.add(
                    new ProjectRecommendation(
                            "Recommendation System",
                            "Create a recommendation engine that suggests items based on user behaviour.",
                            "Python",
                            80,
                            "Python, Machine Learning, Data Processing",
                            "Advanced",
                            "4 weeks"
                    )
            );

            projects.add(
                    new ProjectRecommendation(
                            "Algorithm Visualizer",
                            "Develop a visual application that demonstrates searching, sorting and algorithm behaviour.",
                            "DSA",
                            65,
                            "DSA, Algorithms, Python",
                            "Intermediate",
                            "2–3 weeks"
                    )
            );

            projects.add(
                    new ProjectRecommendation(
                            "ML Dataset Management System",
                            "Create a system for storing, querying and preparing datasets for machine learning experiments.",
                            "SQL",
                            55,
                            "SQL, Python, Data Management",
                            "Intermediate",
                            "2 weeks"
                    )
            );

            return projects;
        }

        /*
         * DEFAULT
         */

        projects.add(
                new ProjectRecommendation(
                        "Personal Portfolio Application",
                        "Build a professional application demonstrating your programming skills and projects.",
                        "Java",
                        70,
                        "Java, HTML/CSS, JavaScript",
                        "Intermediate",
                        "2–3 weeks"
                )
        );

        return projects;
    }


    /*
     * ================================
     * PROJECT MODEL
     * ================================
     */

    public static class ProjectRecommendation {

        private String projectName;

        private String description;

        private String mainSkill;

        private int requiredScore;

        private String technologies;

        private String difficulty;

        private String duration;

        private int currentScore;

        private int skillGap;

        private String priority;


        public ProjectRecommendation(
                String projectName,
                String description,
                String mainSkill,
                int requiredScore,
                String technologies,
                String difficulty,
                String duration) {

            this.projectName = projectName;
            this.description = description;
            this.mainSkill = mainSkill;
            this.requiredScore = requiredScore;
            this.technologies = technologies;
            this.difficulty = difficulty;
            this.duration = duration;
        }


        public String getProjectName() {
            return projectName;
        }

        public String getDescription() {
            return description;
        }

        public String getMainSkill() {
            return mainSkill;
        }

        public int getRequiredScore() {
            return requiredScore;
        }

        public String getTechnologies() {
            return technologies;
        }

        public String getDifficulty() {
            return difficulty;
        }

        public String getDuration() {
            return duration;
        }

        public int getCurrentScore() {
            return currentScore;
        }

        public int getSkillGap() {
            return skillGap;
        }

        public String getPriority() {
            return priority;
        }


        public void setCurrentScore(
                int currentScore) {

            this.currentScore = currentScore;
        }

        public void setSkillGap(
                int skillGap) {

            this.skillGap = skillGap;
        }

        public void setPriority(
                String priority) {

            this.priority = priority;
        }
    }
}