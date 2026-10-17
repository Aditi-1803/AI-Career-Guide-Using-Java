package com.careeriq.model;

import jakarta.persistence.*;

@Entity
@Table(name = "questions")
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    // =========================
    // BASIC QUESTION INFORMATION
    // =========================

    @Column(name = "skill")
    private String skill;


    @Column(name = "question_text", columnDefinition = "TEXT")
    private String questionText;


    @Column(name = "optiona")
    private String optionA;


    @Column(name = "optionb")
    private String optionB;


    @Column(name = "optionc")
    private String optionC;


    @Column(name = "optiond")
    private String optionD;


    @Column(name = "correct_answer")
    private String correctAnswer;


    @Column(name = "difficulty")
    private String difficulty;


    // =========================
    // QUESTION INTELLIGENCE TYPE
    // =========================

    @Column(name = "question_type")
    private String questionType;


    // =========================
    // CONSTRUCTOR
    // =========================

    public Question() {
    }


    // =========================
    // GETTERS AND SETTERS
    // =========================

    public Long getId() {
        return id;
    }


    public String getSkill() {
        return skill;
    }


    public void setSkill(String skill) {
        this.skill = skill;
    }


    public String getQuestionText() {
        return questionText;
    }


    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }


    public String getOptionA() {
        return optionA;
    }


    public void setOptionA(String optionA) {
        this.optionA = optionA;
    }


    public String getOptionB() {
        return optionB;
    }


    public void setOptionB(String optionB) {
        this.optionB = optionB;
    }


    public String getOptionC() {
        return optionC;
    }


    public void setOptionC(String optionC) {
        this.optionC = optionC;
    }


    public String getOptionD() {
        return optionD;
    }


    public void setOptionD(String optionD) {
        this.optionD = optionD;
    }


    public String getCorrectAnswer() {
        return correctAnswer;
    }


    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }


    public String getDifficulty() {
        return difficulty;
    }


    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }


    // =========================
    // QUESTION TYPE
    // =========================

    public String getQuestionType() {
        return questionType;
    }


    public void setQuestionType(String questionType) {
        this.questionType = questionType;
    }
}