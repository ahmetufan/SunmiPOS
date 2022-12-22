package com.ahmet.sunmipost.print;

import androidx.annotation.NonNull;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.ahmet.sunmipost.BaseAppCompatActivity;
import com.ahmet.sunmipost.MyApplication;
import com.ahmet.sunmipost.R;
import com.ahmet.sunmipost.utils.Constant;
import com.ahmet.sunmipost.utils.LogUtil;
import com.ahmet.sunmipost.utils.SystemDateTime;
import com.sunmi.peripheral.printer.InnerResultCallbcak;
import com.sunmi.peripheral.printer.SunmiPrinterService;

public class PrintTextActivity extends BaseAppCompatActivity {

    private SunmiPrinterService sunmiPrinterService;
    private EditText etText, etTextSize;
    private EditText edtRepeatCount;
    private EditText edtWaitTime;
    private EditText edtIntervalTime;
    private Button btnPrint;
    private ScreenOnOffReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print_text);

        initToolbarBringBack(R.string.print_text);

        sunmiPrinterService = MyApplication.sunmiPrinterService;

        initView();
    }

    private void initView() {
        btnPrint = findViewById(R.id.btn_print);
        btnPrint.setOnClickListener(this);
        etText = findViewById(R.id.et_text);
        etTextSize = findViewById(R.id.et_text_size);
        etTextSize.setText("30");
        etText.setVisibility(View.GONE);
        edtRepeatCount = findViewById(R.id.edt_repeat_count);
        edtWaitTime = findViewById(R.id.edt_wait_time);
        edtIntervalTime = findViewById(R.id.edt_interval_time);
//        etText.setText(R.string.text_mock);
        findViewById(R.id.btn_screen_off_print).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.btn_print:
                onPrintClick();
                break;
            case R.id.btn_screen_off_print:
                onScreenOffPrintClick();
                break;
        }
    }

    private void onPrintClick() {
        try {
            String repeatStr = edtRepeatCount.getText().toString();
            if (TextUtils.isEmpty(repeatStr)) {
                showToast("Repeat count shouldn't be empty");
                edtRepeatCount.requestFocus();
                return;
            }
            int repeatCount = Integer.parseInt(repeatStr);
            int textSize = Integer.parseInt(etTextSize.getText().toString());
            String text = etText.getText().toString();
            setHeight(0x12);
            String hHmmss = SystemDateTime.getHHmmss();
            sunmiPrinterService.enterPrinterBuffer(true);
            sunmiPrinterService.printTextWithFont(hHmmss + "\n", "", textSize, innerResultCallbcak);

            for (int i = 0; i < repeatCount; i++) {
                sunmiPrinterService.printTextWithFont(getString(R.string.text_mock1) + "\n", "", textSize, innerResultCallbcak);  //  print to text ------------------------
            }
            sunmiPrinterService.exitPrinterBufferWithCallback(true, innerResultCallbcak);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onScreenOffPrintClick() {
        String waitTimeStr = edtWaitTime.getText().toString();
        if (TextUtils.isEmpty(waitTimeStr)) {
            showToast("Screen off wait time shouldn't be empty");
            edtWaitTime.requestFocus();
            return;
        }
        String intervalTimeStr = edtIntervalTime.getText().toString();
        if (TextUtils.isEmpty(intervalTimeStr)) {
            showToast("Screen off print interval time shouldn't be empty");
            edtIntervalTime.requestFocus();
            return;
        }
        if (receiver == null) {
            receiver = new ScreenOnOffReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_SCREEN_ON);
            filter.addAction(Intent.ACTION_SCREEN_OFF);
            registerReceiver(receiver, filter);
        }
        receiver.waitTime = Integer.parseInt(waitTimeStr);
        receiver.intervalTime = Integer.parseInt(intervalTimeStr);
        showToast("set screen off print success");
    }

    private boolean is = true;

    private InnerResultCallbcak innerResultCallbcak = new InnerResultCallbcak() {

        @Override
        public void onRunResult(boolean isSuccess) throws RemoteException {
            LogUtil.e("lxy", "isSuccess:" + isSuccess);
            if (is) {
                try {
                    sunmiPrinterService.printTextWithFont(SystemDateTime.getHHmmss() + "\n", "", 30, innerResultCallbcak);
                    sunmiPrinterService.lineWrap(6, innerResultCallbcak);
                    is = false;
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

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

    public void setHeight(int height) throws RemoteException {
        byte[] returnText = new byte[3];
        returnText[0] = 0x1B;
        returnText[1] = 0x33;
        returnText[2] = (byte) height;
        sunmiPrinterService.sendRAWData(returnText, null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (receiver != null) {
            unregisterReceiver(receiver);
        }
    }

    /**      Ekran dışı yayın        */

    private class ScreenOnOffReceiver extends BroadcastReceiver {
        private int waitTime;
        private int intervalTime;

        private Handler handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message message) {

                LogUtil.e(Constant.TAG, "PrintTextActivity test screen off print...");
                onPrintClick();
                handler.sendEmptyMessageDelayed(0, intervalTime * 1000);
                return true;
            }
        });

        @Override
        public void onReceive(Context context, Intent intent) {
            if (Intent.ACTION_SCREEN_ON.equalsIgnoreCase(intent.getAction())) {
                handler.removeCallbacksAndMessages(null);
            } else if (Intent.ACTION_SCREEN_OFF.equalsIgnoreCase(intent.getAction())) {
                handler.sendEmptyMessageDelayed(0, waitTime * 1000);
            }
        }
    }














}