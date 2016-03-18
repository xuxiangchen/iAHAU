package net.peacesky.iahau.model;

import org.litepal.crud.DataSupport;

/**
 * @author SuooL
 *         <p/>
 *         Created by SuooL on 15/9/24.
 */
public class REResult extends DataSupport {
    private int id;                 // 主鍵，自增
    private String studentID;       // 学号
    private String examNum;         // 准考证号
    private String year;           // 考试时间
    private String semester;
    private String result;          // 成绩
    private String REName;          // 等级考试名

    public String getREName() {
        return REName;
    }

    public void setREName(String REName) {
        this.REName = REName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStudentID() {
        return studentID;
    }

    public void setStudentID(String studentID) {
        this.studentID = studentID;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public String getExamNum() {
        return examNum;
    }

    public void setExamNum(String examNum) {
        this.examNum = examNum;
    }

    @Override
    public String toString() {
        return "REResult [id=" + id + ", studentID=" + studentID + ", examNum" + examNum + ", result=" + result + ", REName=" + REName + ", year=" + year
                + ", semester" + semester + "]";
    }

}
