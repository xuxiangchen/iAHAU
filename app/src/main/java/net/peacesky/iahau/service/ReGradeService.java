package net.peacesky.iahau.service;

import android.util.Log;

import net.peacesky.iahau.model.REResult;
import net.peacesky.iahau.util.GlobalDataUtil;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.litepal.crud.DataSupport;

import java.util.List;

/**
 * Created by SuooL on 15/10/9.
 */
public class ReGradeService {
    /**
     * 保存一条成绩
     *
     * @param resutlt
     * @return
     */
    public boolean save(REResult resutlt) {
        return resutlt.save();
    }

    /**
     * 查询所有成绩
     *
     * @return
     */
    public List<REResult> findAll() {
        return DataSupport.findAll(REResult.class);
    }


    public void parseResult(String content) {

        Document doc = Jsoup.parse(content);
        Elements elements = doc.select(".datelist");
        //Log.d("zafu", elements.toString());
        Element element = elements.get(0).child(0);

        element.child(0).remove();

        int rowNum = element.childNodeSize() - 1;
        for (int i = 0; i < rowNum; i++) {
            Element row = element.child(i);

            for (int j = 0; j < 6; j++) {
                Log.d("zafu", "...." + row.child(j).text());

            }
            if (row.child(3).text().equals(" "))
                continue;

            REResult newResult = new REResult();
            newResult.setStudentID(GlobalDataUtil.studentID);
            newResult.setYear(row.child(0).text());
            newResult.setSemester(row.child(1).text());
            newResult.setREName(row.child(2).text());
            newResult.setExamNum(row.child(3).text());
            newResult.setResult(row.child(5).text());

            save(newResult);
        }
    }

}
