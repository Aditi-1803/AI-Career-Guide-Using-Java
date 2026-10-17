package com.careeriq.controller;

import com.careeriq.model.TestResult;
import com.careeriq.repository.TestResultRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class SkillIntelligenceController {

    private final TestResultRepository testResultRepository;

    public SkillIntelligenceController(
            TestResultRepository testResultRepository) {

        this.testResultRepository = testResultRepository;
    }

    @GetMapping("/skill-intelligence")
    public String showSkillIntelligence(
            @RequestParam("score") int score,
            @RequestParam("skill") String skill,
            Model model) {

        // -----------------------------------------
        // GET ALL RESULTS FOR THIS SKILL
        // -----------------------------------------

        List<TestResult> results =
                testResultRepository.findBySkill(skill);

        int overallScore = score;

        // -----------------------------------------
        // CALCULATE AVERAGE SCORE
        // -----------------------------------------

        if (!results.isEmpty()) {

            int totalScore = 0;

            for (TestResult result : results) {
                totalScore += result.getScore();
            }

            overallScore =
                    totalScore / results.size();
        }

        // Keep score within 0-100
        overallScore =
                Math.max(
                        0,
                        Math.min(
                                100,
                                overallScore
                        )
                );

        // -----------------------------------------
        // SKILL INTELLIGENCE COMPONENTS
        // -----------------------------------------

        int knowledgeScore =
                overallScore;

        int problemSolvingScore =
                Math.min(
                        100,
                        overallScore + 5
                );

        int applicationScore =
                Math.max(
                        0,
                        overallScore - 3
                );

        int debuggingScore =
                Math.max(
                        0,
                        overallScore - 5
                );

        // Confidence is based on the assessment
        int confidenceScore =
                Math.min(
                        100,
                        Math.max(
                                0,
                                overallScore + 2
                        )
                );

        // -----------------------------------------
        // SKILL LEVEL
        // -----------------------------------------

        String skillLevel;

        if (overallScore < 40) {

            skillLevel = "Beginner";

        } else if (overallScore < 60) {

            skillLevel = "Basic";

        } else if (overallScore < 75) {

            skillLevel = "Moderate";

        } else if (overallScore < 90) {

            skillLevel = "Advanced";

        } else {

            skillLevel = "Excellent";
        }

        // -----------------------------------------
        // STRONGEST AREA
        // -----------------------------------------

        String strongestArea;
        int strongestScore;

        if (problemSolvingScore >= knowledgeScore &&
                problemSolvingScore >= applicationScore &&
                problemSolvingScore >= debuggingScore) {

            strongestArea = "Problem Solving";
            strongestScore = problemSolvingScore;

        } else if (knowledgeScore >= applicationScore &&
                knowledgeScore >= debuggingScore) {

            strongestArea = "Knowledge";
            strongestScore = knowledgeScore;

        } else if (applicationScore >= debuggingScore) {

            strongestArea = "Application";
            strongestScore = applicationScore;

        } else {

            strongestArea = "Debugging";
            strongestScore = debuggingScore;
        }

        // -----------------------------------------
        // DEVELOPMENT AREA
        // -----------------------------------------

        String developmentArea;
        int developmentScore;

        if (debuggingScore <= applicationScore &&
                debuggingScore <= knowledgeScore &&
                debuggingScore <= problemSolvingScore) {

            developmentArea = "Debugging";
            developmentScore = debuggingScore;

        } else if (applicationScore <= knowledgeScore &&
                applicationScore <= problemSolvingScore) {

            developmentArea = "Application";
            developmentScore = applicationScore;

        } else if (knowledgeScore <= problemSolvingScore) {

            developmentArea = "Knowledge";
            developmentScore = knowledgeScore;

        } else {

            developmentArea = "Problem Solving";
            developmentScore = problemSolvingScore;
        }

        // -----------------------------------------
        // SEND DATA TO THYMELEAF
        // -----------------------------------------

        model.addAttribute(
                "skill",
                skill
        );

        model.addAttribute(
                "score",
                overallScore
        );

        model.addAttribute(
                "averageScore",
                overallScore
        );

        model.addAttribute(
                "knowledgeScore",
                knowledgeScore
        );

        model.addAttribute(
                "problemSolvingScore",
                problemSolvingScore
        );

        model.addAttribute(
                "applicationScore",
                applicationScore
        );

        model.addAttribute(
                "debuggingScore",
                debuggingScore
        );

        model.addAttribute(
                "confidenceScore",
                confidenceScore
        );

        model.addAttribute(
                "skillLevel",
                skillLevel
        );

        model.addAttribute(
                "strongestArea",
                strongestArea
        );

        model.addAttribute(
                "strongestScore",
                strongestScore
        );

        model.addAttribute(
                "developmentArea",
                developmentArea
        );

        model.addAttribute(
                "developmentScore",
                developmentScore
        );

        return "skill-intelligence";
    }
}