package net.peacesky.iahau.application;

import net.peacesky.iahau.service.CourseService;
import net.peacesky.iahau.service.GradeService;
import net.peacesky.iahau.service.LinkService;
import net.peacesky.iahau.service.ReGradeService;
import net.peacesky.iahau.service.StudentInfoService;

import org.litepal.LitePalApplication;

/**
 * Created by SuooL on 15/9/27.
 */
public class MyApplication extends LitePalApplication {
    private CourseService courseService;
    private LinkService linkService;
    private StudentInfoService studentInfoService;
    private GradeService gradeService;
    private ReGradeService reGradeService;

    @Override
    public void onCreate() {
        super.onCreate();
        courseService = new CourseService();
        linkService = new LinkService();
        studentInfoService = new StudentInfoService();
        gradeService = new GradeService();
        reGradeService = new ReGradeService();
    }

    public CourseService getCourseService() {
        return courseService;
    }

    public LinkService getLinkService() {
        return linkService;
    }

    public StudentInfoService getStudentInfoService() {
        return studentInfoService;
    }

    public GradeService getGradeService() {
        return gradeService;
    }

    public ReGradeService getReGradeService() {
        return reGradeService;
    }
}

