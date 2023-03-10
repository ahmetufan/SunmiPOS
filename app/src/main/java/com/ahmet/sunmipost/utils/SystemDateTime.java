package com.ahmet.sunmipost.utils;

import android.app.AlarmManager;
import android.content.Context;
import android.os.SystemClock;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import sunmi.sunmiui.utils.LogUtil;

public class SystemDateTime {

    public static void setDateTime(int year, int month, int day, int hour, int minute) throws IOException, InterruptedException {
        requestPermission();
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month - 1);
        c.set(Calendar.DAY_OF_MONTH, day);
        c.set(Calendar.HOUR_OF_DAY, hour);
        c.set(Calendar.MINUTE, minute);

        long when = c.getTimeInMillis();
        if (when / 1000 < Integer.MAX_VALUE) {
            SystemClock.setCurrentTimeMillis(when);
        }
        long now = Calendar.getInstance().getTimeInMillis();
        if (now - when > 1000)
            throw new IOException("failed to set Date.");
    }

    public static void setDate(int year, int month, int day) throws IOException, InterruptedException {
        requestPermission();
        Calendar c = Calendar.getInstance();

        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, day);
        long when = c.getTimeInMillis();

        if (when / 1000 < Integer.MAX_VALUE) {
            SystemClock.setCurrentTimeMillis(when);
        }
        long now = Calendar.getInstance().getTimeInMillis();
        if (now - when > 1000)
            throw new IOException("failed to set Date.");
    }

    public static void setTime(int hour, int minute) throws IOException, InterruptedException {
        requestPermission();
        Calendar c = Calendar.getInstance();

        c.set(Calendar.HOUR_OF_DAY, hour);
        c.set(Calendar.MINUTE, minute);
        long when = c.getTimeInMillis();

        if (when / 1000 < Integer.MAX_VALUE) {
            SystemClock.setCurrentTimeMillis(when);
        }
        long now = Calendar.getInstance().getTimeInMillis();
        if (now - when > 1000)
            throw new IOException("failed to set Time.");
    }

    static void requestPermission() throws InterruptedException, IOException {
        createSuProcess("chmod 666 /dev/alarm").waitFor();
    }

    static Process createSuProcess() throws IOException {
        File rootUser = new File("/system/xbin/ru");
        if (rootUser.exists()) {
            return Runtime.getRuntime().exec(rootUser.getAbsolutePath());
        } else {
            return Runtime.getRuntime().exec("su");
        }
    }

    static Process createSuProcess(String cmd) throws IOException {
        DataOutputStream os = null;
        Process process = createSuProcess();
        try {
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes(cmd + "\n");
            os.writeBytes("exit $?\n");
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                }
            }
        }
        return process;
    }

    /**
     * Sistem saatini ayarla
     */
    public static void setSysTime(int hour, int minute, int second, Context mContext) throws Exception {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, hour);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, second);
        c.set(Calendar.MILLISECOND, 0);

        long when = c.getTimeInMillis();

        if (when / 1000 < Integer.MAX_VALUE) {
            ((AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE)).setTime(when);
        }
    }

    /**
     * Sistem tarihini ayarla
     */
    public static void setSysDate(int year, int month, int day, Context mContext) throws Exception {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, day);

        long when = c.getTimeInMillis();

        if (when / 1000 < Integer.MAX_VALUE) {
            ((AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE)).setTime(when);
        }
    }

    /**
     * zaman damgas??na g??re tarih al
     *
     * @param
     * @return
     */
    public static String getDateByStamp(long stamp) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String sd = sdf.format(new Date(Long.parseLong(String.valueOf(stamp))));
            return sd;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Zaman damgas?? ile zaman al??n
     *
     * @param
     * @return
     */
    public static String getTimeByStamp(long stamp) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            String sd = sdf.format(new Date(Long.parseLong(String.valueOf(stamp))));
            return sd;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * Ge??erli y??l??, ay?? ve g??n?? al??n
     */
    public static String getDateString() {
        final Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        String mYear = String.valueOf(c.get(Calendar.YEAR));// Ge??erli y??l?? al
        String mMonth = String.valueOf(c.get(Calendar.MONTH) + 1);// Ge??erli ay?? al
        String mDay = String.valueOf(c.get(Calendar.DAY_OF_MONTH));// Ge??erli ay??n g??n numaras??n?? al
        return mYear + "-" + (mMonth.length() == 1 ? "0" + mMonth : mMonth) + "-" + (mDay.length() == 1 ? "0" + mDay : mDay);
    }

    /**
     * Bug??nden birka?? g??n sonraki tarihi al??n (ay ve tarih)
     */
    public static String getAfterDaydate(int afterDay) {
        Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        int mCurrentDay = c.get(Calendar.DAY_OF_MONTH);
        c.set(Calendar.DAY_OF_MONTH, mCurrentDay + afterDay);
        String mDay = String.valueOf(c.get(Calendar.DAY_OF_MONTH));
        String mMonth = String.valueOf(c.get(Calendar.MONTH) + 1);
        String mYear = String.valueOf(c.get(Calendar.YEAR));
        return mYear + "-" + (mMonth.length() == 1 ? "0" + mMonth : mMonth) + "-" + (mDay.length() == 1 ? "0" + mDay : mDay);
    }

    public static String getYYMM() {
        SimpleDateFormat date = new SimpleDateFormat("yyMM");
        Date curDate = new Date(System.currentTimeMillis());//  ??imdiki zaman?? al
        String strDate = date.format(curDate);
        return strDate;
    }

    public static String getMMDD() {
        SimpleDateFormat date = new SimpleDateFormat("MM/dd");
        Date curDate = new Date(System.currentTimeMillis());//??imdiki zaman?? al
        String strDate = date.format(curDate);
        return strDate;
    }

    public static String getYYYY() {
        SimpleDateFormat date = new SimpleDateFormat("yyyy");
        Date curDate = new Date(System.currentTimeMillis());//  ??imdiki zaman?? al
        String strDate = date.format(curDate);
        return strDate;
    }

    public static String getHHmmss() {
        SimpleDateFormat time = new SimpleDateFormat("HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());//  ??imdiki zaman?? al
        String strTime = time.format(curDate);
        return strTime;
    }

    public static String getCurrentTime(long stamp) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd/HH/mm/ss");
            String sd = sdf.format(new Date(Long.parseLong(String.valueOf(stamp))));
            return sd;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getCurrentDate(long stamp) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            String sd = sdf.format(new Date(Long.parseLong(String.valueOf(stamp))));
            return sd;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static int getCurrentYYYYMMDD() {
        try {
            long timeMillis = System.currentTimeMillis();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
            String snow = sdf.format(timeMillis);
            int parseInt = Integer.parseInt(snow);
            LogUtil.i("nsz", "parseInt:" + parseInt);
            return parseInt;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}
