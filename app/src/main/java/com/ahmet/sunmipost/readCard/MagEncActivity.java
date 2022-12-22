package com.ahmet.sunmipost.readCard;


import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.widget.TextView;

import com.ahmet.sunmipost.BaseAppCompatActivity;
import com.ahmet.sunmipost.MyApplication;
import com.ahmet.sunmipost.R;
import com.ahmet.sunmipost.readCard.wrapper.CheckCardCallbackV2Wrapper;
import com.ahmet.sunmipost.utils.ByteUtil;
import com.ahmet.sunmipost.utils.Constant;
import com.ahmet.sunmipost.utils.LogUtil;
import com.ahmet.sunmipost.utils.Utility;
import com.google.android.material.button.MaterialButton;
import com.sunmi.pay.hardware.aidl.AidlConstants;
import com.sunmi.pay.hardware.aidlv2.AidlConstantsV2;
import com.sunmi.pay.hardware.aidlv2.readcard.CheckCardCallbackV2;

import java.util.Locale;

public class MagEncActivity extends BaseAppCompatActivity {
    private static final String TAG = Constant.TAG;
    private static final int TDK_INDEX = 19;

    private MaterialButton mBtnTotal;
    private MaterialButton mBtnSuccess;
    private MaterialButton mBtnFail;
    private TextView mTvTrack1;
    private TextView mTvTrack2;
    private TextView mTvTrack3;
    private TextView mTvPAN;
    private TextView mTvCardholderName;
    private TextView mTvExpireDate;
    private TextView mTvServiceCode;
    private int mTotalTime;
    private int mSuccessTime;
    private int mFailTime;

    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mag_enc);

        initView();

        saveTDK();

        checkCard();

    }

    private void initView() {
        initToolbarBringBack(R.string.card_test_mag_enc);

        mBtnTotal = findViewById(R.id.mb_total);
        mBtnSuccess = findViewById(R.id.mb_success);
        mBtnFail = findViewById(R.id.mb_fail);
        mTvTrack1 = findViewById(R.id.tv_track1);
        mTvTrack2 = findViewById(R.id.tv_track2);
        mTvTrack3 = findViewById(R.id.tv_track3);
        mTvPAN = findViewById(R.id.tv_pan);
        mTvCardholderName = findViewById(R.id.tv_cardholder_name);
        mTvExpireDate = findViewById(R.id.tv_expire_date);
        mTvServiceCode = findViewById(R.id.tv_service_code);
    }

    /**
     * Test için bir TDK (veri izleme anahtarı) kaydedin
     */
    private void saveTDK() {
        try {
            byte[] tdk = ByteUtil.hexStr2Bytes("F2914D44BC2AF05533DD20C9A0B5B861");
            byte[] tdkcv = ByteUtil.hexStr2Bytes("36821ADF5EB5513F");
            int code = MyApplication.mSecurityOptV2.savePlaintextKey(AidlConstants.Security.KEY_TYPE_TDK
                    , tdk, tdkcv, AidlConstants.Security.KEY_ALG_TYPE_3DES, TDK_INDEX);
            LogUtil.e(TAG, "save TDK " + (code == 0 ? "success" : "failed"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * start check card
     */
    private void checkCard() {
        Bundle bundle = new Bundle();
        bundle.putInt("cardType", AidlConstantsV2.CardType.MAGNETIC.getValue());
        bundle.putInt("encKeyIndex", TDK_INDEX);
        bundle.putInt("encMode", AidlConstantsV2.Security.DATA_MODE_ECB);
        bundle.putByteArray("encIv", new byte[16]);
        bundle.putByte("encPaddingMode", (byte) 0);
        bundle.putInt("encMaskStart", 6);
        bundle.putInt("encMaskEnd", 4);
        bundle.putChar("encMaskWord", '*');
        try {
            MyApplication.mReadCardOptV2.checkCardEnc(bundle, mCheckCardCallback, 60);
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.d(TAG, e.getMessage());
        }
    }

    private CheckCardCallbackV2 mCheckCardCallback = new CheckCardCallbackV2Wrapper() {
        /**
         * Manyetik kart bul
         *
         * @param bundle paket dönüş verileri， aşağıdaki anahtarları içerir:
         *             <br/>cardType: card type (int)
         *             <br/>TRACK1: track 1 data (String)
         *             <br/>TRACK2: track 2 data (String)
         *             <br/>TRACK3: track 3 data (String)
         *             <br/>pan: PAN data (String)
         *             <br/>name: cardholder name (String)
         *             <br/>expire: card expire date (String)
         *             <br/>servicecode: card service code (String)
         *             <br/>track1ErrorCode: track 1 error code (int)
         *             <br/>track2ErrorCode: track 2 error code (int)
         *             <br/>track3ErrorCode: track 3 error code (int)
         *             <br/> parça hata kodu aşağıdaki değerlerden biridir:
         *             <ul>
         *             * <li> 0 - Hata yok</li>
         *           * <li>   -1 - Parçada veri yok</li>
         *           * <li>   -2 - Parça eşlik denetimi hatası</li>
         *           * <li>   -3 - LRC izleme hatası</li>
         *           * </ul>
         *           */

        @Override
        public void findMagCard(Bundle bundle) throws RemoteException {
            LogUtil.e(Constant.TAG, "findMagCard");
            String pan = bundle.getString("pan");
            String name = bundle.getString("name");
            String expire = bundle.getString("expire");
            String serviceCode = bundle.getString("servicecode");
            LogUtil.e(Constant.TAG, "pan = " + pan + ",name = " + name + ",expire = " + expire + ",serviceCode = " + serviceCode);
            handleResult(bundle);
        }

        @Override
        public void findICCardEx(Bundle bundle) throws RemoteException {
            String atr = bundle.getString("atr");
            LogUtil.e(Constant.TAG, "findICCard,atr:" + atr);
        }

        @Override
        public void findRFCardEx(Bundle bundle) throws RemoteException {
            String uuid = bundle.getString("uuid");
            LogUtil.e(Constant.TAG, "findRFCard,uuid:" + uuid);
        }

        /**
         * Kontrol kartı hatası
         *
         * @param bundle dönüş verileri， aşağıdaki anahtarları içerir:
         * <br/>cardType: kart tipi (int)
         * <br/>kod: hata kodu (Dize)
         * <br/>mesaj: hata mesajı (String)
         */

        @Override
        public void onErrorEx(Bundle bundle) throws RemoteException {
            int code = bundle.getInt("code");
            String msg = bundle.getString("message");
            String error = "onError:" + msg + " -- " + code;
            LogUtil.e(Constant.TAG, error);
            showToast(error);
            handleResult(null);
        }
    };

    private void handleResult(Bundle bundle) {
        if (isFinishing()) {
            return;
        }
        handler.post(() -> {
            if (bundle == null) {
                showResult(false, "", "", "", bundle);
                return;
            }
            String track1 = Utility.null2String(bundle.getString("TRACK1"));
            String track2 = Utility.null2String(bundle.getString("TRACK2"));
            String track3 = Utility.null2String(bundle.getString("TRACK3"));
            // Parça hata kodu: 0  -hata yok, -1  -yolda veri yok, -2  -parite kontrol hatası, -3  -LRC kontrol hatası
            int code1 = bundle.getInt("track1ErrorCode");
            int code2 = bundle.getInt("track2ErrorCode");
            int code3 = bundle.getInt("track3ErrorCode");
            LogUtil.e(TAG, String.format(Locale.getDefault(),
                    "track1ErrorCode:%d,track1:%s\ntrack2ErrorCode:%d,track2:%s\ntrack3ErrorCode:%d,track3:%s",
                    code1, track1, code2, track2, code3, track3));
            if ((code1 != 0 && code1 != -1) || (code2 != 0 && code2 != -1) || (code3 != 0 && code3 != -1)) {
                showResult(false, track1, track2, track3, bundle);
            } else {
                showResult(true, track1, track2, track3, bundle);
            }
            // Kartı kontrol etmeye devam edin
            if (!isFinishing()) {
                handler.postDelayed(this::checkCard, 500);
            }
        });
    }

    private void showResult(boolean success, String track1, String track2, String track3, Bundle bundle) {
        mTotalTime += 1;
        if (success) {
            mSuccessTime += 1;
        } else {
            mFailTime += 1;
        }
        mTvTrack1.setText(track1);
        mTvTrack2.setText(track2);
        mTvTrack3.setText(track3);
        if (bundle != null) {
            mTvPAN.setText(bundle.getString("pan"));
            mTvCardholderName.setText(bundle.getString("name"));
            mTvExpireDate.setText(bundle.getString("expire"));
            mTvServiceCode.setText(bundle.getString("servicecode"));
        }
        String temp = getString(R.string.card_total) + " " + mTotalTime;
        mBtnTotal.setText(temp);
        temp = getString(R.string.card_success) + " " + mSuccessTime;
        mBtnSuccess.setText(temp);
        temp = getString(R.string.card_fail) + " " + mFailTime;
        mBtnFail.setText(temp);
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacksAndMessages(null);
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