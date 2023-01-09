package com.ahmet.sunmipost.utils;

import android.graphics.Bitmap;
import android.graphics.Matrix;

public class BitmapUtils {

    public static Bitmap scale(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap bitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
        return bitmap;
    }


    /**
     * Bit eşlemdeki belirli bir renk değerini yeni bir renkle değiştirin
     *
     * @param oldBitmap
     * @param oldColor
     * @param newColor
     * @return
     */
    public static Bitmap replaceBitmapColor(Bitmap oldBitmap, int oldColor, int newColor) {
        //İlgili talimatlar şu adrese başvurabilir: http://xys289187120.blog.51cto.com/3361352/657590/
        Bitmap mBitmap = oldBitmap.copy(Bitmap.Config.ARGB_8888, true);
        //Bit eşlemin tüm piksellerini almak için döngü yapın
        int mBitmapWidth = mBitmap.getWidth();
        int mBitmapHeight = mBitmap.getHeight();
        for (int i = 0; i < mBitmapHeight; i++) {
            for (int j = 0; j < mBitmapWidth; j++) {
                //Bitmap görüntüsündeki her noktanın renk değerini alın
                //Değilse doldurulması gereken renk değeri
                //Renk tamamen şeffaf veya tamamen siyah ise, dönüş değerinin 0 olduğunu burada açıklayayım.
                //getPixel() -> şeffaf kanal olmadan  getPixel32()-> yalnızca saydam kısımlarda olduğundan, tam saydamlık  0x00000000
                //opak siyah0xFF000000 -- Saydam kısım hesaplanmazsa 0 olur.
                int color = mBitmap.getPixel(j, i);
                //Daha sonra değiştirilmek üzere renk değerini bir dizide saklayın
                if (color == oldColor) {
                    mBitmap.setPixel(j, i, newColor);  //Beyazı şeffafla değiştirin
                }

            }
        }
        return mBitmap;
    }
}
