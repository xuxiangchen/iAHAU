package net.peacesky.iahau.service;

import android.util.Log;

import net.peacesky.iahau.model.Grade;
import net.peacesky.iahau.util.GlobalDataUtil;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.litepal.crud.DataSupport;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UnknownFormatConversionException;

/**
 * Created by SuooL on 15/10/7.
 */
public class GradeService {
    /**
     * 保存一条成绩
     *
     * @param course
     * @return
     */
    public boolean save(Grade course) {
        return course.save();
    }

    /**
     * 查询所有成绩
     *
     * @return
     */
    public List<Grade> findAll() {
        return DataSupport.findAll(Grade.class);
    }

    /**
     * 解析成绩页面,获取其 ViewState 和 EVENTVALIDATION 字段值
     */
    public List<String> parsePostData(String resultContent) {

        List<String> strList = new ArrayList<String>();
        Document content = Jsoup.parse(resultContent);

        Elements postParams = content.select("form#Form1");

        // Element elementState = postParams.select("input#__VIEWSTATE").last();
        Element elementState = postParams.get(0).select("input[name=__VIEWSTATE]").last();
        // 将菜单选项的链接存储下来
        strList.add(0, elementState.attr("value"));
        Log.d("zafu", "获取的状态__VIEWSTATE是" + elementState.attr("value") + "\n\n");

        // Element elementValid = postParams.select("input#__EVENTVALIDATION").first();‘
        Element elementValid = postParams.get(0).select("input[name=__EVENTARGUMENT]").first();
        // 将菜单选项的链接存储下来
        //util.setKeyData(element.html(), element.attr("href"));
        strList.add(1, elementValid.attr("value"));
        Log.d("zafu", "获取的状态__EVENTVALIDATION是" + elementValid.attr("value") + "\n\n");

        return strList;
    }

    public void parseGrade(String content) {

        Document doc = Jsoup.parse(content);
        Elements elements = doc.select(".datelist");
        //Log.d("zafu", elements.toString());
        Element element = elements.get(0).child(0);

        element.child(0).remove();

        int rowNum = element.childNodeSize() - 1;
        BigDecimal divNum = new BigDecimal("1");
        for (int i = 0; i < rowNum; i++) {
            Element row = element.child(i);
            Grade newGrade = new Grade();
            newGrade.setStudentID(GlobalDataUtil.studentID);
            newGrade.setYear(GlobalDataUtil.yearSel);
            newGrade.setSemester(GlobalDataUtil.semesterSel);
            newGrade.setCourseName(row.child(1).text());
            newGrade.setGeneralResult(row.child(4).text());
            newGrade.setExamResult(row.child(4).text());
            newGrade.setFinalResult(row.child(4).text());
            String tmp2 = row.child(4).text();
            String tmp3 = row.child(4).toString();
            // int tmp = Integer.parseInt(row.child(5).text());

            String tmp4 = row.child(0).text();
            String tmp5 = row.child(1).text();
            String tmp6 = row.child(2).text();

            try {
                if (Integer.parseInt(row.child(4).text()) < 59) {
                    Log.d("zafu", "成绩是:" + row.child(4).text() + "分");
                    try {
                        int resu = Integer.parseInt(row.child(4).text());
                        Log.d("zafu", "" + resu);
                        newGrade.setGPA(0.0f);
                    } catch (UnknownFormatConversionException e) {
                        newGrade.setFinalResult("需补考");
                        newGrade.setGPA(0.0f);
                    }

                    newGrade.setFinalResult(row.child(4).text());
                    newGrade.setGPA(1.0f);
                } else {
                    BigDecimal gpaGrade = new BigDecimal((Integer.parseInt(row.child(4).text()) / 10.0f - 5.0f));
                    newGrade.setGPA(gpaGrade.divide(divNum, 2, BigDecimal.ROUND_HALF_UP).floatValue());
                    newGrade.setFinalResult(row.child(4).text());
                }

            } catch (NumberFormatException e) {
                newGrade.setGPA(1.0f);
            }

            save(newGrade);
        }
    }
}
