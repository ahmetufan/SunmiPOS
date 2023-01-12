package com.ahmet.sunmipost.print;

import androidx.annotation.NonNull;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
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
import com.ahmet.sunmipost.utils.BitmapUtils;
import com.ahmet.sunmipost.utils.Constant;
import com.ahmet.sunmipost.utils.ESCUtil;
import com.ahmet.sunmipost.utils.LogUtil;
import com.ahmet.sunmipost.utils.SystemDateTime;
import com.sunmi.peripheral.printer.InnerResultCallback;
import com.sunmi.peripheral.printer.SunmiPrinterService;

import static com.ahmet.sunmipost.utils.ESCUtil.ESC;

public class PrintTextActivity extends BaseAppCompatActivity {

    private SunmiPrinterService sunmiPrinterService;
    private EditText etText, etTextSize;
    private EditText edtRepeatCount;
    private EditText edtWaitTime;
    private EditText edtIntervalTime;
    private Button btnPrint;
    private ScreenOnOffReceiver receiver;
    Bitmap bitmap1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print_text);

        initToolbarBringBack(R.string.print_text);

        sunmiPrinterService = MyApplication.sunmiPrinterService;

        initView();


    }

    private Bitmap scaleImage(Bitmap bitmap1) {
        int width = bitmap1.getWidth();
        int height = bitmap1.getHeight();
        // 设置想要的大小
        int newWidth = (width / 8 + 1) * 8;
        // 计算缩放比例
        float scaleWidth = ((float) newWidth) / width;
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, 1);
        // 得到新的图片
        return Bitmap.createBitmap(bitmap1, 0, 0, width, height, matrix, true);
    }

    private void initView() {
        btnPrint = findViewById(R.id.btn_print);
        btnPrint.setOnClickListener(this);
        etText = findViewById(R.id.et_text);
        etTextSize = findViewById(R.id.et_text_size);
        etTextSize.setText("22");
        etText.setVisibility(View.GONE);
        edtRepeatCount = findViewById(R.id.edt_repeat_count);
        edtWaitTime = findViewById(R.id.edt_wait_time);
        edtIntervalTime = findViewById(R.id.edt_interval_time);
//        etText.setText(R.string.text_mock);
        findViewById(R.id.btn_screen_off_print).setOnClickListener(this);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inTargetDensity = 160;
        options.inDensity = 160;

        if (bitmap1 == null) {
            bitmap1 = BitmapFactory.decodeResource(getResources(), R.drawable.vbtt, options);
            bitmap1 = scaleImage(bitmap1);
        }

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


            sunmiPrinterService.setAlignment(1, innerResultCallbcak);       //  Ortalama  ==>   0 ->left, 1 ->center, 2 ->right.

            sunmiPrinterService.printTextWithFont(SystemDateTime.getMMDD() + "/" + SystemDateTime.getYYYY() + " - " + hHmmss +
                    "\n", "", 20, innerResultCallbcak);
            sunmiPrinterService.printTextWithFont("SHELL-CEVİZLİBAĞ\n", "", 20, innerResultCallbcak);
            sunmiPrinterService.printTextWithFont("MERKEZEFENDİ MAH. E-5 KARAYOLU ÜZERİ LONDRA ASFALTI NO:5 ZEYTİNBURNU 34010\n", "", 20, innerResultCallbcak);
            sunmiPrinterService.printTextWithFont("İSTANBUL\n\n", "", 20, innerResultCallbcak);

            sunmiPrinterService.printTextWithFont("SHELL PETROL A.Ş.\n", "", 20, innerResultCallbcak);
            sunmiPrinterService.printColumnsString(new String[]{"İ:986700", "T:03159456 \n"}, new int[]{1, 1}, new int[]{1, 1}, innerResultCallbcak);


            sunmiPrinterService.sendRAWData(new byte[]{0x1b, 0x45, 0x1}, innerResultCallbcak); //  Font Bold  - UnBold -> 0x1b, 0x45, 0x0       FONT    BOLD
            sunmiPrinterService.printTextWithFont("K. N. : ************ 8541 \n", "", 23, innerResultCallbcak);
            sunmiPrinterService.printTextWithFont("VBT YAZILIM \n", "", 24, innerResultCallbcak);
            sunmiPrinterService.printColumnsString(new String[]{"100,00 TL "}, new int[]{1}, new int[]{2}, innerResultCallbcak);
            sunmiPrinterService.sendRAWData(new byte[]{0x1b, 0x45, 0x0}, innerResultCallbcak); //  Font Bold  - UnBold -> 0x1b, 0x45, 0x0       FONT    UN BOLD


            sunmiPrinterService.printTextWithFont("================================\n", "", 24, innerResultCallbcak);
            sunmiPrinterService.printTextWithFont("VBT YAZILIM POS PRİNTER DENEME . KPAY ÖDEME SİSTEMLERİ \n", "", 20, innerResultCallbcak);
            sunmiPrinterService.printTextWithFont("================================\n", "", 24, innerResultCallbcak);
            sunmiPrinterService.printBitmap(bitmap1, innerResultCallbcak);


            sunmiPrinterService.exitPrinterBufferWithCallback(true, innerResultCallbcak);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static byte[] boldOn() {
        byte[] result = new byte[3];
        result[0] = ESC;
        result[1] = 69;
        result[2] = 0xF;
        return result;
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

    private InnerResultCallback innerResultCallbcak = new InnerResultCallback() {

        @Override
        public void onRunResult(boolean isSuccess) throws RemoteException {
            LogUtil.e("lxy", "isSuccess:" + isSuccess);
            if (is) {
                try {

                    sunmiPrinterService.lineWrap(5, innerResultCallbcak);
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

    /**
     * Ekran dışı yayın
     */

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