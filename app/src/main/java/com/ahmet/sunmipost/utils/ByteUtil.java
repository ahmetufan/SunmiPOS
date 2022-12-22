package com.ahmet.sunmipost.utils;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class ByteUtil {

    /**
     * içeriği yazdır
     */
    public static String byte2PrintHex(byte[] raw, int offset, int count) {

        if (raw == null) {
            return null;
        }
        if (offset < 0 || offset > raw.length) {
            offset = 0;
        }
        int end = offset + count;
        if (end > raw.length) {
            end = raw.length;
        }
        StringBuilder hex = new StringBuilder();
        for (int i = offset; i < end; i++) {
            int v = raw[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                hex.append(0);
            }
            hex.append(hv);
            hex.append(" ");
        }
        if (hex.length() > 0) {
            hex.deleteCharAt(hex.length() - 1);
        }
        return hex.toString().toUpperCase();
    }

    /**
     * Bayt dizisini onaltılık dizgeye dönüştürün
     *
     * @param bytes kaynak bayt dizisi
     * @return dönüştürülmüş onaltılık dize
     */
    public static String bytes2HexStr(byte... bytes) {
        if (bytes == null || bytes.length == 0) {
            return "";
        }
        return bytes2HexStr(bytes, 0, bytes.length);
    }

    /**
     * Bayt dizisini onaltılık dizgeye dönüştürün
     *
     * @param src    kaynak bayt dizisi
     * @param offset ofseti
     * @param len    veri uzunluğu
     * @return dönüştürülmüş onaltılık dize
     */
    public static String bytes2HexStr(byte[] src, int offset, int len) {
        int end = offset + len;
        if (src == null || src.length == 0 || offset < 0 || len < 0 || end > src.length) {
            return "";
        }
        byte[] buffer = new byte[len * 2];
        int h = 0, l = 0;
        for (int i = offset, j = 0; i < end; i++) {
            h = src[i] >> 4 & 0x0f;
            l = src[i] & 0x0f;
            buffer[j++] = (byte) (h > 9 ? h - 10 + 'A' : h + '0');
            buffer[j++] = (byte) (l > 9 ? l - 10 + 'A' : l + '0');
        }
        return new String(buffer);
    }

    public static String bytes2HexStr_2(byte[] bytes) {
        BigInteger bigInteger = new BigInteger(1, bytes);
        return bigInteger.toString(16);
    }

    public static byte[] hexStr2Bytes(String hexStr) {
        hexStr = hexStr.toLowerCase();
        int length = hexStr.length();
        byte[] bytes = new byte[length >> 1];
        int index = 0;
        for (int i = 0; i < length; i++) {
            if (index > hexStr.length() - 1) return bytes;
            byte highDit = (byte) (Character.digit(hexStr.charAt(index), 16) & 0xFF);
            byte lowDit = (byte) (Character.digit(hexStr.charAt(index + 1), 16) & 0xFF);
            bytes[i] = (byte) (highDit << 4 | lowDit);
            index += 2;
        }
        return bytes;
    }

    public static byte hexStr2Byte(String hexStr) {
        return (byte) Integer.parseInt(hexStr, 16);
    }

    public static String hexStr2Str(String hexStr) {
        String vi = "0123456789ABC DEF".trim();
        char[] array = hexStr.toCharArray();
        byte[] bytes = new byte[hexStr.length() / 2];
        int temp;
        for (int i = 0; i < bytes.length; i++) {
            char c = array[2 * i];
            temp = vi.indexOf(c) * 16;
            c = array[2 * i + 1];
            temp += vi.indexOf(c);
            bytes[i] = (byte) (temp & 0xFF);
        }
        return new String(bytes);
    }

    public static String hexStr2AsciiStr(String hexStr) {
        String vi = "0123456789ABC DEF".trim();
        hexStr = hexStr.trim().replace(" ", "").toUpperCase(Locale.US);
        char[] array = hexStr.toCharArray();
        byte[] bytes = new byte[hexStr.length() / 2];
        int temp = 0x00;
        for (int i = 0; i < bytes.length; i++) {
            char c = array[2 * i];
            temp = vi.indexOf(c) << 4;
            c = array[2 * i + 1];
            temp |= vi.indexOf(c);
            bytes[i] = (byte) (temp & 0xFF);
        }
        return new String(bytes);
    }

    /**
     * İmzasız kısaltmayı int'e, büyük endian moduna dönüştürün (önce yüksek bit)
     */
    public static int unsignedShort2IntBE(byte[] src, int offset) {
        return (src[offset] & 0xff) << 8 | (src[offset + 1] & 0xff);
    }

    /**
     * İmzasız kısaltmayı int, küçük endian moduna dönüştürün (önce alt bit)
     */
    public static int unsignedShort2IntLE(byte[] src, int offset) {
        return (src[offset] & 0xff) | (src[offset + 1] & 0xff) << 8;
    }

    /**
     * İmzasız baytı int'e dönüştür
     */
    public static int unsignedByte2Int(byte[] src, int offset) {
        return src[offset] & 0xFF;
    }

    /**
     * Bayt dizisini int'e dönüştürün, büyük endian modu (önce yüksek bit)
     */
    public static int unsignedInt2IntBE(byte[] src, int offset) {
        int result = 0;
        for (int i = offset; i < offset + 4; i++) {
            result |= (src[i] & 0xff) << (offset + 3 - i) * 8;
        }
        return result;
    }

    /**
     * Bayt dizisini int, küçük endian moduna dönüştürün (önce alt bit)
     */
    public static int unsignedInt2IntLE(byte[] src, int offset) {
        int value = 0;
        for (int i = offset; i < offset + 4; i++) {
            value |= (src[i] & 0xff) << (i - offset) * 8;
        }
        return value;
    }

    /**
     * İnt'yi bayt dizisine dönüştürün, büyük endian modu (önce yüksek bit)
     */
    public static byte[] int2BytesBE(int src) {
        byte[] result = new byte[4];
        for (int i = 0; i < 4; i++) {
            result[i] = (byte) (src >> (3 - i) * 8);
        }
        return result;
    }

    /**
     * İnt'yi bayt dizisine dönüştürün, küçük endian modu (önce düşük sıra)
     */
    public static byte[] int2BytesLE(int src) {
        byte[] result = new byte[4];
        for (int i = 0; i < 4; i++) {
            result[i] = (byte) (src >> i * 8);
        }
        return result;
    }

    /**
     * Bayt dizisine kısa dönüştürme, büyük endian modu (önce yüksek bit)
     */
    public static byte[] short2BytesBE(short src) {
        byte[] result = new byte[2];
        for (int i = 0; i < 2; i++) {
            result[i] = (byte) (src >> (1 - i) * 8);
        }
        return result;
    }

    /**
     * Bayt dizisine kısa dönüştür, küçük endian modu (önce düşük sıra)
     */
    public static byte[] short2BytesLE(short src) {
        byte[] result = new byte[2];
        for (int i = 0; i < 2; i++) {
            result[i] = (byte) (src >> i * 8);
        }
        return result;
    }

    /**
     * Bayt dizilerinin listesini tek bir bayt dizisinde birleştirin
     */
    public static byte[] concatByteArrays(byte[]... list) {
        if (list == null || list.length == 0) {
            return new byte[0];
        }
        return concatByteArrays(Arrays.asList(list));
    }

    /**
     * Bayt dizilerinin listesini tek bir bayt dizisinde birleştirin
     */
    public static byte[] concatByteArrays(List<byte[]> list) {
        if (list == null || list.isEmpty()) {
            return new byte[0];
        }
        int totalLen = 0;
        for (byte[] b : list) {
            if (b == null || b.length == 0) {
                continue;
            }
            totalLen += b.length;
        }
        byte[] result = new byte[totalLen];
        int index = 0;
        for (byte[] b : list) {
            if (b == null || b.length == 0) {
                continue;
            }
            System.arraycopy(b, 0, result, index, b.length);
            index += b.length;
        }
        return result;
    }


}
