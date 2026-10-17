package com.careeriq.model;

import jakarta.persistence.*;

@Entity
@Table(name = "test_results")
public class TestResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String skill;

    private int score;

    private int correctAnswers;

    private int totalQuestions;

    private String skillLevel;

    private int violations;

    private long timeTakenSeconds;

    private int knowledgeScore;

    private int applicationScore;

    private int debuggingScore;

    private int problemSolvingScore;


    // =========================
    // CONSTRUCTOR
    // =========================

    public TestResult() {
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


    public int getScore() {
        return score;
    }


    public void setScore(int score) {
        this.score = score;
    }


    public int getCorrectAnswers() {
        return correctAnswers;
    }


    public void setCorrectAnswers(int correctAnswers) {
        this.correctAnswers = correctAnswers;
    }


    public int getTotalQuestions() {
        return totalQuestions;
    }


    public void setTotalQuestions(int totalQuestions) {
        this.totalQuestions = totalQuestions;
    }


    public String getSkillLevel() {
        return skillLevel;
    }


    public void setSkillLevel(String skillLevel) {
        this.skillLevel = skillLevel;
    }


    public int getViolations() {
        return violations;
    }


    public void setViolations(int violations) {
        this.violations = violations;
    }


    public long getTimeTakenSeconds() {
        return timeTakenSeconds;
    }


    public void setTimeTakenSeconds(long timeTakenSeconds) {
        this.timeTakenSeconds = timeTakenSeconds;
    }


    // =========================
    // INTELLIGENCE SCORES
    // =========================

    public int getKnowledgeScore() {
        return knowledgeScore;
    }


    public void setKnowledgeScore(int knowledgeScore) {
        this.knowledgeScore = knowledgeScore;
    }


    public int getApplicationScore() {
        return applicationScore;
    }


    public void setApplicationScore(int applicationScore) {
        this.applicationScore = applicationScore;
    }


    public int getDebuggingScore() {
        return debuggingScore;
    }


    public void setDebuggingScore(int debuggingScore) {
        this.debuggingScore = debuggingScore;
    }


    public int getProblemSolvingScore() {
        return problemSolvingScore;
    }


    public void setProblemSolvingScore(
            int problemSolvingScore) {

        this.problemSolvingScore =
                problemSolvingScore;
    }
}