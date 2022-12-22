package com.ahmet.sunmipost.readCard;

import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.widget.TextView;

import com.ahmet.sunmipost.BaseAppCompatActivity;
import com.ahmet.sunmipost.MyApplication;
import com.ahmet.sunmipost.R;
import com.ahmet.sunmipost.readCard.wrapper.CheckCardCallbackV2Wrapper;
import com.ahmet.sunmipost.utils.Constant;
import com.ahmet.sunmipost.utils.LogUtil;
import com.ahmet.sunmipost.utils.Utility;
import com.google.android.material.button.MaterialButton;
import com.sunmi.pay.hardware.aidl.AidlConstants;
import com.sunmi.pay.hardware.aidlv2.AidlConstantsV2;
import com.sunmi.pay.hardware.aidlv2.readcard.CheckCardCallbackV2;

import java.util.Locale;

public class MagneticActivity extends BaseAppCompatActivity {

    private static final String TAG = "MagActivity";

    private MaterialButton mBtnTotal;
    private MaterialButton mBtnSuccess;
    private MaterialButton mBtnFail;
    private TextView mTvTrack1;
    private TextView mTvTrack2;
    private TextView mTvTrack3;
    private int mTotalTime;
    private int mSuccessTime;
    private int mFailTime;

    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_magnetic);

        initToolbarBringBack(R.string.card_test_mag);

        initView();

        checkCard();

    }

    public void initView() {

        mBtnTotal = findViewById(R.id.mb_total);
        mBtnSuccess = findViewById(R.id.mb_success);
        mBtnFail = findViewById(R.id.mb_fail);
        mTvTrack1 = findViewById(R.id.tv_track1);
        mTvTrack2 = findViewById(R.id.tv_track2);
        mTvTrack3 = findViewById(R.id.tv_track3);
    }

    /** start check card */
    private void checkCard() {

        try {
            MyApplication.mReadCardOptV2.checkCard(AidlConstants.CardType.MAGNETIC.getValue(),mCheckCardCallback,60);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private CheckCardCallbackV2 mCheckCardCallback = new CheckCardCallbackV2Wrapper() {
        /**
         * Find magnetic card
         *
         * @param info dönüş verileri， aşağıdaki anahtarları içerir:
         *             <br/>cardType: card type (int)
         *             <br/>TRACK1: track 1 data (String)
         *             <br/>TRACK2: track 2 data (String)
         *             <br/>TRACK3: track 3 data (String)
         *             <br/>track1ErrorCode: track 1 error code (int)
         *             <br/>track2ErrorCode: track 2 error code (int)
         *             <br/>track3ErrorCode: track 3 error code (int)
         *             <br/> parça hata kodu aşağıdaki değerlerden biridir:
         *             <ul>
         *             <li>  0 - No error</li>
         *             <li> -1 - İzlemede veri yok /li>
         *             <li> -2 - Parite kontrol hatası izle </li>
         *             <li> -3 - LRC kontrol hatası izle </li>
         *             </ul>
         */
        @Override
        public void findMagCard(Bundle info) throws RemoteException {
            LogUtil.e(Constant.TAG, "findMagCard,bundle:" + Utility.bundle2String(info));
            handleResult(info);
        }

        @Override
        public void findICCard(String atr) throws RemoteException {
            LogUtil.e(Constant.TAG, "findICCard,atr:" + atr);
        }

        @Override
        public void findRFCard(String uuid) throws RemoteException {
            LogUtil.e(Constant.TAG, "findRFCard,uuid:" + uuid);
        }

        @Override
        public void onError(int code, String message) throws RemoteException {
            String error = "onError:" + message + " -- " + code;
            LogUtil.e(Constant.TAG, error);
            showToast(error);
            handleResult(null);
        }
    };

    private void handleResult(Bundle bundle) {
        if (isFinishing()){
            return;
        }
        handler.post(()  -> {
            if (bundle == null) {
                showResult(false,"","","");
                return;
            }
            String track1 = Utility.null2String(bundle.getString("TRACK1"));
            String track2 = Utility.null2String(bundle.getString("TRACK2"));
            String track3 = Utility.null2String(bundle.getString("TRACK3"));
            //  Parça hata kodu: 0-hata yok, -1-yolda veri yok, -2-parite kontrol hatası, -3-LRC kontrol hatası
            int code1 = bundle.getInt("track1ErrorCode");
            int code2 = bundle.getInt("track2ErrorCode");
            int code3 = bundle.getInt("track1ErrorCode");
            LogUtil.e(TAG, String.format(Locale.getDefault(),
                    "track1ErrorCode:%d,track1:%s\ntrack2ErrorCode:%d,track2:%s\ntrack3ErrorCode:%d,track3:%s",
                    code1, track1, code2, track2, code3, track3));
            if ((code1 != 0 && code1 != -1)  || (code2 != 0 && code2 != -1) && (code3 != 0 && code3 != -1)) {
                showResult(false,track1,track2,track3);
            } else  {
                showResult(false,track1,track2,track3);
            }
            // Kartı kontrol etmeye devam edin
            if (!isFinishing()) {
                handler.postDelayed(this::checkCard,500);
            }
        });
    }

    private void showResult (boolean success, String track1, String track2, String track3) {
        mTotalTime += 1;
        if (success) {
            mSuccessTime += 1;
        } else  {
            mFailTime += 1;
        }
        mTvTrack1.setText(track1);
        mTvTrack2.setText(track2);
        mTvTrack3.setText(track3);

        String temp = getString(R.string.card_total) + " " +mTotalTime;
        mBtnTotal.setText(temp);
        temp = getString(R.string.card_success) + " " + mSuccessTime;
        mBtnSuccess.setText(temp);
        temp = getString(R.string.card_fail) + " " + mFailTime;
        mBtnFail.setText(temp);
    }

    @Override
    protected void onDestroy() {
        cancelCheckCard();
        super.onDestroy();
    }

    private void cancelCheckCard() {
        try {
            MyApplication.mReadCardOptV2.cancelCheckCard();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}