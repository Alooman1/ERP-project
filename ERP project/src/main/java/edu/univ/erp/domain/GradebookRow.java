package edu.univ.erp.domain;

// Stores marks for one student in different exam components
public class GradebookRow {
    private final int studentId;
    private final int enrollmentId;
    private final String studentRollNo;
    private final String studentName;
    private Double quiz;
    private Double midsem;
    private Double endsem;
    private String rollNo;

    // Get student's roll number
    public String getRollNo() { return rollNo; }
    // Set student's roll number
    public void setRollNo(String rollNo) { this.rollNo = rollNo; }

    // Creates a row with student's marks in quiz, midsem, endsem
    public GradebookRow(int studentId, int enrollmentId, String studentRollNo, String studentName, Double quiz, Double midsem, Double endsem) {
        this.studentId = studentId;
        this.enrollmentId = enrollmentId;
        this.studentRollNo = studentRollNo;
        this.studentName = studentName;
        this.quiz = quiz;
        this.midsem = midsem;
        this.endsem = endsem;
    }

    // Get student's ID number
    public int getStudentId() { return studentId; }
    // Get enrollment record ID
    public int getEnrollmentId() { return enrollmentId; }
    // Get student's roll number
    public String getStudentRollNo() { return studentRollNo; }
    // Get student's name
    public String getStudentName() { return studentName; }
    // Get quiz marks
    public Double getQuiz() { return quiz; }
    // Get midsemester marks
    public Double getMidsem() { return midsem; }
    // Get end semester marks
    public Double getEndsem() { return endsem; }
    // Update quiz marks
    public void setQuiz(Double quiz) { this.quiz = quiz; }
    // Update midsemester marks
    public void setMidsem(Double midsem) { this.midsem = midsem; }
    // Update end semester marks
    public void setEndsem(Double endsem) { this.endsem = endsem; }
}