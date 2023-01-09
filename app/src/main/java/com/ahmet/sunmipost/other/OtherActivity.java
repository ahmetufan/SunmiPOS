package com.ahmet.sunmipost.other;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.ahmet.sunmipost.BaseAppCompatActivity;
import com.ahmet.sunmipost.R;

public class OtherActivity extends BaseAppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other);

        initToolbarBringBack(R.string.other);

        initView();
    }

    private void initView() {

        View view = findViewById(R.id.languageConstraint);
        TextView leftText = view.findViewById(R.id.languageTextview);
        view.setOnClickListener(this);
        leftText.setText(R.string.other_language);

        view = findViewById(R.id.e_signatureConstraint);
        leftText = view.findViewById(R.id.e_signatureTextview);
        view.setOnClickListener(this);
        leftText.setText(R.string.e_signature);

        view = findViewById(R.id.setDateTimeConstraint);
        leftText = view.findViewById(R.id.setDateTimeTextview);
        view.setOnClickListener(this);
        leftText.setText(R.string.set_date_time);

    }

    @Override
    public void onClick(View view) {

        final int id = view.getId();

        switch (id) {

            case R.id.languageConstraint:
                openActivity(LanguageActivity.class);
                break;

            case R.id.e_signatureConstraint:
                openActivity(ESignatureActivity.class);
                break;

            case R.id.setDateTimeConstraint:
                openActivity(SetTimeDatweActivity.class);
                break;
        }
    }
}