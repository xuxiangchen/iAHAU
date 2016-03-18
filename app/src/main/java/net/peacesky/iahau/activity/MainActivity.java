package net.peacesky.iahau.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;

import net.peacesky.iahau.R;
import net.peacesky.iahau.application.MyApplication;
import net.peacesky.iahau.fragment.DrawerItem;
import net.peacesky.iahau.fragment.DrawerItemAdapter;
import net.peacesky.iahau.fragment.DrawerMenu;
import net.peacesky.iahau.fragment.SimpleFramgment;
import net.peacesky.iahau.model.Course;
import net.peacesky.iahau.model.LinkNode;
import net.peacesky.iahau.service.CourseService;
import net.peacesky.iahau.service.GradeService;
import net.peacesky.iahau.service.LinkService;
import net.peacesky.iahau.service.StudentInfoService;
import net.peacesky.iahau.util.CommonUtil;
import net.peacesky.iahau.util.GlobalDataUtil;
import net.peacesky.iahau.util.HttpUtil;
import net.peacesky.iahau.util.LinkUtil;
import net.peacesky.iahau.util.SharedPreferenceUtil;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.litepal.crud.DataSupport;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * @author Anderson
 */

public class MainActivity extends AppCompatActivity {

    private static final String FRAGMENT_TAG = "CURRENT_FRAGMENT";
    public SharedPreferenceUtil sharedPreferenceUtil, util;
    private long firstTime;
    private FrameLayout fl_content;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private TextView tvMoney, tvTips;
    private LinkService linkService;
    private StudentInfoService studentInfoService;
    private CourseService courseService;
    private GradeService gradeService;

    private AlertDialog notifyDialog;
    /**
     * 选项的事件监听器
     */
    DialogInterface.OnClickListener onclick = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            // TODO Auto-generated method stub
            Log.d("zafu", "选择了:" + GlobalDataUtil.yearSel + GlobalDataUtil.semesterSel);
            notifyDialog.dismiss();
        }

    };
    private View notifyView;
    private Toolbar toolbar;
    private int hourOfDay, minute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initValue();
        // getBasicInfo();
        getJWC();
        initView();

        if (savedInstanceState == null) setupFragment(new SimpleFramgment());
        initEvent();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) return true;
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {
        long secondTime = System.currentTimeMillis();
        drawerLayout.closeDrawers();
        if (secondTime - firstTime > 2000) {
//                Snackbar sb = Snackbar.make(fl_content, "再按一次退出", Snackbar.LENGTH_SHORT);
//                sb.getView().setBackgroundColor(getResources().getColor(android.R.color.holo_blue_dark));
//                sb.show();
            Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT).show();
            firstTime = secondTime;
        } else {
            finish();
        }
    }

    private void setupFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        // Fragment currentFragment = fragmentManager.findFragmentByTag(FRAGMENT_TAG);
        // if (true) {
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, fragment, FRAGMENT_TAG)
                .commit();
        // }
        // currentFragment == null || !currentFragment.getClass().equals(fragment.getClass())
    }

    private void onDrawerMenuSelected(int position) {
        drawerLayout.closeDrawers();
    }

    private void initValue() {
        sharedPreferenceUtil = new SharedPreferenceUtil(getApplicationContext(), "accountInfo");
        util = new SharedPreferenceUtil(getApplicationContext(), "LinkInfo");
        MyApplication application = ((MyApplication) getApplicationContext());
        linkService = application.getLinkService();
        studentInfoService = application.getStudentInfoService();
        courseService = application.getCourseService();
        gradeService = application.getGradeService();

        Calendar calendar = Calendar.getInstance();
        hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);
        // TODO 根据是否登陆过判断是否要重新获取信息
//        if ( ! sharedPreferenceUtil.getBooleanData("userID")){
//
//        }
    }

    private void initEvent() {

    }

    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("校园助手");
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        RecyclerView drawerOptions = (RecyclerView) findViewById(R.id.drawer_options);
        setSupportActionBar(toolbar);

        LayoutInflater layoutInflater = LayoutInflater.from(getApplicationContext());
        notifyView = layoutInflater.inflate(R.layout.notify_dialog, (ViewGroup) findViewById(R.id.notify_view));
        tvMoney = (TextView) notifyView.findViewById(R.id.tvMoney);
        tvTips = (TextView) notifyView.findViewById(R.id.tvTips);

        tvMoney.setText("您的饭卡余额还有" + GlobalDataUtil.blanace + "元.");
        Log.d("zafu", GlobalDataUtil.blanace + "元.");
        try {
            if (Float.parseFloat(GlobalDataUtil.blanace) <= 5) {
                tvTips.setText("饭卡余额不足,请及时充值!");
            } else if (Float.parseFloat(GlobalDataUtil.blanace) >= 23333)
                tvTips.setText("抱歉!饭卡余额暂时不能获取!");
        } catch (NumberFormatException ne) {
            Log.d("zafu", GlobalDataUtil.blanace + "元");
        }


        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        drawerLayout.setStatusBarBackground(R.color.primary_dark);
        drawerLayout.setDrawerListener(drawerToggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        List<DrawerItem> drawerItems = Arrays.asList(
                new DrawerItem(DrawerItem.Type.HEADER).setText(sharedPreferenceUtil.getKeyData("Name")),
                new DrawerMenu().setIconRes(R.drawable.ic_group).setText(getString(R.string.menu_kcb)),
                new DrawerMenu().setIconRes(R.drawable.ic_map).setText(getString(R.string.menu_cjb)),
                new DrawerMenu().setIconRes(R.drawable.ic_person).setText(getString(R.string.menu_djb)),
                new DrawerItem(DrawerItem.Type.DIVIDER),
                new DrawerMenu().setIconRes(R.drawable.ic_settings).setText(getString(R.string.menu_set)),
                new DrawerItem(DrawerItem.Type.DIVIDER),
                new DrawerMenu().setIconRes(R.drawable.ic_person).setText(getString(R.string.menu_about)));
        drawerOptions.setLayoutManager(new LinearLayoutManager(this));
        DrawerItemAdapter adapter = new DrawerItemAdapter(drawerItems);
        adapter.setOnItemClickListener(new DrawerItemAdapter.OnItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                onDrawerMenuSelected(position);
                Log.d("zafu", "You Clicked " + position);
                switch (position) {
                    case 0:
                        break;
                    case 1:
                        getKB();
                        toolbar.setTitle("我的课表");
                        break;
                    case 2:
                        // getViewState();
                        // CustomDialog pickerDialog = new CustomDialog(MainActivity.this, R.style.pickerViewDialog, R.layout.pickerview_dialog);
                        // pickerDialog.show();
                        toolbar.setTitle("校园助手");
                        Intent mainToGrade = new Intent(MainActivity.this, GradeActivity.class);
                        startActivity(mainToGrade);
                        //finish();
                        break;
                    case 3:
                        toolbar.setTitle("校园助手");
                        Intent mainToReGrade = new Intent(MainActivity.this, ReGradeActivity.class);
                        startActivity(mainToReGrade);
                        break;
                    case 4:
                        break;
                    case 5:
                        Intent mainToSet = new Intent(MainActivity.this, SettingActivity.class);
                        startActivity(mainToSet);
                        break;
                    case 6:

                        break;
                    case 7:
                        Intent mainToAbout = new Intent(MainActivity.this, AboutActivity.class);
                        startActivity(mainToAbout);
                        break;
                    case 8:

                        break;
                    default:
                }
            }
        });
        drawerOptions.setAdapter(adapter);
        drawerOptions.setHasFixedSize(true);

        notifyDialog = new AlertDialog.Builder(MainActivity.this)
                .setTitle("温馨提示:")
                .setView(notifyView)
                .setPositiveButton("确定", onclick)
                .create();
        notifyDialog.show();


    }

    private boolean getJWC() {
        final ProgressDialog dialog = CommonUtil.getProcessDialog(
                MainActivity.this, "正在登陆教务处网站！");
        dialog.show();

        // HttpUtil.getClient().addHeader("Referer", HttpUtil.URL_REFER);
        // String url = util.getKeyData("教务系统");
        String url = HttpUtil.getURL_JWXTRE();
        Log.d("zafu", "教务处的链接是：" + url);
        // HttpUtil.getClient().addHeader("Referer", HttpUtil.JWXT_URL_REFER);
        // HttpUtil.getClient().addHeader("Referer","http://i.ahau.edu.cn/index.portal");
        HttpUtil.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                dialog.dismiss();
                Log.d("zafu", "statusCode = " + arg0);
                Log.d("zafu", "这个是真实的地址" + HttpUtil.getURL_JWXTRE());
                Log.d("zafu", "这个是真实的中间地址" + HttpUtil.getUrlJwxtremid());
                DataSupport.deleteAll(LinkNode.class);

                String resultContent;
                try {
                    resultContent = new String(arg2, "gb2312");
                    //Log.d("zafu", resultContent);
                    Document content = Jsoup.parse(resultContent);

                    //StringBuilder result = new StringBuilder();
                    Elements elements = content.select("ul.nav a[target=zhuti]");
                    for (Element element : elements) {
                        // 将菜单选项的链接存储下来
                        LinkNode linkNode = new LinkNode();
                        linkNode.setTitle(element.html());
                        linkNode.setLink(HttpUtil.getUrlJwxtremid() + element.attr("href"));
                        Log.d("zafu", "链接" + linkNode.getTitle() + "\n" + linkNode.getLink() + "\n\n");
                        linkService.save(linkNode);
                    }


                } catch (UnsupportedEncodingException e) {

                    e.printStackTrace();
                }
                util.setKeyData("JWCResult", true);
            }

            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2,
                                  Throwable arg3) {
                Toast.makeText(getApplicationContext(), "教务处进入失败！！！",
                        Toast.LENGTH_SHORT).show();

                util.setKeyData("JWCResult", false);
            }
        });
        dialog.dismiss();
        return util.getBooleanData("JWCResult");
    }

    /**
     * 获取课表，存入数据库
     */
    private void getKB() {
        final ProgressDialog dialog = CommonUtil.getProcessDialog(
                MainActivity.this, "正在获取课表！！！");
        dialog.show();

        // String link = util.getKeyData("教务系统")+util.getKeyData(LinkUtil.XSGRKB);
        List<LinkNode> tmp = DataSupport.where("title = ?", LinkUtil.XSGRKB).find(LinkNode.class);
        if (0 == tmp.size()) {
            Toast.makeText(getApplicationContext(), "教务系统访问异常", Toast.LENGTH_SHORT).show();
        } else {
            String link = DataSupport.where("title = ?", LinkUtil.XSGRKB).find(LinkNode.class).get(0).getLink();
            Log.d("zafu", "完整的重定向后的教务系统课程表地址：" + link);
            if (!link.equals("")) {
                HttpUtil.URL_QUERY = link;
            } else {
                Toast.makeText(getApplicationContext(), "链接出现错误",
                        Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // HttpUtil.getClient().setURLEncodingEnabled(true);
        String[] para2 = null, para3 = null;
        String[] para = HttpUtil.URL_QUERY.split("[?]");
        if (para.length > 1) {
            para2 = para[1].split("[&]");
            para3 = para2[1].split("[=]");
            try {
                para3[1] = URLEncoder.encode(para3[1], "gbk");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            HttpUtil.URL_QUERY = para[0] + "?" + para2[0] + "&" + para3[0] + "=" + para3[1] + "&" + para2[2];
        }


        URI uri = null;
        try {
            uri = new URI(HttpUtil.URL_QUERY);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        Log.d("uri", uri.toString());
        String tmp3 = uri.toASCIIString();
        // String url = String.format(Constants.)
        // 如果不设置Referer将会无限重定向
        String referer = "http://zf.ahau.edu.cn/xs_main.aspx?xh=" + GlobalDataUtil.studentID;
        // HttpUtil.getClient().addHeader("Referer", "http://zf.ahau.edu.cn/xs_main.aspx?xh=13100501");
        HttpUtil.getClient().addHeader("Referer", referer);

        HttpUtil.URL_QUERY = "http://zf.ahau.edu.cn/xskbcx_jzjk.aspx";

        HttpUtil.get(HttpUtil.URL_QUERY, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                dialog.dismiss();
                String resultContent;
                try {
                    resultContent = new String(arg2, "gb2312");
                    DataSupport.deleteAll(Course.class);
                    courseService.parseCourse(resultContent);
                    Log.d("zafu", "课程表内容：\n" + resultContent);
                    //Document content = Jsoup.parse(resultContent);

                    Toast.makeText(getApplicationContext(), "课表获取成功！！！",
                            Toast.LENGTH_SHORT).show();

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                setupFragment(new SimpleFramgment());
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