package net.peacesky.iahau.model;

import org.litepal.crud.DataSupport;

/**
 * Created by SuooL on 15/9/24.
 */
public class Grade extends DataSupport {
    private int id;                 // 主鍵，自增
    private String StudentID;       // 学号
    private String Year;           // 学年
    private String semester;        // 学期
    private String courseName;      // 课程名
    private String generalResult;   // 平时成绩
    private String examResult;
    private String finalResult;
    private float GPA;              // 绩点

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStudentID() {
        return StudentID;
    }

    public void setStudentID(String studentID) {
        StudentID = studentID;
    }

    public String getYear() {
        return Year;
    }

    public void setYear(String year) {
        Year = year;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getGeneralResult() {
        return generalResult;
    }

    public void setGeneralResult(String generalResult) {
        this.generalResult = generalResult;
    }

    public String getExamResult() {
        return examResult;
    }

    public void setExamResult(String examResult) {
        this.examResult = examResult;
    }

    public String getFinalResult() {
        return finalResult;
    }

    public void setFinalResult(String finalResult) {
        this.finalResult = finalResult;
    }

    public float getGPA() {
        return GPA;
    }

    public void setGPA(float GPA) {
        this.GPA = GPA;
    }

    @Override
    public String toString() {
        return "Grade [id=" + id + ", StudentID=" + StudentID + ", Year=" + Year + ", semester="
                + semester + ", courseName=" + courseName + ", generalResult=" + generalResult
                + ", examResult=" + examResult + ", finalResult=" + finalResult + ", GPA=" + GPA + "]";
    }
}
