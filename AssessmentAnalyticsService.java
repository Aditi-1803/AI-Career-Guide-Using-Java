package com.careeriq.service;

import com.careeriq.model.Question;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class AssessmentAnalyticsService {


    // =========================================
    // MAIN ANALYTICS METHOD
    // =========================================

    public AnalyticsResult calculateAnalytics(
            List<Question> questions,
            Map<Long, String> answers) {


        if (questions == null ||
                questions.isEmpty()) {

            return new AnalyticsResult(
                    0,
                    0,
                    0,
                    0,
                    0
            );
        }


        int knowledgeCorrect = 0;
        int knowledgeTotal = 0;

        int applicationCorrect = 0;
        int applicationTotal = 0;

        int debuggingCorrect = 0;
        int debuggingTotal = 0;

        int problemSolvingCorrect = 0;
        int problemSolvingTotal = 0;


        // =========================================
        // ANALYZE EVERY QUESTION
        // =========================================

        for (Question question : questions) {


            String selectedAnswer =
                    answers.get(question.getId());


            boolean correct =
                    selectedAnswer != null &&
                            selectedAnswer.equalsIgnoreCase(
                                    question.getCorrectAnswer()
                            );


            String type =
                    question.getQuestionType();


            if (type == null ||
                    type.trim().isEmpty()) {

                type = "Knowledge";
            }


            // =========================================
            // KNOWLEDGE
            // =========================================

            if (type.equalsIgnoreCase("Knowledge")) {

                knowledgeTotal++;

                if (correct) {
                    knowledgeCorrect++;
                }
            }


            // =========================================
            // APPLICATION
            // =========================================

            else if (
                    type.equalsIgnoreCase("Application")
            ) {

                applicationTotal++;

                if (correct) {
                    applicationCorrect++;
                }
            }


            // =========================================
            // DEBUGGING
            // =========================================

            else if (
                    type.equalsIgnoreCase("Debugging")
            ) {

                debuggingTotal++;

                if (correct) {
                    debuggingCorrect++;
                }
            }


            // =========================================
            // PROBLEM SOLVING
            // =========================================

            else if (
                    type.equalsIgnoreCase(
                            "Problem Solving"
                    )
            ) {

                problemSolvingTotal++;

                if (correct) {
                    problemSolvingCorrect++;
                }
            }


            // =========================================
            // SCENARIO QUESTIONS
            // =========================================

            else if (
                    type.equalsIgnoreCase("Scenario")
            ) {

                /*
                 * Scenario questions are currently
                 * counted as Application questions.
                 */

                applicationTotal++;

                if (correct) {
                    applicationCorrect++;
                }
            }


            // =========================================
            // UNKNOWN TYPE
            // =========================================

            else {

                knowledgeTotal++;

                if (correct) {
                    knowledgeCorrect++;
                }
            }
        }


        // =========================================
        // CALCULATE CATEGORY SCORES
        // =========================================

        int knowledgeScore =
                calculatePercentage(
                        knowledgeCorrect,
                        knowledgeTotal
                );


        int applicationScore =
                calculatePercentage(
                        applicationCorrect,
                        applicationTotal
                );


        int debuggingScore =
                calculatePercentage(
                        debuggingCorrect,
                        debuggingTotal
                );


        int problemSolvingScore =
                calculatePercentage(
                        problemSolvingCorrect,
                        problemSolvingTotal
                );


        // =========================================
        // OVERALL SCORE
        // =========================================

        int totalCorrect =
                knowledgeCorrect
                        + applicationCorrect
                        + debuggingCorrect
                        + problemSolvingCorrect;


        int totalQuestions =
                knowledgeTotal
                        + applicationTotal
                        + debuggingTotal
                        + problemSolvingTotal;


        int overallScore =
                calculatePercentage(
                        totalCorrect,
                        totalQuestions
                );


        return new AnalyticsResult(
                overallScore,
                knowledgeScore,
                applicationScore,
                debuggingScore,
                problemSolvingScore
        );
    }


    // =========================================
    // PERCENTAGE CALCULATION
    // =========================================

    private int calculatePercentage(
            int correct,
            int total) {

        if (total <= 0) {
            return 0;
        }

        return
                (correct * 100) / total;
    }


    // =========================================
    // ANALYTICS RESULT CLASS
    // =========================================

    public static class AnalyticsResult {

        private final int overallScore;

        private final int knowledgeScore;

        private final int applicationScore;

        private final int debuggingScore;

        private final int problemSolvingScore;


        public AnalyticsResult(
                int overallScore,
                int knowledgeScore,
                int applicationScore,
                int debuggingScore,
                int problemSolvingScore) {

            this.overallScore =
                    overallScore;

            this.knowledgeScore =
                    knowledgeScore;

            this.applicationScore =
                    applicationScore;

            this.debuggingScore =
                    debuggingScore;

            this.problemSolvingScore =
                    problemSolvingScore;
        }


        public int getOverallScore() {

            return overallScore;
        }


        public int getKnowledgeScore() {

            return knowledgeScore;
        }


        public int getApplicationScore() {

            return applicationScore;
        }


        public int getDebuggingScore() {

            return debuggingScore;
        }


        public int getProblemSolvingScore() {

            return problemSolvingScore;
        }
    }
}