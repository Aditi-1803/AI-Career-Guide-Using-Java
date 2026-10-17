
package com.careeriq.repository;

import com.careeriq.model.RoadmapProgress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoadmapProgressRepository
        extends JpaRepository<RoadmapProgress, Long> {

    List<RoadmapProgress> findBySessionKeyAndCareer(
            String sessionKey,
            String career
    );

    Optional<RoadmapProgress> findBySessionKeyAndCareerAndStepTitle(
            String sessionKey,
            String career,
            String stepTitle
    );
}

