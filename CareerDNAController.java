package com.careeriq.controller;

import com.careeriq.model.TestResult;
import com.careeriq.repository.TestResultRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
public class CareerDNAController {

    private final TestResultRepository testResultRepository;

    public CareerDNAController(
            TestResultRepository testResultRepository) {

        this.testResultRepository =
                testResultRepository;
    }


    // =========================================================
    // CAREER DNA PAGE
    // =========================================================

    @GetMapping("/career-dna")
    public String showCareerDNA(Model model) {


        // -----------------------------------------------------
        // GET ALL ASSESSMENT RESULTS
        // -----------------------------------------------------

        List<TestResult> results =
                testResultRepository.findAll();


        // -----------------------------------------------------
        // CALCULATE SKILL SCORES
        // -----------------------------------------------------

        int javaScore =
                getSkillScore(results, "Java");

        int pythonScore =
                getSkillScore(results, "Python");

        int sqlScore =
                getSkillScore(results, "SQL");

        int javascriptScore =
                getSkillScore(results, "JavaScript");

        int dsaScore =
                getSkillScore(results, "DSA");

        int cppScore =
                getSkillScore(results, "C/C++");


        // -----------------------------------------------------
        // TECHNICAL STRENGTH
        // -----------------------------------------------------

        int technicalStrength =
                calculateAverage(
                        javaScore,
                        pythonScore,
                        sqlScore,
                        javascriptScore
                );


        // -----------------------------------------------------
        // PROGRAMMING ABILITY
        // -----------------------------------------------------

        int programmingAbility =
                calculateAverage(
                        javaScore,
                        pythonScore,
                        cppScore
                );


        // -----------------------------------------------------
        // DATABASE SKILLS
        // -----------------------------------------------------

        int databaseSkills =
                sqlScore;


        // -----------------------------------------------------
        // PROBLEM SOLVING
        // -----------------------------------------------------

        int problemSolving =
                calculateAverage(
                        dsaScore,
                        javaScore,
                        pythonScore
                );


        // -----------------------------------------------------
        // OVERALL READINESS
        // -----------------------------------------------------

        int careerReadiness =
                calculateOverallReadiness(
                        results
                );


        // -----------------------------------------------------
        // LEARNING POTENTIAL
        // -----------------------------------------------------

        int learningPotential =
                calculateLearningPotential(
                        results
                );


        // -----------------------------------------------------
        // CAREER DNA TYPE
        // -----------------------------------------------------

        String dnaType =
                determineDNAType(
                        javaScore,
                        pythonScore,
                        sqlScore,
                        javascriptScore,
                        dsaScore,
                        cppScore
                );


        // -----------------------------------------------------
        // CAREER DNA DESCRIPTION
        // -----------------------------------------------------

        String dnaDescription =
                getDNADescription(
                        dnaType
                );


        // -----------------------------------------------------
        // STRONGEST SKILL
        // -----------------------------------------------------

        String strongestSkill =
                getStrongestSkill(
                        javaScore,
                        pythonScore,
                        sqlScore,
                        javascriptScore,
                        dsaScore,
                        cppScore
                );


        // -----------------------------------------------------
        // SKILL LABELS
        // -----------------------------------------------------

        List<String> skillLabels =
                new ArrayList<>();

        skillLabels.add("Java");
        skillLabels.add("Python");
        skillLabels.add("SQL");
        skillLabels.add("JavaScript");
        skillLabels.add("DSA");
        skillLabels.add("C/C++");


        // -----------------------------------------------------
        // SKILL VALUES
        // -----------------------------------------------------

        List<Integer> skillValues =
                new ArrayList<>();

        skillValues.add(javaScore);
        skillValues.add(pythonScore);
        skillValues.add(sqlScore);
        skillValues.add(javascriptScore);
        skillValues.add(dsaScore);
        skillValues.add(cppScore);


        // -----------------------------------------------------
        // SEND DATA TO THYMELEAF
        // -----------------------------------------------------

        model.addAttribute(
                "technicalStrength",
                technicalStrength
        );

        model.addAttribute(
                "programmingAbility",
                programmingAbility
        );

        model.addAttribute(
                "databaseSkills",
                databaseSkills
        );

        model.addAttribute(
                "problemSolving",
                problemSolving
        );

        model.addAttribute(
                "careerReadiness",
                careerReadiness
        );

        model.addAttribute(
                "learningPotential",
                learningPotential
        );

        model.addAttribute(
                "dnaType",
                dnaType
        );

        model.addAttribute(
                "dnaDescription",
                dnaDescription
        );

        model.addAttribute(
                "strongestSkill",
                strongestSkill
        );

        model.addAttribute(
                "skillLabels",
                skillLabels
        );

        model.addAttribute(
                "skillValues",
                skillValues
        );


        return "career-dna";
    }


    // =========================================================
    // GET SKILL SCORE
    // =========================================================

    private int getSkillScore(
            List<TestResult> results,
            String skill) {

        int total = 0;
        int count = 0;


        for (TestResult result : results) {

            if (skill.equalsIgnoreCase(
                    result.getSkill())) {

                total += result.getScore();

                count++;
            }
        }


        if (count == 0) {

            return 0;
        }


        int average =
                total / count;


        return Math.max(
                0,
                Math.min(
                        100,
                        average
                )
        );
    }


    // =========================================================
    // CALCULATE AVERAGE
    // =========================================================

    private int calculateAverage(
            int... scores) {

        int total = 0;
        int count = 0;


        for (int score : scores) {

            if (score > 0) {

                total += score;
                count++;
            }
        }


        if (count == 0) {

            return 0;
        }


        return total / count;
    }


    // =========================================================
    // OVERALL READINESS
    // =========================================================

    private int calculateOverallReadiness(
            List<TestResult> results) {

        if (results.isEmpty()) {

            return 0;
        }


        int total = 0;


        for (TestResult result :
                results) {

            total += result.getScore();
        }


        int readiness =
                total / results.size();


        return Math.max(
                0,
                Math.min(
                        100,
                        readiness
                )
        );
    }


    // =========================================================
    // LEARNING POTENTIAL
    // =========================================================

    private int calculateLearningPotential(
            List<TestResult> results) {

        if (results.isEmpty()) {

            return 0;
        }


        /*
         * Learning potential is estimated from:
         *
         * 1. Number of skills assessed
         * 2. Current assessment performance
         * 3. Exposure to multiple technical areas
         */

        int skillCount = 0;

        boolean java = false;
        boolean python = false;
        boolean sql = false;
        boolean javascript = false;
        boolean dsa = false;
        boolean cpp = false;


        for (TestResult result :
                results) {

            String skill =
                    result.getSkill();


            if ("Java".equalsIgnoreCase(skill)) {
                java = true;
            }

            if ("Python".equalsIgnoreCase(skill)) {
                python = true;
            }

            if ("SQL".equalsIgnoreCase(skill)) {
                sql = true;
            }

            if ("JavaScript".equalsIgnoreCase(skill)) {
                javascript = true;
            }

            if ("DSA".equalsIgnoreCase(skill)) {
                dsa = true;
            }

            if ("C/C++".equalsIgnoreCase(skill)) {
                cpp = true;
            }
        }


        if (java) skillCount++;
        if (python) skillCount++;
        if (sql) skillCount++;
        if (javascript) skillCount++;
        if (dsa) skillCount++;
        if (cpp) skillCount++;


        int averageScore =
                calculateOverallReadiness(
                        results
                );


        int exposureScore =
                skillCount * 10;


        int potential =
                (averageScore + exposureScore)
                        / 2;


        return Math.max(
                0,
                Math.min(
                        100,
                        potential
                )
        );
    }


    // =========================================================
    // DETERMINE CAREER DNA TYPE
    // =========================================================

    private String determineDNAType(

            int javaScore,

            int pythonScore,

            int sqlScore,

            int javascriptScore,

            int dsaScore,

            int cppScore) {


        int programming =
                calculateAverage(
                        javaScore,
                        pythonScore,
                        cppScore
                );


        int development =
                calculateAverage(
                        javaScore,
                        javascriptScore
                );


        int data =
                calculateAverage(
                        pythonScore,
                        sqlScore
                );


        int problemSolving =
                calculateAverage(
                        dsaScore,
                        javaScore,
                        pythonScore
                );


        if (development >= data &&
                development >= problemSolving) {

            return "Builder";
        }


        if (data >= development &&
                data >= problemSolving) {

            return "Data Explorer";
        }


        if (problemSolving >=
                programming) {

            return "Problem Solver";
        }


        return "Technical Explorer";
    }


    // =========================================================
    // DNA DESCRIPTION
    // =========================================================

    private String getDNADescription(
            String dnaType) {

        if ("Builder".equals(dnaType)) {

            return "Your profile shows a strong orientation toward building software applications and working with development technologies.";
        }


        if ("Data Explorer".equals(dnaType)) {

            return "Your profile shows an interest in working with data, databases, analysis and programming.";
        }


        if ("Problem Solver".equals(dnaType)) {

            return "Your profile shows a strong problem-solving and algorithmic orientation supported by programming skills.";
        }


        return "Your profile shows a broad technical foundation across multiple programming and technology areas.";
    }


    // =========================================================
    // STRONGEST SKILL
    // =========================================================

    private String getStrongestSkill(

            int javaScore,

            int pythonScore,

            int sqlScore,

            int javascriptScore,

            int dsaScore,

            int cppScore) {


        String strongest =
                "No assessed skill";


        int highest = 0;


        if (javaScore > highest) {

            highest = javaScore;
            strongest = "Java";
        }


        if (pythonScore > highest) {

            highest = pythonScore;
            strongest = "Python";
        }


        if (sqlScore > highest) {

            highest = sqlScore;
            strongest = "SQL";
        }


        if (javascriptScore > highest) {

            highest = javascriptScore;
            strongest = "JavaScript";
        }


        if (dsaScore > highest) {

            highest = dsaScore;
            strongest = "DSA";
        }


        if (cppScore > highest) {

            strongest = "C/C++";
        }


        return strongest;
    }
}