package com.ahmet.sunmipost.print;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.ahmet.sunmipost.BaseAppCompatActivity;
import com.ahmet.sunmipost.R;
import com.ahmet.sunmipost.utils.BluetoothUtil;
import com.ahmet.sunmipost.utils.BytesUtil;
import com.ahmet.sunmipost.utils.SunmiPrintHelper;

public class PrintText2Activity extends BaseAppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print_text2);

        initToolbarBringBack(R.string.print_text_2);

        SunmiPrintHelper.getInstance().initSunmiPrinterService(this);

        findViewById(R.id.multi_one).setOnClickListener(this);


    }

    @Override
    public void onClick(View view) {
        super.onClick(view);

        byte[] rv = null;

        switch (view.getId()){

            case R.id.multi_one:
                rv = BytesUtil.getKoubeiData();
                break;

            default:
                break;
        }

        if(BluetoothUtil.isBlueToothPrinter){
            BluetoothUtil.sendData(rv);
        }else{
            SunmiPrintHelper.getInstance().sendRawData(rv);
        }
    }
}