package net.peacesky.iahau.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import net.peacesky.iahau.R;
import net.peacesky.iahau.adapter.MySimpleAdapter;
import net.peacesky.iahau.application.MyApplication;
import net.peacesky.iahau.model.Grade;
import net.peacesky.iahau.model.LinkNode;
import net.peacesky.iahau.service.GradeService;
import net.peacesky.iahau.util.CommonUtil;
import net.peacesky.iahau.util.GlobalDataUtil;
import net.peacesky.iahau.util.HttpUtil;
import net.peacesky.iahau.util.LinkUtil;
import net.peacesky.iahau.util.SharedPreferenceUtil;
import net.peacesky.iahau.view.PickerView;

import org.litepal.crud.DataSupport;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.msebera.android.httpclient.Header;

public class GradeActivity extends AppCompatActivity {

    private PickerView year_pv;
    private PickerView semester_pv;
    private GradeService gradeService;
    private SharedPreferenceUtil util;
    private View dialogView;
    private ListView listView;
    private Toolbar toolbar;
    private AlertDialog timePickerDialog;

    private SimpleAdapter adapter = null;
    /**
     * 选项的事件监听器
     */
    DialogInterface.OnClickListener onclick = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            // TODO Auto-generated method stub
            Log.d("zafu", "选择了:" + GlobalDataUtil.yearSel + GlobalDataUtil.semesterSel);
            toolbar.setSubtitle(GlobalDataUtil.yearSel + "学年" + GlobalDataUtil.semesterSel + "学期");
            getCJ();
            timePickerDialog.dismiss();
        }

    };
    private List<Map<String, Object>> list = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grade);
        initView();
        initVlaue();
        getViewState();

    }

    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        return super.onMenuOpened(featureId, menu);
    }

//    @Override
//    public boolean onMenuClick(){
//
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_grade, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        timePickerDialog.show();

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void initVlaue() {
        MyApplication myApplication = (MyApplication) getApplicationContext();
        gradeService = myApplication.getGradeService();
        util = new SharedPreferenceUtil(this, "LinkInfo");
    }

    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.toolbarGrade);
        toolbar.setTitle("我的成绩表");
        toolbar.setBackgroundColor(getResources().getColor(R.color.light_toolbar));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        listView = (ListView) findViewById(R.id.lv);
        list = initList();


        LayoutInflater layoutInflater = LayoutInflater.from(getApplicationContext());
        dialogView = layoutInflater.inflate(R.layout.pickerview_dialog, (ViewGroup) findViewById(R.id.pick_view));

        year_pv = (PickerView) dialogView.findViewById(R.id.year_pv);
        semester_pv = (PickerView) dialogView.findViewById(R.id.semester_pv);

        List<String> year = new ArrayList<String>();
        List<String> semester = new ArrayList<String>();

        year.add(0, "全部");
        year.add(1, "2012-2013");
        year.add(2, "2013-2014");
        year.add(3, "2014-2015");
        year.add(4, "2015-2016");

        semester.add(0, "全部");
        semester.add(1, "1");
        semester.add(2, "2");

        year_pv.setData(year);
        semester_pv.setData(semester);

        GlobalDataUtil.yearSel = year.get(year.size() / 2);
        GlobalDataUtil.semesterSel = semester.get(semester.size() / 2);

        year_pv.setOnSelectListener(new PickerView.onSelectListener() {

            @Override
            public void onSelect(String text) {
//                Log.d("zafu", "选择了 " + text + "学年");
                GlobalDataUtil.yearSel = text;
            }
        });

        semester_pv.setOnSelectListener(new PickerView.onSelectListener() {

            @Override
            public void onSelect(String text) {
                GlobalDataUtil.semesterSel = text;
            }
        });


        timePickerDialog = new AlertDialog.Builder(GradeActivity.this)
                .setTitle("请选择时间")
                .setView(dialogView)
                .setPositiveButton("确定", onclick)
                .create();
        timePickerDialog.show();


    }

    /**
     * 获取数据库中的数据
     */

    private List<Map<String, Object>> initList() {
        List<Map<String, Object>> temp = new ArrayList<Map<String, Object>>();
        Map<String, Object> map = null;

        List<Grade> grades = DataSupport.findAll(Grade.class);
        // List<Grade> grades= Grade.findAll(Grade.class);

        Grade grade;
        for (int i = 0; i < grades.size(); i++) {
            map = new HashMap<String, Object>();
            grade = grades.get(i);
            map.put("课程", grade.getCourseName());
            map.put("成绩", grade.getFinalResult());
            map.put("绩点", grade.getGPA());
            temp.add(map);
        }

        adapter = new MySimpleAdapter(this, temp, R.layout.listview_item,
                new String[]{"课程", "成绩", "绩点"}, new int[]{R.id.lessonName,
                R.id.result, R.id.gpa});

        adapter.notifyDataSetChanged();
        this.listView.setAdapter(adapter);
        return temp;
    }

    /**
     * 获取成绩，存入数据库
     */
    private void getCJ() {
        final ProgressDialog dialog = CommonUtil.getProcessDialog(
                GradeActivity.this, "正在获取成绩表！！！");
        dialog.show();

        String link = DataSupport.where("title = ?", LinkUtil.CJCX).find(LinkNode.class).get(0).getLink();

        Log.d("zafu", "完整的重定向后的教务系统成绩表地址：" + link);
        if (!link.equals("")) {
            HttpUtil.URL_QUERY = link;
        } else {
            Toast.makeText(getApplicationContext(), "链接出现错误",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        HttpUtil.getClient().setURLEncodingEnabled(true);

        HttpUtil.ddlxn = GlobalDataUtil.yearSel;


        HttpUtil.ddlxq = GlobalDataUtil.semesterSel;

        RequestParams params = HttpUtil.getResultRequestParams();

        HttpUtil.post(HttpUtil.URL_QUERY, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String resultContent = new String(responseBody, "gb2312");
                    DataSupport.deleteAll(Grade.class);
                    List<String> postData = gradeService.parsePostData(resultContent);
                    try {
                        HttpUtil.__VIEWSTATE = postData.get(0);
                        HttpUtil.__EVENTVALIDATION = postData.get(1);
                        HttpUtil.btnCx = " 查  询 ";
                    } catch (ArrayIndexOutOfBoundsException e) {
                        Log.d("zafu", "解析状态参数出错!");
                    }

                    gradeService.parseGrade(resultContent);
                    dialog.dismiss();
                    initList();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } finally {
                    dialog.dismiss();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getApplicationContext(), "获取失败！！！！",
                        Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

//        HttpUtil.get(HttpUtil.URL_QUERY, new AsyncHttpResponseHandler() {
//
//            @Override
//            public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
//                dialog.dismiss();
//                String resultContent;
//                try {
//                    resultContent = new String(arg2, "gb2312");
//                    DataSupport.deleteAll(Course.class);
//                    //courseService.parseCourse(resultContent);
//                    Log.d("zafu", "课程表内容：\n" + resultContent.toString());
//                    //Document content = Jsoup.parse(resultContent);
//
//                    Toast.makeText(getApplicationContext(), "课表获取成功！！！",
//                            Toast.LENGTH_SHORT).show();
//
//                } catch (UnsupportedEncodingException e) {
//                    e.printStackTrace();
//                }
//                //setupFragment(new SimpleFramgment());
//            }
//
//            @Override
//            public void onFailure(int arg0, Header[] arg1, byte[] arg2,
//                                  Throwable arg3) {
//                dialog.dismiss();
//                Toast.makeText(getApplicationContext(), "课表获取失败！！！",
//                        Toast.LENGTH_SHORT).show();
//            }
//        });
    }

    /**
     * 获取成绩，存入数据库
     */
    private void getViewState() {
        final ProgressDialog dialog = CommonUtil.getProcessDialog(
                GradeActivity.this, "正在进入成绩表查询系统！！！");
        dialog.show();

        String link = DataSupport.where("title = ?", LinkUtil.CJCX).find(LinkNode.class).get(0).getLink();


        Log.d("zafu", "完整的重定向后的教务系统成绩表地址：" + link);
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
        String referer = "http://zf.ahau.edu.cn/xs_main_zzjk1.aspx?xh=" + GlobalDataUtil.studentID + "&type=1";
        // HttpUtil.getClient().addHeader("Referer","http://zf.ahau.edu.cn/xs_main_zzjk1.aspx?xh=13100501&type=1");
        HttpUtil.getClient().addHeader("Referer", referer);
        HttpUtil.get(HttpUtil.URL_QUERY, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                dialog.dismiss();
                String resultContent;
                try {
                    resultContent = new String(arg2, "gb2312");
                    DataSupport.deleteAll(Grade.class);
                    List<String> postData = gradeService.parsePostData(resultContent);
                    try {
                        HttpUtil.__VIEWSTATE = postData.get(0);
                        HttpUtil.__EVENTVALIDATION = postData.get(1);

                        HttpUtil.btnCx = " 查  询 ";
                    } catch (ArrayIndexOutOfBoundsException e) {
                        Log.d("zafu", "解析状态参数出错!");
                    }

                    Log.d("zafu", "课程表内容：\n" + resultContent);
                    //Document content = Jsoup.parse(resultContent);

                    Toast.makeText(getApplicationContext(), "成绩表进入成功！！！",
                            Toast.LENGTH_SHORT).show();

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                // setupFragment(new SimpleFramgment());
            }

            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2,
                                  Throwable arg3) {
                dialog.dismiss();
                Toast.makeText(getApplicationContext(), "课表获取失败！！！",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}
