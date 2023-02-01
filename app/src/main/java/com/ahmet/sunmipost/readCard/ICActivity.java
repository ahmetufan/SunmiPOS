package com.ahmet.sunmipost.readCard;


import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.view.View;
import android.widget.TextView;

import com.ahmet.sunmipost.BaseAppCompatActivity;
import com.ahmet.sunmipost.MyApplication;
import com.ahmet.sunmipost.R;
import com.ahmet.sunmipost.readCard.wrapper.CheckCardCallbackV2Wrapper;
import com.ahmet.sunmipost.utils.Constant;
import com.ahmet.sunmipost.utils.LogUtil;
import com.ahmet.sunmipost.utils.SettingUtil;
import com.ahmet.sunmipost.utils.Utility;
import com.sunmi.pay.hardware.aidlv2.AidlConstantsV2;
import com.sunmi.pay.hardware.aidlv2.readcard.CheckCardCallbackV2;

public class ICActivity extends BaseAppCompatActivity {

    private TextView mTvDepictor;
    private TextView mTvUUID;
    private TextView mTvATR;
    private TextView mTvATS;
    private View mLayUUID;
    private View mLayATS;
    private View mLayATR;
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_icactivity);

        initToolbarBringBack(R.string.card_test_ic);

        initView();

        checkCard();
    }

    private void initView() {

        SettingUtil.setBuzzerEnable(false);     //  Enable/Disable kontrol kartı başarılı olduğunda sesli uyarı
        mTvDepictor = findViewById(R.id.tv_depictor);
        mTvUUID = findViewById(R.id.tv_uuid);
        mTvATS = findViewById(R.id.tv_ats);
        mTvATR = findViewById(R.id.tv_atr);
        mLayUUID = findViewById(R.id.lay_uuid);
        mLayATS = findViewById(R.id.lay_ats);
        mLayATR = findViewById(R.id.lay_atr);
    }

    private void checkCard() {
        try {
            int cardType = AidlConstantsV2.CardType.NFC.getValue() | AidlConstantsV2.CardType.IC.getValue();
            MyApplication.mReadCardOptV2.checkCard(cardType, mCheckCardCallback, 60);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private CheckCardCallbackV2 mCheckCardCallback = new CheckCardCallbackV2Wrapper() {

        @Override
        public void findMagCard(Bundle info) throws RemoteException {
            LogUtil.e(Constant.TAG, "findMagCard:" + Utility.bundle2String(info));
        }

        /**
         *  IC card  Bul
         *
         * @param info return veri, aşağıdaki anahtarları içerir:
         *             <br/>cardType: card type (int)
         *             <br/>atr: card's ATR (String)
         */
        @Override
        public void findICCardEx(Bundle info) throws RemoteException {
            LogUtil.e(Constant.TAG, "findICCard:" + Utility.bundle2String(info));
            handleResult(0, info);
        }

        /**
         * Find RF card
         *
         * @param info return veri, aşağıdaki anahtarları içerir:
         *             <br/>cardType: card type (int)
         *             <br/>uuid: card's UUID (String)
         *             <br/>ats: card's ATS (String)
         *             <br/>sak: card's SAK, if exist (int) (M1 S50:0x08, M1 S70:0x18, CPU:0x28)
         *             <br/>cardCategory: card's category,'A' or 'B', if exist (int)
         *             <br/>atqa: card's ATQA, if exist (byte[])
         */
        @Override
        public void findRFCardEx(Bundle info) throws RemoteException {
            LogUtil.e(Constant.TAG, "findRFCard:" + Utility.bundle2String(info));
            handleResult(1, info);
        }

        /**
         * Check card error
         *
         * @param info return veri, aşağıdaki anahtarları içerir:
         *             <br/>cardType: card type (int)
         *             <br/>code: the error code (String)
         *             <br/>message: the error message (String)
         */
        @Override
        public void onErrorEx(Bundle info) throws RemoteException {
            int code = info.getInt("code");
            String msg = info.getString("message");
            String error = "onError:" + msg + " -- " + code;
            LogUtil.e(Constant.TAG, error);
            showToast(error);
            handleResult(-1, info);
        }
    };

    /**
     *Kontrol kartı sonucunu göster
     *
     * @param type 0-find IC, 1-find NFC, <0-check card error
     * @param info check card ile döndürülen bilgiler
     */
    private void handleResult(int type, Bundle info) {
        if (isFinishing()) {
            return;
        }
        mHandler.post(() -> {
            if (type == 0) {// find IC
                mLayATR.setVisibility(View.VISIBLE);
                mLayUUID.setVisibility(View.GONE);
                mLayATS.setVisibility(View.GONE);
                mTvDepictor.setText(getString(R.string.card_check_ic_card));
                mTvATR.setText(info.getString("atr"));
            } else if (type == 1) {//find NFC
                mLayATR.setVisibility(View.GONE);
                mLayUUID.setVisibility(View.VISIBLE);
                mLayATS.setVisibility(View.VISIBLE);
                mTvDepictor.setText(getString(R.string.card_check_rf_card));
                mTvUUID.setText(info.getString("uuid"));
                mTvATS.setText(info.getString("ats"));
            } else {//on Error
                mTvDepictor.setText(getString(R.string.card_check_card_error));
                mLayATR.setVisibility(View.GONE);
                mLayUUID.setVisibility(View.GONE);
                mLayATS.setVisibility(View.GONE);
            }
            // Kartı kontrol etmeye devam edin
            if (!isFinishing()) {
                mHandler.postDelayed(this::checkCard, 500);
            }
        });
    }

    @Override
    protected void onDestroy() {
        mHandler.removeCallbacksAndMessages(null);
        cancelCheckCard();
        super.onDestroy();
    }

    private void cancelCheckCard() {
        try {
            MyApplication.mReadCardOptV2.cardOff(AidlConstantsV2.CardType.NFC.getValue());
            MyApplication.mReadCardOptV2.cancelCheckCard();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}