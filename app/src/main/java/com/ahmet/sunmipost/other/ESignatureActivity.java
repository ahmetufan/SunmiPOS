/*
package com.ahmet.sunmipost.other;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.View;

import com.ahmet.sunmipost.BaseAppCompatActivity;
import com.ahmet.sunmipost.MyApplication;
import com.ahmet.sunmipost.R;
import com.ahmet.sunmipost.utils.BitmapUtils;
import com.ahmet.sunmipost.utils.LogUtil;
import com.ahmet.sunmipost.view.LinePathView;
import com.sunmi.peripheral.printer.InnerResultCallbcak;
import com.sunmi.peripheral.printer.SunmiPrinterService;

import sunmi.sunmiui.utils.BitmapUtil;

public class ESignatureActivity extends BaseAppCompatActivity {

    private LinePathView handWriteView;
    private SunmiPrinterService sunmiPrinterService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_esignature);

        initToolbarBringBack(R.string.e_signature);

        sunmiPrinterService = MyApplication.sunmiPrinterService;

        iniView();
    }

    private void iniView() {

        handWriteView = this.findViewById(R.id.hand_write_view);
        handWriteView.clear();
        this.findViewById(R.id.tv_clear).setOnClickListener(this);
        this.findViewById(R.id.tv_ok).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.tv_clear:
                handWriteView.clear();
                break;

            case R.id.tv_ok:
                showLoadingDialog(getString(R.string.handling) + "...");
                new Thread(() -> {

                    try {

                        Bitmap cacheBitmap = handWriteView.getCacheBitmap();
                        cacheBitmap = BitmapUtils.scale(cacheBitmap, cacheBitmap.getWidth() / 3, cacheBitmap.getHeight() / 3);
                        cacheBitmap = BitmapUtils.replaceBitmapColor(cacheBitmap, Color.TRANSPARENT, Color.WHITE);
                        if (cacheBitmap.getWidth() > 384) {
                            int newHeight = (int) (1.0 * cacheBitmap.getHeight() * 384 / cacheBitmap.getWidth());
                            cacheBitmap = BitmapUtils.scale(cacheBitmap, 384, newHeight);
                        }
                        sunmiPrinterService.enterPrinterBuffer(true);
                        sunmiPrinterService.setAlignment(1, innerResultCallbcak);
                        sunmiPrinterService.printBitmap(cacheBitmap, innerResultCallbcak);
                        sunmiPrinterService.setAlignment(0, innerResultCallbcak);
                        sunmiPrinterService.lineWrap(4, innerResultCallbcak);
                        sunmiPrinterService.exitPrinterBufferWithCallback(true, innerResultCallbcak);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }).start();
                break;
        }
    }

    private InnerResultCallbcak innerResultCallbcak = new InnerResultCallbcak() {

        @Override
        public void onRunResult(boolean isSuccess) throws RemoteException {
            LogUtil.e("lxy", "isSuccess:" + isSuccess);
            dismissLoadingDialog();
        }

        @Override
        public void onReturnString(String result) throws RemoteException {
            LogUtil.e("lxy", "result:" + result);
        }

        @Override
        public void onRaiseException(int code, String msg) throws RemoteException {
            LogUtil.e("lxy", "code:" + code + ",msg:" + msg);
        }

        @Override
        public void onPrintResult(int code, String msg) throws RemoteException {
            LogUtil.e("lxy", "code:" + code + ",msg:" + msg);
        }
    };


}*/
