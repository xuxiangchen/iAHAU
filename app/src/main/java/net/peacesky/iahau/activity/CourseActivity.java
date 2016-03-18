package net.peacesky.iahau.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import net.peacesky.iahau.R;
import net.peacesky.iahau.application.MyApplication;
import net.peacesky.iahau.model.Course;
import net.peacesky.iahau.service.CourseService;
import net.peacesky.iahau.util.CommonUtil;

import java.util.List;

public class CourseActivity extends AppCompatActivity {
    //课程页面的button引用，6行7列
    private int[][] lessons = {
            {R.id.lesson11, R.id.lesson12, R.id.lesson13, R.id.lesson14, R.id.lesson15, R.id.lesson16, R.id.lesson17},
            {R.id.lesson21, R.id.lesson22, R.id.lesson23, R.id.lesson24, R.id.lesson25, R.id.lesson26, R.id.lesson27},
            {R.id.lesson31, R.id.lesson32, R.id.lesson33, R.id.lesson34, R.id.lesson35, R.id.lesson36, R.id.lesson37},
            {R.id.lesson41, R.id.lesson42, R.id.lesson43, R.id.lesson44, R.id.lesson45, R.id.lesson46, R.id.lesson47},
            {R.id.lesson51, R.id.lesson52, R.id.lesson53, R.id.lesson54, R.id.lesson55, R.id.lesson56, R.id.lesson57},
            {R.id.lesson61, R.id.lesson62, R.id.lesson63, R.id.lesson64, R.id.lesson65, R.id.lesson66, R.id.lesson67},
    };
    //某节课的背景图,用于随机获取
    private int[] bg = {R.drawable.kb1, R.drawable.kb2, R.drawable.kb3, R.drawable.kb4, R.drawable.kb5, R.drawable.kb6, R.drawable.kb7};
    private CourseService courseService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);
        initValue();
        initView();
    }

    /**
     * 初始化变量
     */
    private void initValue() {
        MyApplication application = ((MyApplication) getApplicationContext());
        courseService = application.getCourseService();
    }

    /**
     * 初始化视图
     */
    private void initView() {

        List<Course> courses = courseService.findAll();//获得数据库中的课程
        Course course = null;
        //循环遍历
        for (int i = 0; i < courses.size(); i++) {
            course = courses.get(i);//拿到当前课程
            int dayOfWeek = course.getDayOfWeek() - 1;//转换为lessons数组对应的下标
            int section = course.getStartSection() / 2;//转换为lessons数组对应的下标
            Button lesson = (Button) findViewById(lessons[section][dayOfWeek]);//获得该节课的button
            int bgRes = bg[CommonUtil.getRandom(bg.length - 1)];//随机获取背景色
            lesson.setBackgroundResource(bgRes);//设置背景
            lesson.setText(course.getCourseName() + "@" + course.getClasssroom());//设置文本为课程名+“@”+教室
        }
    }
}