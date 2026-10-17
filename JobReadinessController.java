package com.careeriq.controller;

import com.careeriq.model.TestResult;
import com.careeriq.repository.TestResultRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class JobReadinessController {

    private final TestResultRepository testResultRepository;

    public JobReadinessController(
            TestResultRepository testResultRepository) {

        this.testResultRepository =
                testResultRepository;
    }

    @GetMapping("/job-readiness")
    public String showJobReadiness(Model model) {

        List<TestResult> results =
                testResultRepository.findAll();

        int technicalSkills =
                calculateTechnicalSkills(results);

        int problemSolving =
                calculateProblemSolving(results);

        int databaseSkills =
                getSkillScore(results, "SQL");

        int programmingSkills =
                calculateProgrammingSkills(results);

        int skillCoverage =
                calculateSkillCoverage(results);

        int overallReadiness =
                calculateOverallReadiness(
                        technicalSkills,
                        problemSolving,
                        databaseSkills,
                        programmingSkills,
                        skillCoverage
                );

        String[] blockers =
                getBlockers(results);

        String nextAction =
                getNextAction(results);

        model.addAttribute(
                "technicalSkills",
                technicalSkills
        );

        model.addAttribute(
                "problemSolving",
                problemSolving
        );

        model.addAttribute(
                "databaseSkills",
                databaseSkills
        );

        model.addAttribute(
                "programmingSkills",
                programmingSkills
        );

        model.addAttribute(
                "skillCoverage",
                skillCoverage
        );

        model.addAttribute(
                "overallReadiness",
                overallReadiness
        );

        model.addAttribute(
                "blockers",
                blockers
        );

        model.addAttribute(
                "nextAction",
                nextAction
        );

        return "job-readiness";
    }


    /*
     * ==========================================
     * TECHNICAL SKILLS
     * ==========================================
     */

    private int calculateTechnicalSkills(
            List<TestResult> results) {

        int java =
                getSkillScore(results, "Java");

        int python =
                getSkillScore(results, "Python");

        int javascript =
                getSkillScore(results, "JavaScript");

        int cpp =
                getSkillScore(results, "C/C++");

        return calculateAverage(
                java,
                python,
                javascript,
                cpp
        );
    }


    /*
     * ==========================================
     * PROBLEM SOLVING
     * ==========================================
     */

    private int calculateProblemSolving(
            List<TestResult> results) {

        int dsa =
                getSkillScore(results, "DSA");

        int java =
                getSkillScore(results, "Java");

        int python =
                getSkillScore(results, "Python");

        return calculateAverage(
                dsa,
                java,
                python
        );
    }


    /*
     * ==========================================
     * PROGRAMMING SKILLS
     * ==========================================
     */

    private int calculateProgrammingSkills(
            List<TestResult> results) {

        int java =
                getSkillScore(results, "Java");

        int python =
                getSkillScore(results, "Python");

        int cpp =
                getSkillScore(results, "C/C++");

        return calculateAverage(
                java,
                python,
                cpp
        );
    }


    /*
     * ==========================================
     * SKILL COVERAGE
     * ==========================================
     *
     * Measures how many supported technical
     * areas the user has actually assessed.
     */

    private int calculateSkillCoverage(
            List<TestResult> results) {

        int totalSkills = 6;
        int assessedSkills = 0;

        boolean java = false;
        boolean python = false;
        boolean sql = false;
        boolean javascript = false;
        boolean dsa = false;
        boolean cpp = false;

        for (TestResult result : results) {

            String skill =
                    result.getSkill();

            if ("Java".equalsIgnoreCase(skill)) {
                java = true;
            }

            if ("Python".equalsIgnoreCase(skill)) {
                python = true;
            }

            if ("SQL".equalsIgnoreCase(skill)) {
                sql = true;
            }

            if ("JavaScript".equalsIgnoreCase(skill)) {
                javascript = true;
            }

            if ("DSA".equalsIgnoreCase(skill)) {
                dsa = true;
            }

            if ("C/C++".equalsIgnoreCase(skill)) {
                cpp = true;
            }
        }

        if (java) assessedSkills++;
        if (python) assessedSkills++;
        if (sql) assessedSkills++;
        if (javascript) assessedSkills++;
        if (dsa) assessedSkills++;
        if (cpp) assessedSkills++;

        return
                (assessedSkills * 100)
                        / totalSkills;
    }


    /*
     * ==========================================
     * OVERALL READINESS
     * ==========================================
     */

    private int calculateOverallReadiness(
            int technicalSkills,
            int problemSolving,
            int databaseSkills,
            int programmingSkills,
            int skillCoverage) {

        /*
         * Weighted model:
         *
         * Technical Skills  = 30%
         * Problem Solving   = 25%
         * Database Skills   = 15%
         * Programming       = 20%
         * Skill Coverage    = 10%
         */

        double readiness =
                (technicalSkills * 0.30)
                        + (problemSolving * 0.25)
                        + (databaseSkills * 0.15)
                        + (programmingSkills * 0.20)
                        + (skillCoverage * 0.10);

        return Math.max(
                0,
                Math.min(
                        100,
                        (int) readiness
                )
        );
    }


    /*
     * ==========================================
     * BLOCKERS
     * ==========================================
     */

    private String[] getBlockers(
            List<TestResult> results) {

        java.util.ArrayList<String> blockers =
                new java.util.ArrayList<>();

        addBlockerIfLow(
                blockers,
                results,
                "Spring Boot",
                "Spring Boot"
        );

        addBlockerIfLow(
                blockers,
                results,
                "DSA",
                "DSA & Problem Solving"
        );

        addBlockerIfLow(
                blockers,
                results,
                "JavaScript",
                "JavaScript"
        );

        addBlockerIfLow(
                blockers,
                results,
                "SQL",
                "SQL"
        );

        addBlockerIfLow(
                blockers,
                results,
                "Java",
                "Java"
        );

        if (blockers.isEmpty()) {

            blockers.add(
                    "No major blockers detected"
            );
        }

        return blockers
                .stream()
                .limit(3)
                .toArray(String[]::new);
    }


    private void addBlockerIfLow(
            List<String> blockers,
            List<TestResult> results,
            String skill,
            String displayName) {

        int score =
                getSkillScore(
                        results,
                        skill
                );

        /*
         * A skill with no assessment is also
         * considered an area requiring attention.
         */

        if (score < 60) {

            blockers.add(displayName);
        }
    }


    /*
     * ==========================================
     * NEXT ACTION
     * ==========================================
     */

    private String getNextAction(
            List<TestResult> results) {

        String[] importantSkills = {
                "Spring Boot",
                "DSA",
                "JavaScript",
                "SQL",
                "Java",
                "Python"
        };

        String weakestSkill =
                null;

        int weakestScore =
                101;

        for (String skill :
                importantSkills) {

            int score =
                    getSkillScore(
                            results,
                            skill
                    );

            if (score < weakestScore) {

                weakestScore = score;
                weakestSkill = skill;
            }
        }

        if (weakestSkill == null) {

            return
                    "Complete your first technical assessment.";
        }

        if (weakestScore == 0) {

            return
                    "Take the " +
                            weakestSkill +
                            " assessment to identify your current level.";
        }

        return
                "Improve your " +
                        weakestSkill +
                        " skills through focused practice and a practical project.";
    }


    /*
     * ==========================================
     * GET SKILL SCORE
     * ==========================================
     */

    private int getSkillScore(
            List<TestResult> results,
            String skill) {

        int total = 0;
        int count = 0;

        for (TestResult result :
                results) {

            if (skill.equalsIgnoreCase(
                    result.getSkill())) {

                total += result.getScore();
                count++;
            }
        }

        if (count == 0) {
            return 0;
        }

        int average =
                total / count;

        return Math.max(
                0,
                Math.min(
                        100,
                        average
                )
        );
    }


    /*
     * ==========================================
     * AVERAGE
     * ==========================================
     */

    private int calculateAverage(
            int... scores) {

        int total = 0;
        int count = 0;

        for (int score : scores) {

            /*
             * Don't count unassessed skills.
             */
            if (score > 0) {

                total += score;
                count++;
            }
        }

        if (count == 0) {
            return 0;
        }

        return total / count;
    }


    /*
     * ==========================================
     * LIMIT VALUE
     * ==========================================
     */

    private int calculateOverallReadiness(
            List<TestResult> results) {

        if (results.isEmpty()) {
            return 0;
        }

        int total = 0;

        for (TestResult result :
                results) {

            total += result.getScore();
        }

        return Math.max(
                0,
                Math.min(
                        100,
                        total / results.size()
                )
        );
    }
}