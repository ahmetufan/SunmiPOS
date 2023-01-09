package com.ahmet.sunmipost;

import android.content.Context;
import android.content.SharedPreferences;

import com.ahmet.sunmipost.utils.Constant;

public class CacheHelper {

    private static final String PREFERENCE_FILE_NAME = "com.ahmet.sunmipost";

    private static final String KEY_LANGUAGE = "key_language";

    public static void saveCurrentLanguage(int language) {
        SharedPreferences sharedPreferences = MyApplication.getContext().getSharedPreferences(PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);
        int value = sharedPreferences.getInt(KEY_LANGUAGE, Constant.LANGUAGE_AUTO);
        if (value == language) return;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_LANGUAGE, language);
        editor.apply();
    }

    public static int getCurrentLanguage() {
        SharedPreferences sharedPreferences = MyApplication.getContext().getSharedPreferences(PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(KEY_LANGUAGE, Constant.LANGUAGE_AUTO);
    }

}
