package com.careeriq.controller;

import com.careeriq.model.TestResult;
import com.careeriq.repository.TestResultRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
public class CareerSimulatorController {

    private final TestResultRepository testResultRepository;

    public CareerSimulatorController(
            TestResultRepository testResultRepository) {

        this.testResultRepository = testResultRepository;
    }


    // =========================================================
    // CAREER SIMULATOR
    // =========================================================

    @GetMapping("/career-simulator")
    public String showSimulator(

            @RequestParam(
                    required = false,
                    defaultValue = "Java Full Stack Developer"
            )
            String career,

            Model model) {


        // -----------------------------------------------------
        // CAREER REQUIREMENTS
        // -----------------------------------------------------

        Map<String, Integer> requiredSkills =
                getCareerRequirements(career);


        // -----------------------------------------------------
        // GET CURRENT USER SCORES
        // -----------------------------------------------------

        Map<String, Integer> currentScores =
                new LinkedHashMap<>();


        for (String skill :
                requiredSkills.keySet()) {

            currentScores.put(
                    skill,
                    getUserSkillScore(skill)
            );
        }


        // -----------------------------------------------------
        // CURRENT READINESS
        // -----------------------------------------------------

        int currentReadiness =
                calculateReadiness(
                        currentScores,
                        requiredSkills
                );


        // -----------------------------------------------------
        // SEND DATA TO PAGE
        // -----------------------------------------------------

        model.addAttribute(
                "career",
                career
        );

        model.addAttribute(
                "requiredSkills",
                requiredSkills
        );

        model.addAttribute(
                "currentScores",
                currentScores
        );

        model.addAttribute(
                "currentReadiness",
                currentReadiness
        );


        // Default simulation values

        model.addAttribute(
                "simulatedReadiness",
                currentReadiness
        );

        model.addAttribute(
                "improvement",
                0
        );


        return "career-simulator";
    }


    // =========================================================
    // SIMULATE IMPROVEMENT
    // =========================================================

    @GetMapping("/career-simulator/simulate")
    public String simulate(

            @RequestParam(
                    required = false,
                    defaultValue = "Java Full Stack Developer"
            )
            String career,

            @RequestParam(
                    required = false,
                    defaultValue = "0"
            )
            int javaImprovement,

            @RequestParam(
                    required = false,
                    defaultValue = "0"
            )
            int springImprovement,

            @RequestParam(
                    required = false,
                    defaultValue = "0"
            )
            int sqlImprovement,

            @RequestParam(
                    required = false,
                    defaultValue = "0"
            )
            int javascriptImprovement,

            @RequestParam(
                    required = false,
                    defaultValue = "0"
            )
            int htmlImprovement,

            Model model) {


        // -----------------------------------------------------
        // CAREER REQUIREMENTS
        // -----------------------------------------------------

        Map<String, Integer> requiredSkills =
                getCareerRequirements(career);


        // -----------------------------------------------------
        // CURRENT SCORES
        // -----------------------------------------------------

        Map<String, Integer> currentScores =
                new LinkedHashMap<>();


        for (String skill :
                requiredSkills.keySet()) {

            currentScores.put(
                    skill,
                    getUserSkillScore(skill)
            );
        }


        // -----------------------------------------------------
        // CREATE SIMULATED SCORES
        // -----------------------------------------------------

        Map<String, Integer> simulatedScores =
                new LinkedHashMap<>(
                        currentScores
                );


        // Java

        if (simulatedScores.containsKey("Java")) {

            simulatedScores.put(
                    "Java",
                    improveScore(
                            currentScores.get("Java"),
                            javaImprovement
                    )
            );
        }


        // Spring Boot

        if (simulatedScores.containsKey(
                "Spring Boot")) {

            simulatedScores.put(
                    "Spring Boot",
                    improveScore(
                            currentScores.get("Spring Boot"),
                            springImprovement
                    )
            );
        }


        // SQL

        if (simulatedScores.containsKey("SQL")) {

            simulatedScores.put(
                    "SQL",
                    improveScore(
                            currentScores.get("SQL"),
                            sqlImprovement
                    )
            );
        }


        // JavaScript

        if (simulatedScores.containsKey(
                "JavaScript")) {

            simulatedScores.put(
                    "JavaScript",
                    improveScore(
                            currentScores.get("JavaScript"),
                            javascriptImprovement
                    )
            );
        }


        // HTML/CSS

        if (simulatedScores.containsKey(
                "HTML/CSS")) {

            simulatedScores.put(
                    "HTML/CSS",
                    improveScore(
                            currentScores.get("HTML/CSS"),
                            htmlImprovement
                    )
            );
        }


        // -----------------------------------------------------
        // CALCULATE READINESS
        // -----------------------------------------------------

        int currentReadiness =
                calculateReadiness(
                        currentScores,
                        requiredSkills
                );


        int simulatedReadiness =
                calculateReadiness(
                        simulatedScores,
                        requiredSkills
                );


        int improvement =
                simulatedReadiness -
                        currentReadiness;


        // -----------------------------------------------------
        // SEND DATA TO PAGE
        // -----------------------------------------------------

        model.addAttribute(
                "career",
                career
        );

        model.addAttribute(
                "requiredSkills",
                requiredSkills
        );

        model.addAttribute(
                "currentScores",
                currentScores
        );

        model.addAttribute(
                "simulatedScores",
                simulatedScores
        );

        model.addAttribute(
                "currentReadiness",
                currentReadiness
        );

        model.addAttribute(
                "simulatedReadiness",
                simulatedReadiness
        );

        model.addAttribute(
                "improvement",
                improvement
        );


        return "career-simulator";
    }


    // =========================================================
    // IMPROVE SCORE
    // =========================================================

    private int improveScore(
            int currentScore,
            int improvement) {

        int newScore =
                currentScore + improvement;


        return Math.min(
                100,
                Math.max(
                        0,
                        newScore
                )
        );
    }


    // =========================================================
    // CALCULATE READINESS
    // =========================================================

    private int calculateReadiness(

            Map<String, Integer> scores,

            Map<String, Integer> requirements) {


        int totalCurrent = 0;

        int totalRequired = 0;


        for (String skill :
                requirements.keySet()) {

            totalCurrent +=
                    Math.min(
                            scores.getOrDefault(
                                    skill,
                                    0
                            ),
                            requirements.get(skill)
                    );

            totalRequired +=
                    requirements.get(skill);
        }


        if (totalRequired == 0) {

            return 0;
        }


        int readiness =
                (totalCurrent * 100)
                        / totalRequired;


        return Math.min(
                100,
                readiness
        );
    }


    // =========================================================
    // GET USER SCORE
    // =========================================================

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

            total +=
                    result.getScore();
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


    // =========================================================
    // CAREER REQUIREMENTS
    // =========================================================

    private Map<String, Integer>
    getCareerRequirements(
            String career) {


        Map<String, Integer> skills =
                new LinkedHashMap<>();


        // -----------------------------------------------------
        // JAVA FULL STACK
        // -----------------------------------------------------

        if (career.equalsIgnoreCase(
                "Java Full Stack Developer")) {

            skills.put("Java", 75);
            skills.put("Spring Boot", 70);
            skills.put("JavaScript", 65);
            skills.put("SQL", 65);
            skills.put("HTML/CSS", 60);

            return skills;
        }


        // -----------------------------------------------------
        // BACKEND DEVELOPER
        // -----------------------------------------------------

        if (career.equalsIgnoreCase(
                "Backend Developer")) {

            skills.put("Java", 75);
            skills.put("Spring Boot", 75);
            skills.put("SQL", 65);
            skills.put("DSA", 60);

            return skills;
        }


        // -----------------------------------------------------
        // SOFTWARE ENGINEER
        // -----------------------------------------------------

        if (career.equalsIgnoreCase(
                "Software Engineer")) {

            skills.put("Java", 70);
            skills.put("DSA", 75);
            skills.put("Python", 60);
            skills.put("SQL", 55);
            skills.put("C/C++", 60);

            return skills;
        }


        // -----------------------------------------------------
        // PYTHON DEVELOPER
        // -----------------------------------------------------

        if (career.equalsIgnoreCase(
                "Python Developer")) {

            skills.put("Python", 75);
            skills.put("SQL", 60);
            skills.put("DSA", 60);
            skills.put("Java", 50);

            return skills;
        }


        // -----------------------------------------------------
        // DATA ANALYST
        // -----------------------------------------------------

        if (career.equalsIgnoreCase(
                "Data Analyst")) {

            skills.put("SQL", 75);
            skills.put("Python", 65);
            skills.put("DSA", 50);

            return skills;
        }


        // -----------------------------------------------------
        // MACHINE LEARNING ENGINEER
        // -----------------------------------------------------

        if (career.equalsIgnoreCase(
                "Machine Learning Engineer")) {

            skills.put("Python", 80);
            skills.put("DSA", 65);
            skills.put("SQL", 55);
            skills.put("C/C++", 60);

            return skills;
        }


        // -----------------------------------------------------
        // DEFAULT
        // -----------------------------------------------------

        skills.put("Java", 75);
        skills.put("SQL", 65);
        skills.put("JavaScript", 65);

        return skills;
    }
}