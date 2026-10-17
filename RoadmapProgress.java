
package com.careeriq.model;

import jakarta.persistence.*;

@Entity
@Table(
        name = "roadmap_progress",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"session_key", "career", "step_title"}
        )
)
public class RoadmapProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "session_key", nullable = false, length = 100)
    private String sessionKey;

    @Column(nullable = false, length = 150)
    private String career;

    @Column(name = "step_title", nullable = false, length = 255)
    private String stepTitle;

    @Column(nullable = false)
    private boolean completed;

    public RoadmapProgress() {
    }

    public Long getId() {
        return id;
    }

    public String getSessionKey() {
        return sessionKey;
    }

    public void setSessionKey(String sessionKey) {
        this.sessionKey = sessionKey;
    }

    public String getCareer() {
        return career;
    }

    public void setCareer(String career) {
        this.career = career;
    }

    public String getStepTitle() {
        return stepTitle;
    }

    public void setStepTitle(String stepTitle) {
        this.stepTitle = stepTitle;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}
