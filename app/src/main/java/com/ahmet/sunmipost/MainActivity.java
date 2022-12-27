package com.ahmet.sunmipost;

import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.ahmet.sunmipost.print.PrintActivity;
import com.ahmet.sunmipost.process.EmvActivity;
import com.ahmet.sunmipost.readCard.CardActivity;
import com.ahmet.sunmipost.scan.ScanActivity;
import com.ahmet.sunmipost.utils.Constant;
import com.ahmet.sunmipost.utils.LogUtil;

import sunmi.paylib.SunmiPayKernel;

public class MainActivity extends BaseAppCompatActivity {

    private SunmiPayKernel mSMPayKernel;

    private boolean isDisConnectService = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {  //   -->>  protected change public  <------
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();

        connectPayService();
    }


    private void initView() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("VBTSunmiPos");
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));

        // Read Card
        findViewById(R.id.card_view_card).setOnClickListener(this);
        // Print
        findViewById(R.id.card_view_print).setOnClickListener(this);
        // Scan
        findViewById(R.id.card_view_scan).setOnClickListener(this);
        // IC/NFC Process
        findViewById(R.id.card_view_emv).setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        if (isDisConnectService) {
            connectPayService();
            showToast(R.string.connect_loading);
            return;
        }
        final int id = view.getId();
        switch (id) {

            case R.id.card_view_card:
                openActivity(CardActivity.class);
                break;

            case R.id.card_view_print:
                openActivity(PrintActivity.class);
                break;

            case R.id.card_view_scan:
                openActivity(ScanActivity.class);
                break;

            case R.id.card_view_emv:
                openActivity(EmvActivity.class);
                break;
        }
    }


    private void connectPayService() {
        mSMPayKernel = SunmiPayKernel.getInstance();
        mSMPayKernel.initPaySDK(this, mConnectCallback);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mSMPayKernel != null) {
            mSMPayKernel.destroyPaySDK();
        }
    }

    private SunmiPayKernel.ConnectCallback mConnectCallback = new SunmiPayKernel.ConnectCallback() {
        @Override
        public void onConnectPaySDK() {
            LogUtil.e(Constant.TAG, "onConnectPaySDK");
            try {
                MyApplication.mEMVOptV2 = mSMPayKernel.mEMVOptV2;
                MyApplication.mBasicOptV2 = mSMPayKernel.mBasicOptV2;
                MyApplication.mPinPadOptV2 = mSMPayKernel.mPinPadOptV2;
                MyApplication.mReadCardOptV2 = mSMPayKernel.mReadCardOptV2;
                MyApplication.mSecurityOptV2 = mSMPayKernel.mSecurityOptV2;
                MyApplication.mTaxOptV2 = mSMPayKernel.mTaxOptV2;
                MyApplication.mETCOptV2 = mSMPayKernel.mETCOptV2;
                isDisConnectService = false;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onDisconnectPaySDK() {
            LogUtil.e(Constant.TAG, "onDisconnectPaySDK");
            isDisConnectService = true;
            showToast(R.string.connect_fail);
        }
    };

    public static void reStart(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }


}