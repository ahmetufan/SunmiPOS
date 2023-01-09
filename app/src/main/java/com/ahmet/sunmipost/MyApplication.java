package com.ahmet.sunmipost;

import android.app.Application;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.IBinder;
import android.util.DisplayMetrics;

import androidx.annotation.NonNull;

import com.ahmet.sunmipost.utils.Constant;
import com.ahmet.sunmipost.utils.LogUtil;
import com.sunmi.pay.hardware.aidlv2.emv.EMVOptV2;
import com.sunmi.pay.hardware.aidlv2.etc.ETCOptV2;
import com.sunmi.pay.hardware.aidlv2.pinpad.PinPadOptV2;
import com.sunmi.pay.hardware.aidlv2.readcard.ReadCardOptV2;
import com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2;
import com.sunmi.pay.hardware.aidlv2.system.BasicOptV2;
import com.sunmi.pay.hardware.aidlv2.tax.TaxOptV2;
import com.sunmi.peripheral.printer.InnerPrinterCallback;
import com.sunmi.peripheral.printer.InnerPrinterException;
import com.sunmi.peripheral.printer.InnerPrinterManager;
import com.sunmi.peripheral.printer.SunmiPrinterService;
import com.sunmi.scanner.IScanInterface;

import java.util.Locale;

public class MyApplication extends Application {

    public static Context context;

    public static BasicOptV2 mBasicOptV2;           // Temel işlem modülünü edinin
    public static ReadCardOptV2 mReadCardOptV2;     // Kart okuyucu modülünü edinin
    public static PinPadOptV2 mPinPadOptV2;         // PinPad Eylem Modülünü Alın
    public static SecurityOptV2 mSecurityOptV2;     // Güvenlik Operasyonları Modülünü Alın
    public static EMVOptV2 mEMVOptV2;               // EMV Operasyon Modülünü Alın
    public static TaxOptV2 mTaxOptV2;               // Vergi kontrolü operasyon modülünü edinin
    public static ETCOptV2 mETCOptV2;               // ETC operasyon modülünü edinin
    public static SunmiPrinterService sunmiPrinterService;  // Printer modülü
    public static IScanInterface scanInterface;


    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();

        initLocaleLanguage();

        bindPrintService();

        bindScannerService();


    }


    public static void initLocaleLanguage() {
        Resources resources = MyApplication.getContext().getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        Configuration config = resources.getConfiguration();

        int showLanguage = CacheHelper.getCurrentLanguage();

        if (showLanguage == Constant.LANGUAGE_AUTO) {
            LogUtil.e(Constant.TAG, config.locale.getCountry() + "---Bu sistem dili");
            config.locale = Resources.getSystem().getConfiguration().locale;
        } else if (showLanguage == Constant.LANGUAGE_TR_TR) {
            LogUtil.e(Constant.TAG, "TÜRKÇE");
            config.locale = Locale.getDefault();
        } else if (showLanguage == Constant.LANGUAGE_EN_US) {
            LogUtil.e(Constant.TAG, "ENGLİSH");
            config.locale = Locale.ENGLISH;
        }
        resources.updateConfiguration(config, dm);
    }


    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        LogUtil.e(Constant.TAG, "onConfigurationChanged");
    }

    public static Context getContext() {
        return context;
    }

    private void bindPrintService() {

        try {
            InnerPrinterManager.getInstance().bindService(this, new InnerPrinterCallback() {
                @Override
                protected void onConnected(SunmiPrinterService service) {
                    MyApplication.sunmiPrinterService = service;
                }

                @Override
                protected void onDisconnected() {
                    MyApplication.sunmiPrinterService = null;
                }
            });


        } catch (InnerPrinterException e) {
            e.printStackTrace();
        }
    }

    public void bindScannerService() {
        Intent intent = new Intent();
        intent.setPackage("com.sunmi.scanner");
        intent.setAction("com.sunmi.scanner.IScanInterface");
        bindService(intent, scanConn, Service.BIND_AUTO_CREATE);
    }

    private static ServiceConnection scanConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            scanInterface = IScanInterface.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            scanInterface = null;
        }
    };


}
