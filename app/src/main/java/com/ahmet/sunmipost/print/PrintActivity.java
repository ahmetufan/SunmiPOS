package com.ahmet.sunmipost.print;


import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.ahmet.sunmipost.BaseAppCompatActivity;
import com.ahmet.sunmipost.R;

public class PrintActivity extends BaseAppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print);

        initToolbarBringBack(R.string.print);

        initView();

    }

    private void initView() {

        View view = findViewById(R.id.printTextConstraint);
        TextView leftText = view.findViewById(R.id.printTextTextview);
        view.setOnClickListener(this);
        leftText.setText(R.string.print_text);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.printTextConstraint:
                openActivity(PrintTextActivity.class);
                break;
        }
    }
}