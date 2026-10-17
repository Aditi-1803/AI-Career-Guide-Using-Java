package com.careeriq.controller;

import com.careeriq.model.TestResult;
import com.careeriq.repository.TestResultRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.*;

@Controller
public class DashboardController {

    private final TestResultRepository testResultRepository;

    public DashboardController(
            TestResultRepository testResultRepository) {

        this.testResultRepository =
                testResultRepository;
    }

    @GetMapping("/dashboard")
    public String showDashboard(Model model) {

        List<TestResult> results =
                testResultRepository.findAll();

        // ==============================
        // 1. OVERALL READINESS
        // ==============================

        int overallReadiness =
                calculateOverallReadiness(results);

        // ==============================
        // 2. ASSESSED SKILLS
        // ==============================

        Set<String> assessedSkills =
                new HashSet<>();

        for (TestResult result : results) {
            assessedSkills.add(
                    result.getSkill()
            );
        }

        int skillsAnalyzed =
                assessedSkills.size();

        // ==============================
        // 3. STRONGEST SKILL
        // ==============================

        String strongestSkill =
                getStrongestSkill(results);

        int strongestScore =
                getSkillScore(
                        results,
                        strongestSkill
                );

        // ==============================
        // 4. WEAKEST SKILL
        // ==============================

        String weakestSkill =
                getWeakestSkill(results);

        int weakestScore =
                getSkillScore(
                        results,
                        weakestSkill
                );

        // ==============================
        // 5. TOP CAREER MATCH
        // ==============================

        String topCareer =
                getTopCareer(
                        overallReadiness
                );

        int topCareerMatch =
                getCareerMatch(
                        overallReadiness
                );

        // ==============================
        // 6. SKILL DATA
        // ==============================

        Map<String, Integer> skillScores =
                new LinkedHashMap<>();

        String[] skills = {
                "Java",
                "SQL",
                "Python",
                "JavaScript",
                "DSA",
                "C/C++"
        };

        for (String skill : skills) {

            int score =
                    getSkillScore(
                            results,
                            skill
                    );

            if (score > 0) {
                skillScores.put(
                        skill,
                        score
                );
            }
        }

        // ==============================
        // 7. RECOMMENDED ACTION
        // ==============================

        String nextAction =
                getNextAction(
                        weakestSkill,
                        weakestScore
                );

        // ==============================
        // SEND DATA TO HTML
        // ==============================

        model.addAttribute(
                "overallReadiness",
                overallReadiness
        );

        model.addAttribute(
                "skillsAnalyzed",
                skillsAnalyzed
        );

        model.addAttribute(
                "strongestSkill",
                strongestSkill
        );

        model.addAttribute(
                "strongestScore",
                strongestScore
        );

        model.addAttribute(
                "weakestSkill",
                weakestSkill
        );

        model.addAttribute(
                "weakestScore",
                weakestScore
        );

        model.addAttribute(
                "topCareer",
                topCareer
        );

        model.addAttribute(
                "topCareerMatch",
                topCareerMatch
        );

        model.addAttribute(
                "skillScores",
                skillScores
        );

        model.addAttribute(
                "nextAction",
                nextAction
        );

        return "dashboard";
    }

    // ==========================================
    // OVERALL READINESS
    // ==========================================

    private int calculateOverallReadiness(
            List<TestResult> results) {

        if (results.isEmpty()) {
            return 0;
        }

        int total = 0;

        for (TestResult result : results) {
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

    // ==========================================
    // SKILL SCORE
    // ==========================================

    private int getSkillScore(
            List<TestResult> results,
            String skill) {

        if (skill == null) {
            return 0;
        }

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

    // ==========================================
    // STRONGEST SKILL
    // ==========================================

    private String getStrongestSkill(
            List<TestResult> results) {

        if (results.isEmpty()) {
            return "No assessment yet";
        }

        String strongest =
                "No assessment yet";

        int highest = -1;

        Set<String> skills =
                new HashSet<>();

        for (TestResult result : results) {
            skills.add(result.getSkill());
        }

        for (String skill : skills) {

            int score =
                    getSkillScore(
                            results,
                            skill
                    );

            if (score > highest) {
                highest = score;
                strongest = skill;
            }
        }

        return strongest;
    }

    // ==========================================
    // WEAKEST SKILL
    // ==========================================

    private String getWeakestSkill(
            List<TestResult> results) {

        if (results.isEmpty()) {
            return "No assessment yet";
        }

        String weakest =
                "No assessment yet";

        int lowest = 101;

        Set<String> skills =
                new HashSet<>();

        for (TestResult result : results) {
            skills.add(result.getSkill());
        }

        for (String skill : skills) {

            int score =
                    getSkillScore(
                            results,
                            skill
                    );

            if (score < lowest) {
                lowest = score;
                weakest = skill;
            }
        }

        return weakest;
    }

    // ==========================================
    // CAREER MATCH
    // ==========================================

    private String getTopCareer(
            int readiness) {

        if (readiness >= 80) {
            return "Java Full Stack Developer";
        }

        if (readiness >= 65) {
            return "Backend Developer";
        }

        if (readiness >= 50) {
            return "Software Engineer";
        }

        return "Java Full Stack Developer";
    }

    private int getCareerMatch(
            int readiness) {

        int match =
                readiness + 10;

        return Math.min(
                98,
                Math.max(
                        20,
                        match
                )
        );
    }

    // ==========================================
    // NEXT ACTION
    // ==========================================

    private String getNextAction(
            String weakestSkill,
            int weakestScore) {

        if (weakestSkill.equals(
                "No assessment yet")) {

            return
                    "Take your first technical assessment to build your CAREERIQ profile.";
        }

        if (weakestScore < 40) {

            return
                    "Build your " +
                            weakestSkill +
                            " fundamentals before moving to advanced topics.";
        }

        if (weakestScore < 60) {

            return
                    "Improve your " +
                            weakestSkill +
                            " through focused practice and a practical project.";
        }

        if (weakestScore < 75) {

            return
                    "Strengthen your " +
                            weakestSkill +
                            " to close your remaining skill gap.";
        }

        return
                "Maintain your current skills and focus on projects and interview preparation.";
    }
}