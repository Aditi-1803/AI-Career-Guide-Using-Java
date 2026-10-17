
package com.careeriq.controller;

import com.careeriq.model.TestResult;
import com.careeriq.repository.TestResultRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;

@Controller
public class CareerController {

    private final TestResultRepository testResultRepository;

    public CareerController(
            TestResultRepository testResultRepository) {
        this.testResultRepository = testResultRepository;
    }

    @GetMapping("/career-matches")
    public String showCareerMatches(
            @RequestParam(required = false) Integer score,
            Model model) {

        List<TestResult> results =
                testResultRepository.findAll();

        // Keep the latest score parameter for compatibility
        // with existing result-page links.
        Map<String, Integer> userScores = new HashMap<>();

        Map<String, Integer> totals = new HashMap<>();
        Map<String, Integer> counts = new HashMap<>();

        for (TestResult result : results) {
            if (result.getSkill() == null) {
                continue;
            }

            String skill = normalize(result.getSkill());

            totals.merge(skill, result.getScore(), Integer::sum);
            counts.merge(skill, 1, Integer::sum);
        }

        for (String skill : totals.keySet()) {
            userScores.put(
                    skill,
                    totals.get(skill) / counts.get(skill)
            );
        }

        List<CareerMatch> careers = new ArrayList<>();

        addCareer(
                careers, userScores,
                "Java Full Stack Developer",
                "Java, Spring Boot, JavaScript, SQL, HTML/CSS",
                requirements(
                        "Java", 75,
                        "Spring Boot", 70,
                        "JavaScript", 65,
                        "SQL", 65,
                        "HTML/CSS", 60
                )
        );

        addCareer(
                careers, userScores,
                "Backend Developer",
                "Java, Spring Boot, SQL and DSA",
                requirements(
                        "Java", 75,
                        "Spring Boot", 75,
                        "SQL", 65,
                        "DSA", 60
                )
        );

        addCareer(
                careers, userScores,
                "Software Engineer",
                "Programming, algorithms and problem solving",
                requirements(
                        "Java", 70,
                        "DSA", 75,
                        "Python", 60,
                        "SQL", 55,
                        "C/C++", 60
                )
        );

        addCareer(
                careers, userScores,
                "Python Developer",
                "Python programming, SQL and algorithms",
                requirements(
                        "Python", 75,
                        "SQL", 60,
                        "DSA", 60,
                        "Java", 50
                )
        );

        addCareer(
                careers, userScores,
                "Data Analyst",
                "SQL, Python and data-oriented skills",
                requirements(
                        "SQL", 75,
                        "Python", 65,
                        "DSA", 50
                )
        );

        addCareer(
                careers, userScores,
                "Machine Learning Engineer",
                "Python, algorithms and technical foundations",
                requirements(
                        "Python", 80,
                        "DSA", 65,
                        "SQL", 55,
                        "C/C++", 60
                )
        );

        careers.sort(
                Comparator.comparingInt(
                        CareerMatch::getMatchPercentage
                ).reversed()
        );

        model.addAttribute("careers", careers);
        model.addAttribute("score", score);
        model.addAttribute("assessedSkills", userScores.size());
        model.addAttribute("hasAssessmentData", !userScores.isEmpty());

        return "career-matches";
    }

    private void addCareer(
            List<CareerMatch> careers,
            Map<String, Integer> userScores,
            String careerName,
            String reason,
            Map<String, Integer> requiredSkills) {

        int assessedCount = 0;
        int proficiencyTotal = 0;

        List<String> missingSkills = new ArrayList<>();
        List<String> strongSkills = new ArrayList<>();

        for (Map.Entry<String, Integer> entry
                : requiredSkills.entrySet()) {

            String skill = entry.getKey();
            int requiredScore = entry.getValue();

            Integer userScore = userScores.get(normalize(skill));

            if (userScore == null) {
                missingSkills.add(skill + " (not assessed)");
                continue;
            }

            assessedCount++;

            int cappedScore = Math.min(userScore, requiredScore);

            int proficiencyPercentage =
                    (cappedScore * 100) / requiredScore;

            proficiencyTotal += proficiencyPercentage;

            if (userScore >= requiredScore) {
                strongSkills.add(skill);
            } else {
                missingSkills.add(
                        skill + " (need " + requiredScore
                                + "%, current " + userScore + "%)"
                );
            }
        }

        int totalRequired = requiredSkills.size();

        int coverage = totalRequired == 0
                ? 0
                : assessedCount * 100 / totalRequired;

        int averageProficiency = assessedCount == 0
                ? 0
                : proficiencyTotal / assessedCount;

        // 70% measured proficiency + 30% assessed skill coverage.
        // Unassessed skills are treated as unverified, not as
        // proof that the user has no knowledge of that skill.
        int matchPercentage = assessedCount == 0
                ? 0
                : (averageProficiency * 70
                + coverage * 30) / 100;

        matchPercentage = Math.max(
                0,
                Math.min(100, matchPercentage)
        );

        String explanation = reason
                + ". Based on your recorded assessments, "
                + assessedCount + " of " + totalRequired
                + " required skills have been assessed.";

        careers.add(
                new CareerMatch(
                        careerName,
                        matchPercentage,
                        explanation,
                        missingSkills.isEmpty()
                                ? "No measured gaps against these targets"
                                : String.join(", ", missingSkills),
                        coverage,
                        strongSkills.isEmpty()
                                ? "None confirmed yet"
                                : String.join(", ", strongSkills)
                )
        );
    }

    private Map<String, Integer> requirements(
            Object... values) {

        Map<String, Integer> map = new LinkedHashMap<>();

        for (int i = 0; i < values.length; i += 2) {
            map.put(
                    (String) values[i],
                    (Integer) values[i + 1]
            );
        }

        return map;
    }

    private String normalize(String skill) {
        return skill.trim()
                .toLowerCase(Locale.ROOT)
                .replace(" ", "");
    }

    public static class CareerMatch {

        private final String careerName;
        private final int matchPercentage;
        private final String reason;
        private final String missingSkills;
        private final int skillCoverage;
        private final String confirmedStrengths;

        public CareerMatch(
                String careerName,
                int matchPercentage,
                String reason,
                String missingSkills,
                int skillCoverage,
                String confirmedStrengths) {

            this.careerName = careerName;
            this.matchPercentage = matchPercentage;
            this.reason = reason;
            this.missingSkills = missingSkills;
            this.skillCoverage = skillCoverage;
            this.confirmedStrengths = confirmedStrengths;
        }

        public String getCareerName() {
            return careerName;
        }

        public int getMatchPercentage() {
            return matchPercentage;
        }

        public String getReason() {
            return reason;
        }

        public String getMissingSkills() {
            return missingSkills;
        }

        public int getSkillCoverage() {
            return skillCoverage;
        }

        public String getConfirmedStrengths() {
            return confirmedStrengths;
        }
    }
}
