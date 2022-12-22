package com.ahmet.sunmipost.utils;

import android.os.Bundle;

import java.util.Locale;
import java.util.Set;

public final class Utility {

    private Utility() {
        throw new AssertionError("Utility örneği oluşturmak yasaktır.");
    }

    /**
     * Bundle nesnesini bir dizgeye dönüştürün
     */
    public static String bundle2String(Bundle bundle) {
        if (bundle == null || bundle.keySet().isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        Set<String> set = bundle.keySet();
        for (String key : set) {
            sb.append(key);
            sb.append(":");
            sb.append(bundle.get(key));
            sb.append("\n");
        }
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    /**
     * Null'u boş dizgeye dönüştür
     */

    public static String null2String(String str) {
        return str == null ? "" : str;
    }

    public static String formatStr(String format, Object... params) {
        return String.format(Locale.getDefault(), format, params);
    }
}
