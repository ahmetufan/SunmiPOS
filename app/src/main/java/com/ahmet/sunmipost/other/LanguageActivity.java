package com.ahmet.sunmipost.other;

import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;

import com.ahmet.sunmipost.BaseAppCompatActivity;
import com.ahmet.sunmipost.CacheHelper;
import com.ahmet.sunmipost.MainActivity;
import com.ahmet.sunmipost.R;
import com.ahmet.sunmipost.utils.Constant;

import static com.ahmet.sunmipost.utils.Constant.LANGUAGE_EN_US;
import static com.ahmet.sunmipost.utils.Constant.LANGUAGE_TR_TR;

public class LanguageActivity extends BaseAppCompatActivity {

    private int mCurrentLanguage;

    private RadioButton mRbAuto;
    private RadioButton mRbZH_TR;
    private RadioButton mRbEN_US;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language);

        initToolbarBringBack(R.string.other_language);

        iniView();

    }

    private void iniView() {

        mRbAuto = findViewById(R.id.rb_auto);
        mRbZH_TR = findViewById(R.id.rb_tr);
        mRbEN_US = findViewById(R.id.rb_en_us);

        findViewById(R.id.item_auto).setOnClickListener(this);
        findViewById(R.id.item_tr).setOnClickListener(this);
        findViewById(R.id.item_en_us).setOnClickListener(this);

        reset();

        mCurrentLanguage = CacheHelper.getCurrentLanguage();
        switch (mCurrentLanguage) {

            case LANGUAGE_TR_TR:
                mRbZH_TR.setChecked(true);
                break;

            case LANGUAGE_EN_US:
                mRbEN_US.setChecked(true);
                break;

            default:
                mRbAuto.setChecked(true);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (CacheHelper.getCurrentLanguage() != mCurrentLanguage) {
            MainActivity.reStart(this);
        }
        super.onBackPressed();
    }

    @Override
    public void onClick(View view) {
        final int id = view.getId();

        switch (id) {
            case R.id.item_auto:
                reset();
                mRbAuto.setChecked(true);
                CacheHelper.saveCurrentLanguage(Constant.LANGUAGE_AUTO);
                break;

            case R.id.item_tr:
                reset();
                mRbZH_TR.setChecked(true);
                CacheHelper.saveCurrentLanguage(Constant.LANGUAGE_TR_TR);
                break;

            case R.id.item_en_us:
                reset();
                mRbEN_US.setChecked(true);
                CacheHelper.saveCurrentLanguage(Constant.LANGUAGE_EN_US);
                break;
        }
    }

    private void reset() {
        mRbAuto.setChecked(false);
        mRbZH_TR.setChecked(false);
        mRbEN_US.setChecked(false);
    }
}