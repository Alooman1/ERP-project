package edu.univ.erp.domain;

// Shows detailed marks breakdown for a student in one course
public class DetailedGradeRow {
    private final String courseCode;
    private final String courseTitle;
    private final Double quiz;
    private final Double midsem;
    private final Double endsem;

    // Creates detailed marks for quiz, midsem, endsem in one course
    public DetailedGradeRow(String courseCode, String courseTitle, Double quiz, Double midsem, Double endsem) {
        this.courseCode = courseCode;
        this.courseTitle = courseTitle;
        this.quiz = quiz;
        this.midsem = midsem;
        this.endsem = endsem;
    }

    // Get course code
    public String getCourseCode() { return courseCode; }
    // Get course name
    public String getCourseTitle() { return courseTitle; }
    // Get quiz marks
    public Double getQuiz() { return quiz; }
    // Get midsemester marks
    public Double getMidsem() { return midsem; }
    // Get end semester marks
    public Double getEndsem() { return endsem; }
}