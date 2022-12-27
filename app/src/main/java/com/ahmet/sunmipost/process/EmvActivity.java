package com.ahmet.sunmipost.process;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.ahmet.sunmipost.BaseAppCompatActivity;
import com.ahmet.sunmipost.R;

public class EmvActivity extends BaseAppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emv);

        initToolbarBringBack(R.string.emv);

        initView();


    }

    private void initView() {

        View view = findViewById(R.id.nfcIcProcessConstraint);
        TextView leftText = view.findViewById(R.id.nfcIcProcessConstraintTextview);
        view.setOnClickListener(this);
        leftText.setText(R.string.emv_ic_process);

        view = findViewById(R.id.magneticProcessConstraint);
        leftText = view.findViewById(R.id.magneticProcessTextview);
        view.setOnClickListener(this);
        leftText.setText(R.string.emv_mag_process);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);

        final int id = view.getId();

        switch (id) {

            case R.id.nfcIcProcessConstraint:
                openActivity(ICProcessActivity.class);
                break;

            case R.id.magneticProcessConstraint:
                openActivity(MagProcessActivity.class);
                break;
        }
    }


}