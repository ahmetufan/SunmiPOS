package com.ahmet.sunmipost.basic;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.ahmet.sunmipost.BaseAppCompatActivity;
import com.ahmet.sunmipost.R;

public class BasicActivity extends BaseAppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic);

        initToolbarBringBack(R.string.basic);

        initView();
    }

    private void initView() {

        View view = findViewById(R.id.ledLambConstraint);
        TextView leftText = view.findViewById(R.id.ledLambTextview);
        view.setOnClickListener(this);
        leftText.setText(R.string.basic_led);
    }

    @Override
    public void onClick(View view) {

        final  int id = view.getId();

        switch (id) {

            case R.id.ledLambConstraint:
                openActivity(LedActivity.class);
                break;
        }
    }
}