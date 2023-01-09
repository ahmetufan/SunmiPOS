package com.ahmet.sunmipost.readCard;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.ahmet.sunmipost.BaseAppCompatActivity;
import com.ahmet.sunmipost.R;
import com.ahmet.sunmipost.readCard.MagEncActivity;

public class CardActivity extends BaseAppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card);

        initToolbarBringBack(R.string.read_card);

        initView();
    }

    private void initView() {

        View view = findViewById(R.id.magneticTestConstraint);
        TextView leftText = view.findViewById(R.id.magneticCardTestTextview);
        view.setOnClickListener(this);
        leftText.setText(R.string.card_test_mag_enc);

        view = findViewById(R.id.magneticConstraint);
        leftText = view.findViewById(R.id.magneticTextview);
        view.setOnClickListener(this);
        leftText.setText(R.string.card_test_mag);

        view = findViewById(R.id.iCCardConstraint);
        leftText = view.findViewById(R.id.iCCardTextview);
        view.setOnClickListener(this);
        leftText.setText(R.string.card_test_ic);
    }

    @Override
    public void onClick(View view) {
        final int id = view.getId();

        switch (id) {

            case R.id.magneticTestConstraint:
                openActivity(MagEncActivity.class);
                break;

            case R.id.magneticConstraint:
                openActivity(MagneticActivity.class);
                break;

            case R.id.iCCardConstraint:
                openActivity(ICActivity.class);
                break;
        }
    }


}