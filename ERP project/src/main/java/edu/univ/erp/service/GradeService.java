package edu.univ.erp.service;

import edu.univ.erp.data.CourseDAO;
import edu.univ.erp.data.GradeDAO;
import edu.univ.erp.domain.ClassStats;
import edu.univ.erp.domain.DetailedGradeRow;
import edu.univ.erp.domain.FinalGradeRow;
import edu.univ.erp.domain.GradebookRow;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GradeService {

    private final GradeDAO gradeDAO;
    private final CourseDAO courseDAO;

    // Grade thresholds for calculating final grades
    private static final String GRADE_A = "A"; // 90+
    private static final String GRADE_B = "B"; // 80+
    private static final String GRADE_C = "C"; // 70+
    private static final String GRADE_D = "D"; // 60+
    private static final String GRADE_F = "F"; // < 60

    public GradeService() {
        this.gradeDAO = new GradeDAO();
        this.courseDAO = new CourseDAO();
    }

    // Calculate and display final grades for a student
    public List<FinalGradeRow> getCalculatedFinalGradesForDisplay(int studentId) {
        // Get all the component grades (quiz, midsem, endsem)
        List<DetailedGradeRow> detailedGrades = gradeDAO.getComponentGrades(studentId);

        List<FinalGradeRow> calculatedFinalGrades = new ArrayList<>();

        // Calculate final grade for each course
        for (DetailedGradeRow row : detailedGrades) {
            Double quiz = row.getQuiz();
            Double midsem = row.getMidsem();
            Double endsem = row.getEndsem();

            String courseCode = row.getCourseCode();

            // Only calculate if all components are available
            if (quiz != null && midsem != null && endsem != null) {
                double totalScore = quiz + midsem + endsem;
                String finalGrade = mapScoreToGrade(totalScore);

                // Get course details for credits
                Map<String, Object> courseDetails = courseDAO.getCourseDetailsByCode(courseCode);

                int credits = (courseDetails != null && courseDetails.containsKey("credits"))
                              ? (Integer) courseDetails.get("credits") : 0;

                // Add to results
                calculatedFinalGrades.add(new FinalGradeRow(
                    courseCode,
                    row.getCourseTitle(),
                    credits,
                    finalGrade
                ));
            }
        }

        return calculatedFinalGrades;
    }

    // Get final grades from database (already calculated)
    public List<FinalGradeRow> getFinalGrades(int studentId) {
        return gradeDAO.getFinalGrades(studentId);
    }

    // Get individual component grades (quiz, midsem, endsem)
    public List<DetailedGradeRow> getComponentGrades(int studentId) {
        return gradeDAO.getComponentGrades(studentId);
    }

    // Get gradebook for a specific section (for instructors)
    public List<GradebookRow> getGradebookForSection(int sectionId) {
        return gradeDAO.getGradebookForSection(sectionId);
    }

    // Save or update a grade for a student
    public boolean saveGrade(int enrollmentId, String component, Double score) {
        return gradeDAO.saveOrUpdateGrade(enrollmentId, component, score);
    }

    // Get statistics for a section (average grades, etc.)
    public List<ClassStats> getSectionStats(int sectionId) {
        return gradeDAO.getStatsForSection(sectionId);
    }

    // Convert numerical score to letter grade
    private String mapScoreToGrade(double score) {
        if (score >= 90) {
            return GRADE_A;
        } else if (score >= 80) {
            return GRADE_B;
        } else if (score >= 70) {
            return GRADE_C;
        } else if (score >= 60) {
            return GRADE_D;
        } else {
            return GRADE_F;
        }
    }
}