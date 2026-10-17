package com.careeriq.service;

import com.careeriq.model.Question;
import com.careeriq.repository.QuestionRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class AssessmentService {

    private final QuestionRepository questionRepository;

    public AssessmentService(
            QuestionRepository questionRepository) {

        this.questionRepository =
                questionRepository;
    }

    /*
     * ---------------------------------------------------------
     * BALANCED QUESTIONS
     * ---------------------------------------------------------
     */

    public List<Question> getBalancedQuestions(
            String skill,
            int easyCount,
            int mediumCount,
            int hardCount) {

        List<Question> easyQuestions =
                questionRepository
                        .findBySkillAndDifficulty(
                                skill,
                                "Easy"
                        );

        List<Question> mediumQuestions =
                questionRepository
                        .findBySkillAndDifficulty(
                                skill,
                                "Medium"
                        );

        List<Question> hardQuestions =
                questionRepository
                        .findBySkillAndDifficulty(
                                skill,
                                "Hard"
                        );

        Collections.shuffle(easyQuestions);
        Collections.shuffle(mediumQuestions);
        Collections.shuffle(hardQuestions);

        List<Question> selectedQuestions =
                new ArrayList<>();

        selectedQuestions.addAll(
                easyQuestions.subList(
                        0,
                        Math.min(
                                easyCount,
                                easyQuestions.size()
                        )
                )
        );

        selectedQuestions.addAll(
                mediumQuestions.subList(
                        0,
                        Math.min(
                                mediumCount,
                                mediumQuestions.size()
                        )
                )
        );

        selectedQuestions.addAll(
                hardQuestions.subList(
                        0,
                        Math.min(
                                hardCount,
                                hardQuestions.size()
                        )
                )
        );

        Collections.shuffle(selectedQuestions);

        return selectedQuestions;
    }

    /*
     * ---------------------------------------------------------
     * GET ALL QUESTIONS
     * ---------------------------------------------------------
     */

    public List<Question> getAllQuestions() {

        return questionRepository.findAll();
    }

    /*
     * ---------------------------------------------------------
     * GET QUESTION BY ID
     * ---------------------------------------------------------
     */

    public Question getQuestionById(Long id) {

        return questionRepository
                .findById(id)
                .orElse(null);
    }

    /*
     * ---------------------------------------------------------
     * ADAPTIVE QUESTION
     * ---------------------------------------------------------
     */

    public Question getAdaptiveQuestion(
            String skill,
            String difficulty,
            List<Long> usedQuestionIds) {

        List<Question> questions =
                questionRepository
                        .findBySkillAndDifficulty(
                                skill,
                                difficulty
                        );

        List<Question> availableQuestions =
                new ArrayList<>();

        for (Question question :
                questions) {

            if (usedQuestionIds == null ||
                    !usedQuestionIds.contains(
                            question.getId()
                    )) {

                availableQuestions.add(
                        question
                );
            }
        }

        if (availableQuestions.isEmpty()) {

            return null;
        }

        Collections.shuffle(
                availableQuestions
        );

        return availableQuestions.get(0);
    }

    /*
     * ---------------------------------------------------------
     * ADAPTIVE QUESTION WITH DIFFICULTY FALLBACK
     * ---------------------------------------------------------
     */

    public Question getAdaptiveQuestionWithFallback(
            String skill,
            String difficulty,
            List<Long> usedQuestionIds) {

        Question question =
                getAdaptiveQuestion(
                        skill,
                        difficulty,
                        usedQuestionIds
                );

        if (question != null) {

            return question;
        }

        if ("Easy".equalsIgnoreCase(
                difficulty)) {

            question =
                    getAdaptiveQuestion(
                            skill,
                            "Medium",
                            usedQuestionIds
                    );

            if (question != null) {
                return question;
            }

            question =
                    getAdaptiveQuestion(
                            skill,
                            "Hard",
                            usedQuestionIds
                    );

            if (question != null) {
                return question;
            }
        }

        if ("Medium".equalsIgnoreCase(
                difficulty)) {

            question =
                    getAdaptiveQuestion(
                            skill,
                            "Easy",
                            usedQuestionIds
                    );

            if (question != null) {
                return question;
            }

            question =
                    getAdaptiveQuestion(
                            skill,
                            "Hard",
                            usedQuestionIds
                    );

            if (question != null) {
                return question;
            }
        }

        if ("Hard".equalsIgnoreCase(
                difficulty)) {

            question =
                    getAdaptiveQuestion(
                            skill,
                            "Medium",
                            usedQuestionIds
                    );

            if (question != null) {
                return question;
            }

            question =
                    getAdaptiveQuestion(
                            skill,
                            "Easy",
                            usedQuestionIds
                    );

            if (question != null) {
                return question;
            }
        }

        return null;
    }

    /*
     * ---------------------------------------------------------
     * GET QUESTION BY TYPE + DIFFICULTY
     * ---------------------------------------------------------
     */

    public Question getAdaptiveQuestionByType(
            String skill,
            String difficulty,
            String questionType,
            List<Long> usedQuestionIds) {

        List<Question> questions =
                questionRepository
                        .findBySkillAndDifficultyAndQuestionType(
                                skill,
                                difficulty,
                                questionType
                        );

        List<Question> availableQuestions =
                new ArrayList<>();

        for (Question question :
                questions) {

            if (usedQuestionIds == null ||
                    !usedQuestionIds.contains(
                            question.getId()
                    )) {

                availableQuestions.add(
                        question
                );
            }
        }

        if (availableQuestions.isEmpty()) {

            return null;
        }

        Collections.shuffle(
                availableQuestions
        );

        return availableQuestions.get(0);
    }

    /*
     * ---------------------------------------------------------
     * GET QUESTION BY TYPE WITH DIFFICULTY FALLBACK
     *
     * If the requested type is not available at the current
     * difficulty, try another difficulty.
     * ---------------------------------------------------------
     */

    public Question getAdaptiveQuestionByTypeWithFallback(
            String skill,
            String difficulty,
            String questionType,
            List<Long> usedQuestionIds) {

        Question question =
                getAdaptiveQuestionByType(
                        skill,
                        difficulty,
                        questionType,
                        usedQuestionIds
                );

        if (question != null) {

            return question;
        }

        if ("Easy".equalsIgnoreCase(
                difficulty)) {

            question =
                    getAdaptiveQuestionByType(
                            skill,
                            "Medium",
                            questionType,
                            usedQuestionIds
                    );

            if (question != null) {
                return question;
            }

            question =
                    getAdaptiveQuestionByType(
                            skill,
                            "Hard",
                            questionType,
                            usedQuestionIds
                    );

            if (question != null) {
                return question;
            }
        }

        else if ("Medium".equalsIgnoreCase(
                difficulty)) {

            question =
                    getAdaptiveQuestionByType(
                            skill,
                            "Easy",
                            questionType,
                            usedQuestionIds
                    );

            if (question != null) {
                return question;
            }

            question =
                    getAdaptiveQuestionByType(
                            skill,
                            "Hard",
                            questionType,
                            usedQuestionIds
                    );

            if (question != null) {
                return question;
            }
        }

        else if ("Hard".equalsIgnoreCase(
                difficulty)) {

            question =
                    getAdaptiveQuestionByType(
                            skill,
                            "Medium",
                            questionType,
                            usedQuestionIds
                    );

            if (question != null) {
                return question;
            }

            question =
                    getAdaptiveQuestionByType(
                            skill,
                            "Easy",
                            questionType,
                            usedQuestionIds
                    );

            if (question != null) {
                return question;
            }
        }

        return null;
    }
}