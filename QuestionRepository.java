package com.careeriq.repository;

import com.careeriq.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionRepository
        extends JpaRepository<Question, Long> {

    List<Question> findBySkill(String skill);

    List<Question> findBySkillAndDifficulty(
            String skill,
            String difficulty
    );

    List<Question> findBySkillAndDifficultyAndQuestionType(
            String skill,
            String difficulty,
            String questionType
    );

    List<Question> findBySkillAndQuestionType(
            String skill,
            String questionType
    );
}