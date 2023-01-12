package com.ahmet.sunmipost;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.ahmet.sunmipost.utils.BluetoothUtil;
import com.ahmet.sunmipost.utils.ESCUtil;
import com.ahmet.sunmipost.utils.SunmiPrintHelper;
import com.ahmet.sunmipost.view.LoadingDialog;
import com.ahmet.sunmipost.view.SwingCardHintDialog;
import com.sunmi.pay.hardware.aidlv2.AidlErrorCodeV2;

public abstract class BaseAppCompatActivity extends AppCompatActivity implements View.OnClickListener {

    private LoadingDialog loadingDialog;
    private SwingCardHintDialog swingCardHintDialog;

    private android.os.Handler dlgHandler = new Handler();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarColor();
        MyApplication.initLocaleLanguage();

        initPrinterStyle();
    }

    private void initPrinterStyle() {
        if(BluetoothUtil.isBlueToothPrinter){
            BluetoothUtil.sendData(ESCUtil.init_printer());
        }else{
            SunmiPrintHelper.getInstance().initPrinter();
        }
    }

    public void setStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    public void initToolbarBringBack() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        //  Etkili olması için setSupportActionBar'da Navigasyon Simgesi ayarlanmalıdır, aksi takdirde geri düğmesi görünür
        toolbar.setNavigationIcon(R.drawable.ic_back_white);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(
                v -> finish()
        );
    }

    public void initToolbarBringBack(int resId) {
        initToolbarBringBack(getString(resId));
    }

    public void initToolbarBringBack(String title) {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(title);
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        toolbar.setNavigationIcon(R.drawable.ic_back_white);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(
                v -> finish()
        );
    }

    public void showToast(int resId) {
        showToastOnUI(getString(resId));
    }

    public void showToast(String msg) {
        showToastOnUI(msg);
    }

    private void showToastOnUI(final String msg) {
        runOnUiThread(
                () -> Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        );
    }

    public void toastHint(int code) {
        if (code == 0) {
            showToast(getString(R.string.success));
        } else {
            String msg = AidlErrorCodeV2.valueOf(code).getMsg();
            String error = msg + ":" + code;
            showToast(error);
        }
    }

    protected void showLoadingDialog(int resId) {
        runOnUiThread(() -> _showLoadingDialog(getString(resId)));
    }

    protected void showLoadingDialog(final String msg) {
        runOnUiThread(() -> _showLoadingDialog(msg));
    }

    /**
     * Bu yöntem, UI iş parçacığında çağrılmalıdır
     */
    private void _showLoadingDialog(final String msg) {
        if (loadingDialog == null) {
            loadingDialog = new LoadingDialog(this, msg);
        } else {
            loadingDialog.setMessage(msg);
        }
        if (!loadingDialog.isShowing()) {
            loadingDialog.show();
        }
    }

    protected void dismissLoadingDialog() {
        runOnUiThread(
                () -> {
                    if (loadingDialog != null && loadingDialog.isShowing()) {
                        loadingDialog.dismiss();
                    }
                    dlgHandler.removeCallbacksAndMessages(null);
                }
        );
    }

    protected void showSwingCardHintDialog() {
        runOnUiThread(
                () -> {
                    if (swingCardHintDialog == null) {
                        swingCardHintDialog = new SwingCardHintDialog(this);
                        swingCardHintDialog.setOwnerActivity(this);
                    }
                    if (swingCardHintDialog.isShowing() || isDestroyed()) {
                        return;
                    }
                    swingCardHintDialog.show();
                }
        );
    }

    protected void dismissSwingCardHintDialog() {
        runOnUiThread(
                () -> {
                    if (swingCardHintDialog != null) {
                        swingCardHintDialog.dismiss();
                    }
                }
        );
    }

    protected void openActivity(Class<? extends Activity> clazz) {
        Intent intent = new Intent(this, clazz);
        openActivity(intent,false);
    }

    protected void openActivity(Class<? extends Activity> clazz, boolean finishSelf) {
        Intent intent = new Intent(this, clazz);
        openActivity(intent,finishSelf);
    }

    protected void openActivity(Intent intent, boolean finishSelf) {
        startActivity(intent);
        if (finishSelf) {
            finish();
        }
    }


    @Override
    public void onClick(View view) {

    }
}
