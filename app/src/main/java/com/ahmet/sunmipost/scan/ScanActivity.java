package com.ahmet.sunmipost.scan;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.ahmet.sunmipost.BaseAppCompatActivity;
import com.ahmet.sunmipost.R;

import java.util.ArrayList;
import java.util.HashMap;

public class ScanActivity extends BaseAppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        initToolbarBringBack(R.string.scan);

        initView();
    }


    private void initView() {

        View view = findViewById(R.id.cameraScannerConstraint);
        TextView leftText = view.findViewById(R.id.cameraScannerTextview);
        view.setOnClickListener(this);
        leftText.setText(R.string.camera_scanner);
    }

    @Override
    public void onClick(View view) {

        final int id = view.getId();
        switch (id) {

            case R.id.cameraScannerConstraint:
                startScan();
                break;
        }
    }

    private void startScan() {

        Intent intent = new Intent();
        intent.setAction("com.summi.scan");
        intent.setPackage("com.sunmi.sunmiqrcodescanner");
        intent.putExtra("IS_SHOW_SETTING", false);  //   ayar düğmesinin görüntülenip görüntülenmeyeceği, varsayılan olarak doğru
        intent.putExtra("IDENTIFY_MORE_CODE", true);  //  ekranda birden fazla qr kodunu tanımlayın
        intent.putExtra("IS_AZTEC_ENABLE", true);     //  AZTEC kodunun okunmasına izin ver
        intent.putExtra("IS_PDF417_ENABLE", true);    //  PDF417 kodunun okunmasına izin ver
        intent.putExtra("IS_DATA_MATRIX_ENABLE", true);  //  DataMatrix kodunun okunmasına izin ver

        PackageManager packageManager = getPackageManager();
        if (intent.resolveActivity(packageManager) != null) {
            startActivityForResult(intent, 100);
        } else {
            showToast(R.string.error_scan);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 100 && data != null) {
            Bundle bundle = data.getExtras();
            ArrayList<HashMap<String, String>> result = (ArrayList<HashMap<String, String>>) bundle.getSerializable("data");

            if (result != null && result.size() > 0) {
                String type = result.get(0).get("TYPE");
                String value = result.get(0).get("VALUE");
                Intent intent = new Intent(this, ScanResultActivity.class);
                intent.putExtra("type", type);
                intent.putExtra("value", value);
                startActivity(intent);
            } else {
                showToast("Scan Failed");
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}