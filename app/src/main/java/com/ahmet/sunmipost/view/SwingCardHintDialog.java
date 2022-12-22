package com.ahmet.sunmipost.view;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ahmet.sunmipost.R;

public class SwingCardHintDialog extends Dialog {


    public SwingCardHintDialog(@NonNull Context context) {
        this(context, R.style.DefaultDialogStyle);
    }

    public SwingCardHintDialog(@NonNull Context context, int theme) {
        super(context, theme);
        init();
    }

    private void init() {
        setContentView(R.layout.dialog_nfc_hint);
        if (getWindow() != null) {
            // merkezinde
            getWindow().getAttributes().gravity = Gravity.CENTER;
        }
        // İptal etmemek için boş tıklayın
        setCanceledOnTouchOutside(false);
        // İptal etmeden geri düğmesine tıklayın
        setCancelable(true);
    }
}
