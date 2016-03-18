package net.peacesky.iahau.service;

import android.util.Log;

import net.peacesky.iahau.model.Course;
import net.peacesky.iahau.model.StudentInfo;
import net.peacesky.iahau.util.CommonUtil;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.litepal.crud.DataSupport;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class CourseService {

    private String studentID;

    /**
     * 保存一节课程
     *
     * @param course
     * @return
     */
    public boolean save(Course course) {
        return course.save();
    }

    /**
     * 查询所有课程
     *
     * @return
     */
    public List<Course> findAll() {
        return DataSupport.findAll(Course.class);
    }

    /**
     * 根据网页返回结果解析课程并保存
     *
     * @param content
     * @return
     */
    public String parseCourse(String content) {
        StringBuilder result = new StringBuilder();
        Document doc = Jsoup.parse(content);

        StudentInfo newStudentInfo = new StudentInfo();
        Elements stuElements = doc.select(".trbg1 > td > span ");
        for (Element element : stuElements) {
            Log.d("zafu", "GGGGGGG" + element.text());

        }
        try {
            studentID = stuElements.get(0).text().split("：")[1];
            // newStudentInfo.setStudentID(studentID);
            newStudentInfo.setName(stuElements.get(1).text().split("：")[1]);
            newStudentInfo.setDepartment(stuElements.get(2).text().split("：")[1]);
            newStudentInfo.setMajor(stuElements.get(3).text().split("：")[1]);
            newStudentInfo.setClassNum(stuElements.get(4).text().split("：")[1]);
            newStudentInfo.updateAll("studentID = ?", studentID);
        } catch (IndexOutOfBoundsException e) {
            Log.d("zafu", "解析课程表页面的个人信息出错");
        }


        Elements elements = doc.select("table#Table1");
        //Log.d("zafu", elements.toString());
        Element element = elements.get(0).child(0);
        //移除一些无用数据
        element.child(1).remove();
        element.child(1).child(0).remove();
        element.child(5).child(0).remove();
        element.child(9).child(0).remove();

        int rowNum = element.childNodeSize() - 1;

        int times = 0;

        for (int i = 1; i < rowNum; i++) {
            Element row = element.child(i);
            int columnNum = row.childNodeSize() - 2;
            for (int j = 1; j < columnNum; j++) {
                Element column = row.child(j);

                if (!column.html().equals("&nbsp;")) {
                    // result.append(column.html()+ "\n\n");
                    Log.d("zafu", "这是第几次：" + times++);
                    Log.d("zafu", "原始课程是:" + column.html());
                    splitCourse(column.html());//所提取课程里面可能包含多节课，进行分割
                }
            }
        }
        return result.toString();
    }

    /**
     * 设置单双周
     *
     * @param week
     * @param course
     */
    public void setEveryWeekByChinese(String week, Course course) {
        // 1代表单周，2代表双周
        if (week != null) {
            if (week.equals("单周"))
                course.setEveryWeek(1);
            else if (week.equals("双周"))
                course.setEveryWeek(2);
        }
        // 默认值为0，代表每周
    }

    /**
     * 根据传进来的课程格式转换为对应的实体类并保存
     *
     * @param sub
     * @return
     */
    private Course storeCourseByResult(String sub) {
        //周二第1,2节{第4-16周}		二,1,2,4,16,null
        //{第2-10周|3节/周}		null,null,null,2,10,3节/周
        //周二第1,2节{第4-16周|双周}	二,1,2,4,16,双周
        //周二第1节{第4-16周}		二,1,null,4,16,null
        //周二第1节{第4-16周|双周}	二,1,null,4,16,双周
        // str格式如上，这里只是简单考虑每个课都只有两节课，实际上有三节和四节，模式就要改动，其他匹配模式请自行修改
        // String reg="周.第(\\d{1,2}),(\\d{1,2})节\\{第(\\d{1,2})-(\\d{1,2})周\\}";
        //String reg = "周(.)第(\\d{1,2}),(\\d{1,2})节\\{第(\\d{1,2})-(\\d{1,2})周\\|?((.周))?\\}";
        String reg = "周?(.)?第?(\\d{1,2})?,?(\\d{1,2})?节?\\{第(\\d{1,2})-(\\d{1,2})周\\|?((.*周))?\\}";

        String splitPattern = "<br>";
        Log.d("zafu", "课程表" + sub);
        String[] temp = sub.split(splitPattern);
        for (String str : temp) {
            Log.d("zafu", "课程信息分解：" + str);
        }

        // 按指定模式在字符串查找
        String coueseInfo = sub.replace("<br />", "");

        // 创建 Pattern 对象
        Pattern r = Pattern.compile(reg);

        // 现在创建 matcher 对象
        Matcher m = r.matcher(coueseInfo);

        Course course = new Course();
        course.setStudentID(studentID);
        course.setCourseName(temp[0]);
        course.setCourseTime(temp[1]);
        course.setTeacher(temp[2]);
        try {
            //数组肯能越界，即没有教师
            course.setClasssroom(temp[3]);
        } catch (ArrayIndexOutOfBoundsException e) {
            course.setClasssroom("无教师");
        }
        if (m.find()) {
            Log.d("zafu", "这个是解析后的" + m.group(1));
            course.setDayOfWeek(CommonUtil.getDayOfWeek(m.group(1)));
            course.setStartSection(Integer.parseInt(m.group(2)));
            if (null != m.group(3))
                course.setEndSection(Integer.parseInt(m.group(3)));
            else
                course.setEndSection(Integer.parseInt(m.group(2)));
            course.setStartWeek(Integer.parseInt(m.group(4)));
            course.setEndWeek(Integer.parseInt(m.group(5)));
            String t = m.group(6);
            setEveryWeekByChinese(t, course);
            save(course);
        }

        return course;
    }

    /**
     * 提取课程格式，可能包含多节课
     *
     * @param str
     * @return
     */
    private int splitCourse(String str) {
        String pattern = "<br /><br />";
        String[] split = str.split(pattern);
        for (String str1 : split) {
            Log.d("zafu", "分解" + str1);
        }

        if (split.length > 1) {// 如果大于一节课
            for (int i = 0; i < split.length; i++) {
                if (!(split[i].startsWith("<br />") && split[i].endsWith("<br />"))) {
                    storeCourseByResult(split[i]);//保存单节课
                } else {
                    //<br />文化地理（网络课程）<br />周日第10节{第17-17周}<br />李宏伟<br />
                    //以上格式的特殊处理
                    int brLength = "<br />".length();
                    String substring = split[i].substring(brLength, split[i].length() - brLength);
                    storeCourseByResult(substring);//保存单节课
                }
            }
            return split.length;
        } else {
            storeCourseByResult(str);//保存
            return 1;
        }
    }
}
