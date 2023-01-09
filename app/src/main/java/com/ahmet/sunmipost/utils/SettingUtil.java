package com.ahmet.sunmipost.utils;

import android.text.TextUtils;

import com.ahmet.sunmipost.MyApplication;
import com.sunmi.pay.hardware.aidlv2.AidlConstantsV2;

import org.json.JSONObject;

public final class SettingUtil {

    private static final String TAG = Constant.TAG;
    private static final String KEY_BUZZER = "buzzer";
    private static final String KEY_SUPPORT_KEY_PARTITION = "supportKeyPartition";
    private static final String KEY_PSAM_CHANNEL = "psamChannel";
    private static final String KEY_AUTO_RESTORE_NFC = "autoRestoreNfc";
    private static final String KEY_MAX_ONLINE_TIME = "maxOnlineTime";
    private static final int DEFAULT_CHANNEL_COUNT = 2;

    private SettingUtil() {
        throw new AssertionError("Create instance of SettingUtil is prohibited");
    }

    /** Anahtar bölümlemenin desteklenip desteklenmediğini öğrenin*/
    public static boolean getSupportKeyPartition() {
        try {
            String jsonStr = MyApplication.mBasicOptV2.getSysParam(AidlConstantsV2.SysParam.RESERVED);
            if (!TextUtils.isEmpty(jsonStr)) {
                JSONObject jobj = new JSONObject(jsonStr);
                if (jobj.has(KEY_SUPPORT_KEY_PARTITION)) {
                    LogUtil.e(TAG, "has keypartition");
                    return jobj.getBoolean(KEY_SUPPORT_KEY_PARTITION);
                }
            }
            if (DeviceUtil.isP1N() || DeviceUtil.isP14G()) {//P1N/P14G, varsayılan olarak bölmeyi desteklemez
                return false;
            }
            return true;
        } catch (Exception e) {
            LogUtil.e(TAG, "SettingUtil getSupportKeyPartition:" + e);
            e.printStackTrace();
        }
        return true;
    }

    /** Anahtar bölümlemenin desteklenip desteklenmeyeceğini ayarlayın */
    public static void setSupportKeyPartition(boolean enable) {
        try {
            JSONObject jobj = new JSONObject();
            String jsonStr = MyApplication.mBasicOptV2.getSysParam(AidlConstantsV2.SysParam.RESERVED);
            if (!TextUtils.isEmpty(jsonStr)) {
                jobj = new JSONObject(jsonStr);
            }
            jobj.put(KEY_SUPPORT_KEY_PARTITION, enable);
            MyApplication.mBasicOptV2.setSysParam(AidlConstantsV2.SysParam.RESERVED, jobj.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * V1S, PSAM kanalını alır
     * <br/>V1S iki PSAM kanalı içerir: kanal 1 ve kanal 2
     *
     * @return >0-mevcut PSAM kanalı,<0-atanan kanal yok
     */
    public static int getPSAMChannel() {
        int channel = -1;
        try {
            String jsonStr = MyApplication.mBasicOptV2.getSysParam(AidlConstantsV2.SysParam.RESERVED);
            if (!TextUtils.isEmpty(jsonStr)) {
                JSONObject jobj = new JSONObject(jsonStr);
                if (jobj.has(KEY_PSAM_CHANNEL)) {
                    channel = jobj.getInt(KEY_PSAM_CHANNEL);
                    LogUtil.e(TAG, "has psam channel:" + channel);
                    return channel;
                }
            }
        } catch (Exception e) {
            LogUtil.e(TAG, "SettingUtil getPSAMChannel:" + e);
            e.printStackTrace();
        }
        return channel;
    }

    /**
     * V1SPSAM kanalını değiştir
     * <br/>V1S iki PSAM kanalı içerir: kanal 1 ve kanal 2
     */
    public static void switchPSAMChannel() {
        try {
            int curChannel = -1;
            JSONObject jobj = new JSONObject();
            String jsonStr = MyApplication.mBasicOptV2.getSysParam(AidlConstantsV2.SysParam.RESERVED);
            if (!TextUtils.isEmpty(jsonStr)) {
                jobj = new JSONObject(jsonStr);
                if (jobj.has(KEY_PSAM_CHANNEL)) {
                    LogUtil.e(TAG, "has keypartition");
                    curChannel = jobj.getInt(KEY_PSAM_CHANNEL);
                }
            }
            if (curChannel < 0) {
                curChannel = 1;
            } else if (curChannel < DEFAULT_CHANNEL_COUNT) {
                curChannel++;
            } else if (curChannel == DEFAULT_CHANNEL_COUNT) {
                curChannel = -1;
            }
            jobj.put(KEY_PSAM_CHANNEL, curChannel);
            MyApplication.mBasicOptV2.setSysParam(AidlConstantsV2.SysParam.RESERVED, jobj.toString());
            LogUtil.e(TAG, "switch psam channel to " + curChannel + " success");
        } catch (Exception e) {
            LogUtil.e(TAG, "SettingUtil getSupportKeyPartition:" + e);
            e.printStackTrace();
        }
    }

    /** NFC işlevinin otomatik olarak geri yüklenip yüklenmeyeceğini öğrenin (sürekli kart okumanın NFC işlevi anormal olduğunda otomatik olarak geri yüklenip yüklenmeyeceği)*/
    public static boolean getAutoRestoreNfc() {
        boolean autoRestoreNfc = false;
        try {
            String jsonStr = MyApplication.mBasicOptV2.getSysParam(AidlConstantsV2.SysParam.RESERVED);
            if (TextUtils.isEmpty(jsonStr)) {
                return false;
            }
            JSONObject jobj = new JSONObject(jsonStr);
            if (!jobj.has(KEY_AUTO_RESTORE_NFC)) {
                return false;
            }
            autoRestoreNfc = jobj.getBoolean(KEY_AUTO_RESTORE_NFC);
            LogUtil.e(TAG, "autoRestoreNfc:" + autoRestoreNfc);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return autoRestoreNfc;
    }

    /**
     * NFC işlevinin otomatik olarak geri yüklenip yüklenmeyeceğini ayarlayın. Şu anda bu yapılandırma yalnızca V2Pro'da kullanılmaktadır.
     * <br/> autoRestoreNfc'yi true olarak ayarlarsanız, NFC okuma kartı istisnası oluştuğunda,
     * SDK, NFC'yi kapatıp yeniden açarak NFC işlevini geri yüklemeye çalışacaktır
     */
    public static void setAutoRestoreNfc(boolean enable) {
        try {
            JSONObject jobj = new JSONObject();
            String jsonStr = MyApplication.mBasicOptV2.getSysParam(AidlConstantsV2.SysParam.RESERVED);
            if (!TextUtils.isEmpty(jsonStr)) {
                jobj = new JSONObject(jsonStr);
            }
            jobj.put(KEY_AUTO_RESTORE_NFC, enable);
            MyApplication.mBasicOptV2.setSysParam(AidlConstantsV2.SysParam.RESERVED, jobj.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * SDK yerleşik PinPad modunu ayarlayın
     *
     * @param mode PinPad modu, bkz.{@link AidlConstantsV2.PinPadMode}
     * @return code >=0-success, <0-failed
     */
    public static int setPinPadMode(String mode) {
//        int code = -1;
//        try {
//            code = MyApplication.mBasicOptV2.setSysParam(AidlConstantsV2.SysParam.PINPAD_MODE, mode);
//            if (code < 0) {
//                LogUtil.e(TAG, "setPinPadMode failed,code:" + code);
//            }
//        } catch (RemoteException e) {
//            e.printStackTrace();
//        }
//        return code;
        return 0;
    }

    /**
     * EMV maksimum çevrimiçi zamanı ayarla
     *
     * @param maxOnlineTime maksimum çevrimiçi süre, unit: s
     *                      <br/> (1) maxOnlineTime<=60s ayarlanırsa, SDK varsayılan değer olan 60s'yi kullanır.
     *                      <br/> (2) maxOnlineTime>60 ayarlanırsa, SDK ayarlanan değeri kullanır.
     *                      <br/>Note: Ayarlanan maxOnlineTime, bir String değil, bir int değeri olmalıdır.
     * @return code >=0-success, <0-failed
     */
    public static int setEmvMaxOnlineTime(int maxOnlineTime) {
        int code = -1;
        try {
            JSONObject jobj = new JSONObject();
            String jsonStr = MyApplication.mBasicOptV2.getSysParam(AidlConstantsV2.SysParam.RESERVED);
            if (!TextUtils.isEmpty(jsonStr)) {
                jobj = new JSONObject(jsonStr);
            }
            jobj.put(KEY_MAX_ONLINE_TIME, maxOnlineTime);
            code = MyApplication.mBasicOptV2.setSysParam(AidlConstantsV2.SysParam.RESERVED, jobj.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return code;
    }

    /**
     * Enable/Disable kontrol kartı başarılı olduğunda sesli uyarı
     * <br/> SDK'nın varsayılan eylemi, kart kontrolü başarılı olduğunda sesli uyarıdır.
     *
     * @param enable true-enable, false-disable
     */
    public static void setBuzzerEnable(boolean enable) {
        try {
            JSONObject jobj = new JSONObject();
            String jsonStr = MyApplication.mBasicOptV2.getSysParam(AidlConstantsV2.SysParam.RESERVED);
            if (!TextUtils.isEmpty(jsonStr)) {
                jobj = new JSONObject(jsonStr);
            }
            jobj.put(KEY_BUZZER, enable ? 1 : 0);
            MyApplication.mBasicOptV2.setSysParam(AidlConstantsV2.SysParam.RESERVED, jobj.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
