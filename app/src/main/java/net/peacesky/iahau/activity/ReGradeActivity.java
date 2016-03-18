package net.peacesky.iahau.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;

import net.peacesky.iahau.R;
import net.peacesky.iahau.adapter.MySimpleAdapter;
import net.peacesky.iahau.application.MyApplication;
import net.peacesky.iahau.model.LinkNode;
import net.peacesky.iahau.model.REResult;
import net.peacesky.iahau.service.ReGradeService;
import net.peacesky.iahau.util.CommonUtil;
import net.peacesky.iahau.util.HttpUtil;
import net.peacesky.iahau.util.LinkUtil;

import org.litepal.crud.DataSupport;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.msebera.android.httpclient.Header;

public class ReGradeActivity extends AppCompatActivity {
    private ListView listView;
    private Toolbar toolbar;
    private SimpleAdapter adapter = null;

    private ReGradeService reGradeService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_re_grade);
        initVlaue();
        initView();
        getReG();
    }


    @Override
    public void onBackPressed() {
        finish();
    }

    private void initVlaue() {
        MyApplication myApplication = (MyApplication) getApplicationContext();
        reGradeService = myApplication.getReGradeService();
    }

    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.toolbarReGrade);
        toolbar.setTitle("等级考试成绩表");
        toolbar.setBackgroundColor(getResources().getColor(R.color.light_toolbar));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        listView = (ListView) findViewById(R.id.lvRe);

    }

    /**
     * 获取数据库中的数据
     */

    private List<Map<String, Object>> initList() {
        List<Map<String, Object>> temp = new ArrayList<Map<String, Object>>();
        Map<String, Object> map = null;

        List<REResult> grades = DataSupport.findAll(REResult.class);
        // List<Grade> grades= Grade.findAll(Grade.class);

        REResult grade;
        for (int i = 0; i < grades.size(); i++) {
            map = new HashMap<String, Object>();
            grade = grades.get(i);
            map.put("考试名称", grade.getREName());
            map.put("时间", grade.getYear() + "学年" + grade.getSemester() + "学期");
            map.put("成绩", grade.getResult());
            temp.add(map);
        }

        adapter = new MySimpleAdapter(this, temp, R.layout.listview_re_item,
                new String[]{"考试名称", "时间", "成绩"}, new int[]{R.id.ReName,
                R.id.reTime, R.id.REsult});

        adapter.notifyDataSetChanged();
        this.listView.setAdapter(adapter);
        return temp;
    }

    /**
     * 获取成绩，存入数据库
     */
    private void getReG() {
        final ProgressDialog dialog = CommonUtil.getProcessDialog(
                ReGradeActivity.this, "正在进入等级考试成绩表查询系统！！！");
        dialog.show();

        String link = DataSupport.where("title = ?", LinkUtil.DJKSCX).find(LinkNode.class).get(0).getLink();

        // linkService.save(newLink);

        Log.d("zafu", "完整的重定向后的教务系统等级考试成绩表地址：" + link);
        if (!link.equals("")) {
            HttpUtil.URL_QUERY = link;
            Log.d("zafu", HttpUtil.URL_QUERY);
        } else {
            Toast.makeText(getApplicationContext(), "链接出现错误",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        //HttpUtil.getClient().addHeader("Referer", HttpUtil.getURL_JWXTRE());
        HttpUtil.getClient().setURLEncodingEnabled(true);

        HttpUtil.get(HttpUtil.URL_QUERY, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                String resultContent;
                try {
                    resultContent = new String(arg2, "gb2312");
                    DataSupport.deleteAll(REResult.class);
                    reGradeService.parseResult(resultContent);

                    Toast.makeText(getApplicationContext(), "等级考试成绩表获取成功！！！",
                            Toast.LENGTH_SHORT).show();

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                initList();
                dialog.dismiss();
            }

            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2,
                                  Throwable arg3) {
                dialog.dismiss();
                Toast.makeText(getApplicationContext(), "获取失败！！！",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}
