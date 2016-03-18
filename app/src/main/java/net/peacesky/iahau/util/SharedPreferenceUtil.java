package net.peacesky.iahau.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by SuooL on 15/9/27.
 */
public class SharedPreferenceUtil {
    private Context mContext;
    private String mFileName;

    public SharedPreferenceUtil(Context context, String fileName) {
        mContext = context;
        this.mFileName = fileName;
    }

    /**
     * 保存键值对
     *
     * @param key
     * @param value
     */
    public void setKeyData(String key, String value) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(mFileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public void setKeyData(String key, Boolean value) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(mFileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    /**
     * 根据键得到值，如果为空返回""
     *
     * @param key
     * @return
     */
    public String getKeyData(String key) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(mFileName, Context.MODE_PRIVATE);
        String value = sharedPreferences.getString(key, "");//第二个参数为默认值
        return value;
    }

    public Boolean getBooleanData(String key) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(mFileName, Context.MODE_PRIVATE);
        Boolean value = sharedPreferences.getBoolean(key, false);//第二个参数为默认值
        return value;
    }


}