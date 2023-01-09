package com.ahmet.sunmipost.utils;

import android.os.Build;

public class DeviceUtil {

    private DeviceUtil() {
        throw new AssertionError("create instance of DeviceUtil is prohibited");
    }


    /** Cihaz modelini al */
    public static String getModel() {
        return Build.MODEL;
    }

    /** P1N mi */
    public static boolean isP1N() {
        String model = Build.MODEL.toLowerCase();
        return model.matches("p1n(-.+)?");
    }

    /** P1_4G mi */
    public static boolean isP14G() {
        String model = Build.MODEL.toLowerCase();
        return model.matches("p1_4g(-.+)?");
    }

    /** P2lite mi */
    public static boolean isP2Lite() {
        String model = Build.MODEL.toLowerCase();
        return model.matches("p2lite(-.+)?");
    }

    /** P2_PRO mu */
    public static boolean isP2Pro() {
        String model = Build.MODEL.toLowerCase();
        return model.matches("p2_pro(-.+)?");
    }

    /** P2 mi */
    public static boolean isP2() {
        String model = Build.MODEL.toLowerCase();
        return model.matches("p2(-.+)?");
    }

    /** V2_PRO mu */
    public static boolean isV2Pro() {
        String model = Build.MODEL.toLowerCase();
        return model.matches("v2_pro(-.+)?");
    }

    /** V1S mi */
    public static boolean isV1s() {
        String model = Build.MODEL.toLowerCase();
        return model.matches("v1s(-.+)?");
    }
}
