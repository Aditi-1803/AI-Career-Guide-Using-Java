package com.careeriq.repository;

import com.careeriq.model.TestResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TestResultRepository
        extends JpaRepository<TestResult, Long> {

    List<TestResult> findBySkill(String skill);

    List<TestResult> findBySkillOrderByIdDesc(String skill);

    TestResult findFirstBySkillOrderByIdDesc(String skill);
}