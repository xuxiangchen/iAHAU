package net.peacesky.iahau.model;

import org.litepal.crud.DataSupport;

/**
 * Created by SuooL on 15/9/24.
 */
public class StudentInfo extends DataSupport {
    private int id;
    private String name;
    private String studentID;
    private String department;
    private String classNum;
    private String identity;
    private String major;
    private String balance;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStudentID() {
        return studentID;
    }

    public void setStudentID(String studentID) {
        this.studentID = studentID;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public String getClassNum() {
        return classNum;
    }

    public void setClassNum(String calssNum) {
        this.classNum = calssNum;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    @Override
    public String toString() {
        return "StudentInfo [id=" + id + ", name=" + name + ", studentID=" + studentID
                + ", department" + department + ", classNum " + classNum + ", major " + major +
                ", identity" + identity + ", balance" + balance + "]";
    }
}
