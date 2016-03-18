package net.peacesky.iahau.view;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.Button;

import net.peacesky.iahau.R;
import net.peacesky.iahau.util.GlobalDataUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SuooL on 15/10/8.
 */
public class CustomDialog extends AlertDialog {

    int layoutRes;//布局文件
    Context context;
    private PickerView year_pv;
    private PickerView semester_pv;
    private Button positiveBtn;

    public CustomDialog(Context context) {
        super(context);
        // this.layoutRes = R.layout.pickerview_dialog;
        this.context = context;
    }

    /**
     * 自定义布局的构造方法
     *
     * @param context
     * @param resLayout
     */
    public CustomDialog(Context context, int resLayout) {
        super(context);
        this.context = context;
        this.layoutRes = resLayout;
    }

    /**
     * 自定义主题及布局的构造方法
     *
     * @param context
     * @param theme
     * @param resLayout
     */
    public CustomDialog(Context context, int theme, int resLayout) {
        super(context, theme);
        this.context = context;
        this.layoutRes = resLayout;
    }


    protected CustomDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setTitle("设置时间");

        this.setContentView(layoutRes);
        year_pv = (PickerView) findViewById(R.id.year_pv);
        semester_pv = (PickerView) findViewById(R.id.semester_pv);
        // positiveBtn = (Button) findViewById(R.id.getCJ);

        List<String> year = new ArrayList<String>();
        List<String> semester = new ArrayList<String>();

        year.add(0, "全部");
        year.add(1, "2012-2013");
        year.add(2, "2013-2014");
        year.add(3, "2014-2015");
        year.add(4, "2015-2016");

        semester.add(0, "全部");
        semester.add(1, "上");
        semester.add(2, "下");

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
//                Toast.makeText(getContext(), "选择了 " + text + "学期",
//                        Toast.LENGTH_SHORT).show();
                GlobalDataUtil.semesterSel = text;
            }
        });
//        positiveBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                Log.d("zafu", "选择了:"+GlobalDataUtil.yearSel+GlobalDataUtil.semesterSel );
//                dismiss();
//            }
//        });

    }
}
