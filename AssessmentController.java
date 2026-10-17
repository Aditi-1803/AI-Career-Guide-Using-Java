package com.careeriq.controller;

import com.careeriq.model.Question;
import com.careeriq.model.TestResult;
import com.careeriq.repository.TestResultRepository;
import com.careeriq.service.AssessmentAnalyticsService;
import com.careeriq.service.AssessmentService;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class AssessmentController {

    private final AssessmentService assessmentService;

    private final TestResultRepository testResultRepository;

    private final AssessmentAnalyticsService
            assessmentAnalyticsService;

    public AssessmentController(
            AssessmentService assessmentService,
            TestResultRepository testResultRepository,
            AssessmentAnalyticsService
                    assessmentAnalyticsService) {

        this.assessmentService =
                assessmentService;

        this.testResultRepository =
                testResultRepository;

        this.assessmentAnalyticsService =
                assessmentAnalyticsService;
    }

    /*
     * =========================================================
     * START ASSESSMENT
     * =========================================================
     */

    @GetMapping("/assessment/{skill}")
    public String startAssessment(
            @PathVariable String skill,
            HttpSession session) {

        /*
         * First question starts at Easy difficulty.
         */
        Question firstQuestion =
                assessmentService
                        .getAdaptiveQuestionWithFallback(
                                skill,
                                "Easy",
                                new ArrayList<>()
                        );

        if (firstQuestion == null) {

            return "redirect:/dashboard";
        }

        /*
         * -----------------------------------------------------
         * Question list
         * -----------------------------------------------------
         */

        List<Question> questions =
                new ArrayList<>();

        questions.add(
                firstQuestion
        );

        /*
         * -----------------------------------------------------
         * Used question IDs
         * -----------------------------------------------------
         */

        List<Long> usedQuestionIds =
                new ArrayList<>();

        usedQuestionIds.add(
                firstQuestion.getId()
        );

        /*
         * -----------------------------------------------------
         * Assessment basic information
         * -----------------------------------------------------
         */

        session.setAttribute(
                "assessmentSkill",
                skill
        );

        session.setAttribute(
                "assessmentQuestions",
                questions
        );

        session.setAttribute(
                "usedQuestionIds",
                usedQuestionIds
        );

        /*
         * -----------------------------------------------------
         * Answers
         * -----------------------------------------------------
         */

        session.setAttribute(
                "assessmentAnswers",
                new HashMap<Long, String>()
        );

        /*
         * -----------------------------------------------------
         * Evaluated questions
         * -----------------------------------------------------
         */

        session.setAttribute(
                "evaluatedQuestionIds",
                new ArrayList<Long>()
        );

        /*
         * -----------------------------------------------------
         * Current question
         * -----------------------------------------------------
         */

        session.setAttribute(
                "currentQuestionIndex",
                0
        );

        /*
         * -----------------------------------------------------
         * Timer
         * -----------------------------------------------------
         */

        session.setAttribute(
                "assessmentStartTime",
                System.currentTimeMillis()
        );

        /*
         * -----------------------------------------------------
         * Adaptive difficulty
         * -----------------------------------------------------
         */

        session.setAttribute(
                "currentDifficulty",
                "Easy"
        );

        session.setAttribute(
                "adaptiveCorrect",
                0
        );

        session.setAttribute(
                "adaptiveAnswered",
                0
        );

        /*
         * -----------------------------------------------------
         * Anti-cheating violations
         * -----------------------------------------------------
         */

        session.setAttribute(
                "assessmentViolations",
                0
        );

        session.setAttribute(
                "assessmentActive",
                true
        );

        /*
         * -----------------------------------------------------
         * QUESTION TYPE TARGETS
         *
         * Total = 10 questions
         *
         * Knowledge       = 3
         * Application     = 2
         * Debugging       = 2
         * Problem Solving = 2
         * Scenario        = 1
         * -----------------------------------------------------
         */

        Map<String, Integer> questionTypeTargets =
                new HashMap<>();

        questionTypeTargets.put(
                "Knowledge",
                3
        );

        questionTypeTargets.put(
                "Application",
                2
        );

        questionTypeTargets.put(
                "Debugging",
                2
        );

        questionTypeTargets.put(
                "Problem Solving",
                2
        );

        questionTypeTargets.put(
                "Scenario",
                1
        );

        session.setAttribute(
                "questionTypeTargets",
                questionTypeTargets
        );

        /*
         * -----------------------------------------------------
         * QUESTION TYPE COUNTERS
         * -----------------------------------------------------
         */

        Map<String, Integer> questionTypeCounts =
                new HashMap<>();

        questionTypeCounts.put(
                "Knowledge",
                0
        );

        questionTypeCounts.put(
                "Application",
                0
        );

        questionTypeCounts.put(
                "Debugging",
                0
        );

        questionTypeCounts.put(
                "Problem Solving",
                0
        );

        questionTypeCounts.put(
                "Scenario",
                0
        );

        session.setAttribute(
                "questionTypeCounts",
                questionTypeCounts
        );

        /*
         * Count the first question.
         */
        markQuestionTypeUsed(
                firstQuestion,
                session
        );

        return "redirect:/assessment/question";
    }

    /*
     * =========================================================
     * SHOW QUESTION
     * =========================================================
     */

    @GetMapping("/assessment/question")
    public String showQuestion(
            HttpSession session,
            Model model) {

        List<Question> questions =
                (List<Question>)
                        session.getAttribute(
                                "assessmentQuestions"
                        );

        Integer currentIndex =
                (Integer)
                        session.getAttribute(
                                "currentQuestionIndex"
                        );

        String skill =
                (String)
                        session.getAttribute(
                                "assessmentSkill"
                        );

        if (questions == null ||
                currentIndex == null ||
                skill == null) {

            return "redirect:/assessment/Java";
        }

        /*
         * Check whether assessment is active.
         */

        Boolean assessmentActive =
                (Boolean)
                        session.getAttribute(
                                "assessmentActive"
                        );

        if (assessmentActive == null ||
                !assessmentActive) {

            return "redirect:/assessment/Java";
        }

        /*
         * Check question index.
         */

        if (currentIndex >= questions.size()) {

            return "redirect:/assessment/complete";
        }

        Question question =
                questions.get(
                        currentIndex
                );

        /*
         * -----------------------------------------------------
         * Send data to Thymeleaf page
         * -----------------------------------------------------
         */

        model.addAttribute(
                "skill",
                skill
        );

        model.addAttribute(
                "question",
                question
        );

        model.addAttribute(
                "questionNumber",
                currentIndex + 1
        );

        model.addAttribute(
                "totalQuestions",
                10
        );

        /*
         * -----------------------------------------------------
         * Previously selected answer
         * -----------------------------------------------------
         */

        Map<Long, String> answers =
                (Map<Long, String>)
                        session.getAttribute(
                                "assessmentAnswers"
                        );

        if (answers == null) {

            answers =
                    new HashMap<>();
        }

        String selectedAnswer =
                answers.get(
                        question.getId()
                );

        model.addAttribute(
                "selectedAnswer",
                selectedAnswer
        );

        /*
         * -----------------------------------------------------
         * Current difficulty
         * -----------------------------------------------------
         */

        String currentDifficulty =
                (String)
                        session.getAttribute(
                                "currentDifficulty"
                        );

        if (currentDifficulty == null) {

            currentDifficulty =
                    question.getDifficulty();
        }

        /*
         * Always display the actual difficulty
         * of the question being shown.
         */

        if (question.getDifficulty() != null) {

            currentDifficulty =
                    question.getDifficulty();
        }

        model.addAttribute(
                "currentDifficulty",
                currentDifficulty
        );

        /*
         * -----------------------------------------------------
         * Violations
         * -----------------------------------------------------
         */

        Integer violations =
                (Integer)
                        session.getAttribute(
                                "assessmentViolations"
                        );

        if (violations == null) {

            violations = 0;
        }

        model.addAttribute(
                "violations",
                violations
        );

        /*
         * -----------------------------------------------------
         * Timer
         * -----------------------------------------------------
         */

        Long startTime =
                (Long)
                        session.getAttribute(
                                "assessmentStartTime"
                        );

        if (startTime == null) {

            return "redirect:/assessment/Java";
        }

        long totalTime =
                10 * 60 * 1000L;

        long elapsed =
                System.currentTimeMillis()
                        - startTime;

        long remaining =
                Math.max(
                        0,
                        totalTime - elapsed
                );

        if (remaining <= 0) {

            return "redirect:/assessment/complete";
        }

        model.addAttribute(
                "remainingTime",
                remaining
        );

        return "assessment-question";
    }

    /*
     * =========================================================
     * PROCESS ANSWER
     * =========================================================
     */

    @PostMapping("/assessment/question")
    public String processAnswer(
            @RequestParam("questionId")
            Long questionId,

            @RequestParam(
                    value = "answer",
                    required = false
            )
            String answer,

            @RequestParam("action")
            String action,

            HttpSession session) {

        /*
         * -----------------------------------------------------
         * Check timer
         * -----------------------------------------------------
         */

        Long startTime =
                (Long)
                        session.getAttribute(
                                "assessmentStartTime"
                        );

        if (startTime == null) {

            return "redirect:/assessment/Java";
        }

        long totalTime =
                10 * 60 * 1000L;

        long elapsedTime =
                System.currentTimeMillis()
                        - startTime;

        if (elapsedTime >= totalTime) {

            return "redirect:/assessment/complete";
        }

        /*
         * -----------------------------------------------------
         * Check assessment status
         * -----------------------------------------------------
         */

        Boolean assessmentActive =
                (Boolean)
                        session.getAttribute(
                                "assessmentActive"
                        );

        if (assessmentActive == null ||
                !assessmentActive) {

            return "redirect:/assessment/Java";
        }

        /*
         * -----------------------------------------------------
         * Get session data
         * -----------------------------------------------------
         */

        List<Question> questions =
                (List<Question>)
                        session.getAttribute(
                                "assessmentQuestions"
                        );

        List<Long> usedQuestionIds =
                (List<Long>)
                        session.getAttribute(
                                "usedQuestionIds"
                        );

        Integer currentIndex =
                (Integer)
                        session.getAttribute(
                                "currentQuestionIndex"
                        );

        Map<Long, String> answers =
                (Map<Long, String>)
                        session.getAttribute(
                                "assessmentAnswers"
                        );

        String skill =
                (String)
                        session.getAttribute(
                                "assessmentSkill"
                        );

        if (questions == null ||
                usedQuestionIds == null ||
                currentIndex == null ||
                answers == null ||
                skill == null) {

            return "redirect:/assessment/Java";
        }

        /*
         * -----------------------------------------------------
         * Validate current index
         * -----------------------------------------------------
         */

        if (currentIndex < 0 ||
                currentIndex >= questions.size()) {

            return "redirect:/assessment/question";
        }

        Question currentQuestion =
                questions.get(
                        currentIndex
                );

        /*
         * -----------------------------------------------------
         * Security check
         * -----------------------------------------------------
         */

        if (!currentQuestion
                .getId()
                .equals(questionId)) {

            return "redirect:/assessment/question";
        }

        /*
         * -----------------------------------------------------
         * Save answer
         * -----------------------------------------------------
         */

        if (answer != null &&
                !answer.trim().isEmpty()) {

            answers.put(
                    questionId,
                    answer
            );

            session.setAttribute(
                    "assessmentAnswers",
                    answers
            );
        }

        /*
         * =====================================================
         * PREVIOUS
         * =====================================================
         */

        if ("previous".equals(action)) {

            if (currentIndex > 0) {

                currentIndex--;

                session.setAttribute(
                        "currentQuestionIndex",
                        currentIndex
                );
            }

            return "redirect:/assessment/question";
        }

        /*
         * =====================================================
         * NEXT
         * =====================================================
         */

        if ("next".equals(action)) {

            /*
             * Evaluate current question only once.
             */

            if (answer != null &&
                    !answer.trim().isEmpty()) {

                boolean alreadyEvaluated =
                        isQuestionAlreadyEvaluated(
                                currentQuestion,
                                session
                        );

                if (!alreadyEvaluated) {

                    updateAdaptiveDifficulty(
                            currentQuestion,
                            answer,
                            session
                    );

                    markQuestionEvaluated(
                            currentQuestion,
                            session
                    );
                }
            }

            /*
             * Continue until 10 questions.
             */

            if (questions.size() < 10) {

                String nextDifficulty =
                        (String)
                                session.getAttribute(
                                        "currentDifficulty"
                                );

                /*
                 * Determine which question type
                 * is needed next.
                 */

                String nextQuestionType =
                        getNextQuestionType(
                                session
                        );

                /*
                 * Try to get the requested type
                 * at the adaptive difficulty.
                 *
                 * If unavailable, try another
                 * difficulty while keeping the
                 * same question type.
                 */

                Question nextQuestion =
                        assessmentService
                                .getAdaptiveQuestionByTypeWithFallback(
                                        skill,
                                        nextDifficulty,
                                        nextQuestionType,
                                        usedQuestionIds
                                );

                /*
                 * If that question type is exhausted,
                 * use normal adaptive fallback so
                 * the assessment does not stop early.
                 */

                if (nextQuestion == null) {

                    nextQuestion =
                            assessmentService
                                    .getAdaptiveQuestionWithFallback(
                                            skill,
                                            nextDifficulty,
                                            usedQuestionIds
                                    );
                }

                if (nextQuestion != null) {

                    questions.add(
                            nextQuestion
                    );

                    usedQuestionIds.add(
                            nextQuestion.getId()
                    );

                    /*
                     * Count the selected type.
                     */

                    markQuestionTypeUsed(
                            nextQuestion,
                            session
                    );

                    session.setAttribute(
                            "assessmentQuestions",
                            questions
                    );

                    session.setAttribute(
                            "usedQuestionIds",
                            usedQuestionIds
                    );

                    currentIndex++;

                    session.setAttribute(
                            "currentQuestionIndex",
                            currentIndex
                    );

                    return "redirect:/assessment/question";
                }
            }

            return "redirect:/assessment/complete";
        }

        /*
         * =====================================================
         * SUBMIT
         * =====================================================
         */

        if ("submit".equals(action)) {

            /*
             * Evaluate final question.
             */

            if (answer != null &&
                    !answer.trim().isEmpty()) {

                boolean alreadyEvaluated =
                        isQuestionAlreadyEvaluated(
                                currentQuestion,
                                session
                        );

                if (!alreadyEvaluated) {

                    updateAdaptiveDifficulty(
                            currentQuestion,
                            answer,
                            session
                    );

                    markQuestionEvaluated(
                            currentQuestion,
                            session
                    );
                }
            }

            return "redirect:/assessment/complete";
        }

        return "redirect:/assessment/question";
    }

    /*
     * =========================================================
     * CHECK WHETHER QUESTION WAS ALREADY EVALUATED
     * =========================================================
     */

    private boolean isQuestionAlreadyEvaluated(
            Question question,
            HttpSession session) {

        List<Long> evaluatedQuestionIds =
                (List<Long>)
                        session.getAttribute(
                                "evaluatedQuestionIds"
                        );

        if (evaluatedQuestionIds == null) {

            return false;
        }

        return evaluatedQuestionIds.contains(
                question.getId()
        );
    }

    /*
     * =========================================================
     * MARK QUESTION AS EVALUATED
     * =========================================================
     */

    private void markQuestionEvaluated(
            Question question,
            HttpSession session) {

        List<Long> evaluatedQuestionIds =
                (List<Long>)
                        session.getAttribute(
                                "evaluatedQuestionIds"
                        );

        if (evaluatedQuestionIds == null) {

            evaluatedQuestionIds =
                    new ArrayList<>();
        }

        if (!evaluatedQuestionIds.contains(
                question.getId()
        )) {

            evaluatedQuestionIds.add(
                    question.getId()
            );
        }

        session.setAttribute(
                "evaluatedQuestionIds",
                evaluatedQuestionIds
        );
    }

    /*
     * =========================================================
     * UPDATE ADAPTIVE DIFFICULTY
     * =========================================================
     */

    private void updateAdaptiveDifficulty(
            Question question,
            String answer,
            HttpSession session) {

        String currentDifficulty =
                (String)
                        session.getAttribute(
                                "currentDifficulty"
                        );

        if (currentDifficulty == null) {

            currentDifficulty =
                    "Easy";
        }

        Integer adaptiveCorrect =
                (Integer)
                        session.getAttribute(
                                "adaptiveCorrect"
                        );

        Integer adaptiveAnswered =
                (Integer)
                        session.getAttribute(
                                "adaptiveAnswered"
                        );

        if (adaptiveCorrect == null) {

            adaptiveCorrect = 0;
        }

        if (adaptiveAnswered == null) {

            adaptiveAnswered = 0;
        }

        boolean correct =
                answer.equalsIgnoreCase(
                        question.getCorrectAnswer()
                );

        adaptiveAnswered++;

        if (correct) {

            adaptiveCorrect++;
        }

        String nextDifficulty =
                calculateNextDifficulty(
                        currentDifficulty,
                        correct
                );

        session.setAttribute(
                "currentDifficulty",
                nextDifficulty
        );

        session.setAttribute(
                "adaptiveCorrect",
                adaptiveCorrect
        );

        session.setAttribute(
                "adaptiveAnswered",
                adaptiveAnswered
        );
    }

    /*
     * =========================================================
     * CALCULATE NEXT DIFFICULTY
     * =========================================================
     */

    private String calculateNextDifficulty(
            String currentDifficulty,
            boolean correct) {

        if (correct) {

            if ("Easy".equalsIgnoreCase(
                    currentDifficulty)) {

                return "Medium";
            }

            if ("Medium".equalsIgnoreCase(
                    currentDifficulty)) {

                return "Hard";
            }

            return "Hard";
        }

        if ("Hard".equalsIgnoreCase(
                currentDifficulty)) {

            return "Medium";
        }

        if ("Medium".equalsIgnoreCase(
                currentDifficulty)) {

            return "Easy";
        }

        return "Easy";
    }

    /*
     * =========================================================
     * DETERMINE NEXT QUESTION TYPE
     * =========================================================
     */

    private String getNextQuestionType(
            HttpSession session) {

        Map<String, Integer> targets =
                (Map<String, Integer>)
                        session.getAttribute(
                                "questionTypeTargets"
                        );

        Map<String, Integer> counts =
                (Map<String, Integer>)
                        session.getAttribute(
                                "questionTypeCounts"
                        );

        if (targets == null ||
                counts == null) {

            return "Knowledge";
        }

        String selectedType =
                "Knowledge";

        int highestRemaining =
                -1;

        /*
         * Find the question type that still
         * needs the most questions.
         */

        for (String type :
                targets.keySet()) {

            int target =
                    targets.get(type);

            int used =
                    counts.getOrDefault(
                            type,
                            0
                    );

            int remaining =
                    target - used;

            if (remaining >
                    highestRemaining) {

                highestRemaining =
                        remaining;

                selectedType =
                        type;
            }
        }

        return selectedType;
    }

    /*
     * =========================================================
     * COUNT QUESTION TYPE
     * =========================================================
     */

    private void markQuestionTypeUsed(
            Question question,
            HttpSession session) {

        Map<String, Integer> counts =
                (Map<String, Integer>)
                        session.getAttribute(
                                "questionTypeCounts"
                        );

        if (counts == null) {

            counts =
                    new HashMap<>();
        }

        String type =
                question.getQuestionType();

        if (type == null ||
                type.trim().isEmpty()) {

            type = "Knowledge";
        }

        counts.put(
                type,
                counts.getOrDefault(
                        type,
                        0
                ) + 1
        );

        session.setAttribute(
                "questionTypeCounts",
                counts
        );
    }

    /*
     * =========================================================
     * RECORD ANTI-CHEATING VIOLATION
     * =========================================================
     */

    @PostMapping("/assessment/violation")
    @ResponseBody
    public String recordViolation(
            @RequestParam("reason")
            String reason,

            HttpSession session) {

        Boolean assessmentActive =
                (Boolean)
                        session.getAttribute(
                                "assessmentActive"
                        );

        if (assessmentActive == null ||
                !assessmentActive) {

            return "INVALID";
        }

        Integer violations =
                (Integer)
                        session.getAttribute(
                                "assessmentViolations"
                        );

        if (violations == null) {

            violations = 0;
        }

        violations++;

        session.setAttribute(
                "assessmentViolations",
                violations
        );

        /*
         * Three violations automatically
         * submit the assessment.
         */

        if (violations >= 3) {

            return "SUBMIT";
        }

        return String.valueOf(
                violations
        );
    }

    /*
     * =========================================================
     * COMPLETE ASSESSMENT
     * =========================================================
     */

    @GetMapping("/assessment/complete")
    public String assessmentComplete(
            HttpSession session,
            Model model) {

        Long startTime =
                (Long)
                        session.getAttribute(
                                "assessmentStartTime"
                        );

        if (startTime == null) {

            return "redirect:/assessment/Java";
        }

        Boolean assessmentActive =
                (Boolean)
                        session.getAttribute(
                                "assessmentActive"
                        );

        if (assessmentActive == null ||
                !assessmentActive) {

            return "redirect:/assessment/Java";
        }

        List<Question> questions =
                (List<Question>)
                        session.getAttribute(
                                "assessmentQuestions"
                        );

        Map<Long, String> answers =
                (Map<Long, String>)
                        session.getAttribute(
                                "assessmentAnswers"
                        );

        String skill =
                (String)
                        session.getAttribute(
                                "assessmentSkill"
                        );

        Integer violations =
                (Integer)
                        session.getAttribute(
                                "assessmentViolations"
                        );

        if (questions == null ||
                answers == null ||
                skill == null) {

            return "redirect:/assessment/Java";
        }

        if (violations == null) {

            violations = 0;
        }

        /*
         * -----------------------------------------------------
         * Calculate normal score
         * -----------------------------------------------------
         */

        int correctAnswers = 0;

        for (Question question :
                questions) {

            String selectedAnswer =
                    answers.get(
                            question.getId()
                    );

            if (selectedAnswer != null &&
                    selectedAnswer.equalsIgnoreCase(
                            question.getCorrectAnswer()
                    )) {

                correctAnswers++;
            }
        }

        int totalQuestions =
                questions.size();

        int score = 0;

        if (totalQuestions > 0) {

            score =
                    (correctAnswers * 100)
                            / totalQuestions;
        }

        /*
         * -----------------------------------------------------
         * Skill level
         * -----------------------------------------------------
         */

        String skillLevel;

        if (score < 40) {

            skillLevel =
                    "Beginner";

        } else if (score < 60) {

            skillLevel =
                    "Basic";

        } else if (score < 75) {

            skillLevel =
                    "Moderate";

        } else if (score < 90) {

            skillLevel =
                    "Advanced";

        } else {

            skillLevel =
                    "Excellent";
        }

        /*
         * -----------------------------------------------------
         * Time taken
         * -----------------------------------------------------
         */

        long timeTakenSeconds =
                (System.currentTimeMillis()
                        - startTime)
                        / 1000;

        timeTakenSeconds =
                Math.min(
                        timeTakenSeconds,
                        600
                );

        /*
         * -----------------------------------------------------
         * ANALYTICS
         * -----------------------------------------------------
         */

        AssessmentAnalyticsService.AnalyticsResult
                analyticsResult =

                assessmentAnalyticsService
                        .calculateAnalytics(
                                questions,
                                answers
                        );

        /*
         * -----------------------------------------------------
         * CREATE TEST RESULT
         * -----------------------------------------------------
         */

        TestResult testResult =
                new TestResult();

        testResult.setSkill(
                skill
        );

        testResult.setScore(
                score
        );

        testResult.setCorrectAnswers(
                correctAnswers
        );

        testResult.setTotalQuestions(
                totalQuestions
        );

        testResult.setSkillLevel(
                skillLevel
        );

        testResult.setViolations(
                violations
        );

        testResult.setTimeTakenSeconds(
                timeTakenSeconds
        );

        /*
         * Analytics scores
         */

        testResult.setKnowledgeScore(
                analyticsResult
                        .getKnowledgeScore()
        );

        testResult.setApplicationScore(
                analyticsResult
                        .getApplicationScore()
        );

        testResult.setDebuggingScore(
                analyticsResult
                        .getDebuggingScore()
        );

        testResult.setProblemSolvingScore(
                analyticsResult
                        .getProblemSolvingScore()
        );

        /*
         * -----------------------------------------------------
         * SAVE TO DATABASE
         * -----------------------------------------------------
         */

        testResultRepository.save(
                testResult
        );

        /*
         * -----------------------------------------------------
         * SEND DATA TO RESULT PAGE
         * -----------------------------------------------------
         */

        model.addAttribute(
                "skill",
                skill
        );

        model.addAttribute(
                "score",
                score
        );

        model.addAttribute(
                "correctAnswers",
                correctAnswers
        );

        model.addAttribute(
                "totalQuestions",
                totalQuestions
        );

        model.addAttribute(
                "skillLevel",
                skillLevel
        );

        model.addAttribute(
                "knowledgeScore",
                analyticsResult
                        .getKnowledgeScore()
        );

        model.addAttribute(
                "applicationScore",
                analyticsResult
                        .getApplicationScore()
        );

        model.addAttribute(
                "debuggingScore",
                analyticsResult
                        .getDebuggingScore()
        );

        model.addAttribute(
                "problemSolvingScore",
                analyticsResult
                        .getProblemSolvingScore()
        );

        model.addAttribute(
                "overallAnalyticsScore",
                analyticsResult
                        .getOverallScore()
        );

        /*
         * -----------------------------------------------------
         * End assessment
         * -----------------------------------------------------
         */

        session.setAttribute(
                "assessmentActive",
                false
        );

        return "result";
    }

    @GetMapping("/assessment")
    public String showAssessmentSelection() {
        return "assessment";
    }

}