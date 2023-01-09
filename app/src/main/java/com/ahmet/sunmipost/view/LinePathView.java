package com.ahmet.sunmipost.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class LinePathView extends View {

    /**
     * İmza için Yol Görünümünü çizin
     */
    private float mX;
    /**
     * Strok Y koordinatı başlangıç ​​noktası
     */
    private float mY;
    /**
     * el yazısı fırçası
     */
    private final Paint mGesturePaint = new Paint();
    private final Paint mTextPaint = new Paint();
    /**
     * yol
     */
    private final Path mPath = new Path();
    /**
     * arka plan tuval
     */
    private Canvas cacheCanvas;
    /**
     * Arka plan Bitmap önbelleği
     */
    private Bitmap cacheBitmap;
    /**
     * imzalı mı
     */
    private boolean isTouched = false;


    /**
     * fırça genişliği piksel;
     */
    private int mPaintWidth = 10;

    /**
     * ön plan rengi
     */
    private int mPenColor = Color.BLACK;
    /**
     * arka plan rengi
     */
    private int mBackColor = Color.TRANSPARENT;

    public LinePathView(Context context) {
        super(context);
        init();
    }

    public LinePathView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LinePathView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init() {
        mGesturePaint.setAntiAlias(true);
        mGesturePaint.setStyle(Paint.Style.STROKE);
        mGesturePaint.setStrokeWidth(mPaintWidth);
        mGesturePaint.setColor(mPenColor);
        setPaintWidth(10);//Fırça genişliğini ayarlayın, varsayılan genişlik 10 pikseldir.

        mTextPaint.setTextSize(60);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setStrokeWidth(2);
        mTextPaint.setColor(mPenColor);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(cacheBitmap, 0, 0, mGesturePaint);
        // Tuval boyunca birden çok noktadan oluşan bir grafik çizin
        canvas.drawPath(mPath, mGesturePaint);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        cacheBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        cacheCanvas = new Canvas(cacheBitmap);
        if (!TextUtils.isEmpty(transFeatureCode)) {
            float fontWidth = mTextPaint.measureText(transFeatureCode);
            cacheCanvas.drawText(transFeatureCode, (cacheCanvas.getWidth() - fontWidth) / 2, cacheCanvas.getHeight() / 2, mTextPaint);
            transFeatureCode = null;
        }
        isTouched = false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touchDown(event);
                break;
            case MotionEvent.ACTION_MOVE:
                isTouched = true;
                touchMove(event);
                break;
            case MotionEvent.ACTION_UP:
                cacheCanvas.drawPath(mPath, mGesturePaint);
                mPath.reset();
                break;
        }
        // çizimi güncelle
        invalidate();
        return true;
    }


    // Parmak ekrana dokunduğunda çağrılır
    private void touchDown(MotionEvent event) {

        // Çizilen rotayı sıfırlayın, yani daha önce çizilen yolu gizleyin
        mPath.reset();
        float x = event.getX();
        float y = event.getY();

        mX = x;
        mY = y;
        // mPath çizimi için çizim başlangıç ​​noktası
        mPath.moveTo(x, y);
    }

    // Parmak ekranda kaydırıldığında çağrılır
    private void touchMove(MotionEvent event) {
        final float x = event.getX();
        final float y = event.getY();

        final float previousX = mX;
        final float previousY = mY;

        final float dx = Math.abs(x - previousX);
        final float dy = Math.abs(y - previousY);

        // İki nokta arasındaki mesafe 3'ten büyük veya eşit olduğunda, bir Bezier çizim eğrisi oluşturulur.
        if (dx >= 3 || dy >= 3) {
            // Bezier eğrisinin çalışma noktasını başlangıç ​​ve bitiş noktalarının yarısına ayarlayın
            float cX = (x + previousX) / 2;
            float cY = (y + previousY) / 2;

            // Düzgün bir eğri elde etmek için ikinci dereceden Bezier; öncekiX, öncekiY çalışma noktalarıdır, cX, cY bitiş noktalarıdır
            mPath.quadTo(previousX, previousY, cX, cY);

            // İkinci uygulamada, birinci son aramanın koordinat değeri, ikinci aramanın başlangıç ​​koordinat değeri olarak kullanılacaktır.
            mX = x;
            mY = y;
        }
    }

    /**
     * temiz çalışma yüzeyi
     */
    public void clear() {
        if (cacheCanvas != null) {
            isTouched = false;
            mGesturePaint.setColor(mPenColor);
            cacheBitmap.eraseColor(Color.TRANSPARENT);
            cacheCanvas.drawColor(mBackColor);  //背景色
            invalidate();
        }
    }

    private String transFeatureCode;

    /**
     * Temizle düğmesine tıklandığında, işlem imzasının yeniden çizilmesi gerekiyor
     */
    public void updateTransFeatureCode(String transFeatureCode) {
        this.transFeatureCode = transFeatureCode;
        if (cacheCanvas != null) {
            float fontWidth = mTextPaint.measureText(transFeatureCode);
            cacheCanvas.drawText(transFeatureCode, (cacheCanvas.getWidth() - fontWidth) / 2, cacheCanvas.getHeight() / 2, mTextPaint);
            isTouched = false;
            invalidate();
        }
    }

    public Bitmap getCacheBitmap() {
        return cacheBitmap;
    }

    /**
     * Fırça genişliğini ayarlayın, varsayılan genişlik 10 pikseldir.
     *
     * @param mPaintWidth
     */
    public void setPaintWidth(int mPaintWidth) {
        mPaintWidth = mPaintWidth > 0 ? mPaintWidth : 10;
        this.mPaintWidth = mPaintWidth;
        mGesturePaint.setStrokeWidth(mPaintWidth);
    }

    /**
     * imza var mı
     *
     * @return
     */
    public boolean getTouched() {
        return isTouched;
    }

    /**
     * geçerli bir imza mı
     *
     * @return
     */
    public boolean getIsValidSignature() {
        Bitmap bitmap = cacheBitmap;

        int blank = 10;//Kenar boşluğu için kaç piksel bırakılacağı
        int HEIGHT = bitmap.getHeight();
        int WIDTH = bitmap.getWidth();
        int top = 0, left = 0, right = 0, bottom = 0;
        int[] pixs = new int[WIDTH];
        boolean isStop;
        for (int y = 0; y < HEIGHT; y++) {
            bitmap.getPixels(pixs, 0, WIDTH, 0, y, WIDTH, 1);
            isStop = false;
            for (int pix : pixs) {
                if (pix != mBackColor) {
                    top = y;
                    isStop = true;
                    break;
                }
            }
            if (isStop) {
                break;
            }
        }
        for (int y = HEIGHT - 1; y >= 0; y--) {
            bitmap.getPixels(pixs, 0, WIDTH, 0, y, WIDTH, 1);
            isStop = false;
            for (int pix : pixs) {
                if (pix != mBackColor) {
                    bottom = y;
                    isStop = true;
                    break;
                }
            }
            if (isStop) {
                break;
            }
        }
        pixs = new int[HEIGHT];
        for (int x = 0; x < WIDTH; x++) {
            bitmap.getPixels(pixs, 0, 1, x, 0, 1, HEIGHT);
            isStop = false;
            for (int pix : pixs) {
                if (pix != mBackColor) {
                    left = x;
                    isStop = true;
                    break;
                }
            }
            if (isStop) {
                break;
            }
        }
        for (int x = WIDTH - 1; x > 0; x--) {
            bitmap.getPixels(pixs, 0, 1, x, 0, 1, HEIGHT);
            isStop = false;
            for (int pix : pixs) {
                if (pix != mBackColor) {
                    right = x;
                    isStop = true;
                    break;
                }
            }
            if (isStop) {
                break;
            }
        }
        if (blank < 0) {
            blank = 0;
        }
        left = left - blank > 0 ? left - blank : 0;
        top = top - blank > 0 ? top - blank : 0;
        right = right + blank > WIDTH - 1 ? WIDTH - 1 : right + blank;
        bottom = bottom + blank > HEIGHT - 1 ? HEIGHT - 1 : bottom + blank;

        int width = right - left;
        int height = bottom - top;

        if ((width >= 240) && (height >= 120)) {
            return true;
        } else if ((width >= 120) && (height >= 240)) {
            return true;
        } else {
            return false;
        }
    }
}
