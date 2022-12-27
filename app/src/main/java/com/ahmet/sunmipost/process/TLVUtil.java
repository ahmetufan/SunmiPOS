package com.ahmet.sunmipost.process;

import android.text.TextUtils;

import com.ahmet.sunmipost.tuple.Tuple;
import com.ahmet.sunmipost.tuple.TupleUtil;
import com.ahmet.sunmipost.utils.ByteUtil;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class TLVUtil {


    private TLVUtil() {
        throw new AssertionError("Create instance of TLVUtil is prohibited");
    }

    /**
     * Onaltılık bir dizeyi TLV nesneleri listesine dönüştürün
     *
     * @param hexStr Hex biçimindeki TLV verileri
     * @return TLV veri listesi
     */
    public static List<TLV> buildTLVList(String hexStr) {
        List<TLV> list = new ArrayList<>();
        int position = 0;

        while (position != hexStr.length()) {
            Tuple<String, Integer> tupleTag = getTag(hexStr, position);
            if (TextUtils.isEmpty(tupleTag.a) || "00".equals(tupleTag.a)) {
                break;
            }
            Tuple<Integer, Integer> tupleLen = getLength(hexStr, tupleTag.b);
            Tuple<String, Integer> tupleValue = getValue(hexStr, tupleLen.b, tupleLen.a);
//            Log.e("TLV-buildTLVList", tupleTag.a + ":" + tupleValue.a);
            list.add(new TLV(tupleTag.a, tupleLen.a, tupleValue.a));
            position = tupleValue.b;
        }
        return list;
    }

    /**
     * Onaltılık bir dizeyi TLV nesnesi MAP'ye dönüştürün<br/>
     * TLV belge bağlantı referansı：http://wenku.baidu.com/view/b31b26a13186bceb18e8bb53.html?re=view&qq-pf-to=pcqq.c2c
     *
     * @param hexStr Onaltılık biçim TLV verileri
     * @return TLV veri Haritası
     */
    public static Map<String, TLV> buildTLVMap(String hexStr) {
        Map<String, TLV> map = new LinkedHashMap<>();
        if (TextUtils.isEmpty(hexStr) || hexStr.length() % 2 != 0) return map;
        int position = 0;
        while (position < hexStr.length()) {
            Tuple<String, Integer> tupleTag = getTag(hexStr, position);
            if (TextUtils.isEmpty(tupleTag.a) || "00".equals(tupleTag.a)) {
                break;
            }
            Tuple<Integer, Integer> tupleLen = getLength(hexStr, tupleTag.b);
            Tuple<String, Integer> tupleValue = getValue(hexStr, tupleLen.b, tupleLen.a);
//            Log.e("TLV-buildTLVMap", tupleTag.a + ":" + tupleValue.a);
            map.put(tupleTag.a, new TLV(tupleTag.a, tupleLen.a, tupleValue.a));
            position = tupleValue.b;
        }
        return map;
    }

    /**
     * Bayt dizisini TLV nesneleri listesine dönüştürün
     *
     * @param hexByte Bayt veri biçimindeki TLV verileri
     * @return TLV数据List
     */
    public static List<TLV> buildTLVList(byte[] hexByte) {
        String hexString = ByteUtil.bytes2HexStr(hexByte);
        return buildTLVList(hexString);
    }

    /**
     * Bayt dizisini TLV nesnesi MAP'ye dönüştürün
     *
     * @param hexByte Bayt veri biçimindeki TLV verileri
     * @return TLV veri Haritası
     */
    public static Map<String, TLV> buildTLVMap(byte[] hexByte) {
        String hexString = ByteUtil.bytes2HexStr(hexByte);
        return buildTLVMap(hexString);
    }

    /**
     * Etiketi ve güncellenmiş imleç konumunu alın
     */
    private static Tuple<String, Integer> getTag(String hexString, int position) {
        String tag = "";
        try {
            String byte1 = hexString.substring(position, position + 2);
            String byte2 = hexString.substring(position + 2, position + 4);
            int b1 = Integer.parseInt(byte1, 16);
            int b2 = Integer.parseInt(byte2, 16);
            // b5~b1'in tümü 1 ise, bu etiketin altında bir alt bayt olduğu ve PBOC/EMV'deki etiketin en fazla iki bayt yer kapladığı anlamına gelir
            if ((b1 & 0x1F) == 0x1F) {
                // Etiketin ilk baytı hariç, etiketteki diğer baytların en yüksek biti: 1-takip edilecek daha çok bayt olduğunu, 0-son bayt olduğunu gösterir.
                if ((b2 & 0x80) == 0x80) {
                    tag = hexString.substring(position, position + 6);// 3Byte etiketi
                } else {
                    tag = hexString.substring(position, position + 4);// 2Byte etiketi
                }
            } else {
                tag = hexString.substring(position, position + 2);// 1Byte etiketi
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return TupleUtil.tuple(tag.toUpperCase(), position + tag.length());
    }

    /**
     * İmleç güncellemesinden sonra Uzunluk ve imleç konumunu alın
     */
    private static Tuple<Integer, Integer> getLength(final String hexStr, final int position) {
        int index = position;
        String hexLen = hexStr.substring(index, index + 2);
        index += 2;
        int byte1 = Integer.parseInt(hexLen, 16);
        // Uzunluk alanının kodlanması, maksimum dört bayt ile nispeten basittir.
        // İlk baytın en yüksek biti b8 0 ise, b7~b1 değeri, değer alanının uzunluğudur.
        // b8 1 ise, b7~b1'in değeri aşağıda kaç alt bayt olduğunu gösterir.Bir sonraki alt baytın değeri, değer alanının uzunluğudur.
        if ((byte1 & 0x80) != 0) {// 最左侧的bit位为1
            int subLen = byte1 & 0x7F;
            hexLen = hexStr.substring(index, index + subLen * 2);
            index += subLen * 2;
        }
        return TupleUtil.tuple(Integer.parseInt(hexLen, 16), index);
    }

    /**
     * İmleç güncellemesinden sonra Değer ve imleç konumunu alın
     */
    private static Tuple<String, Integer> getValue(final String hexStr, final int position, final int len) {
        String value = "";
        try {
            value = hexStr.substring(position, position + len * 2);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return TupleUtil.tuple(value.toUpperCase(), position + len * 2);
    }

    /***
     * TLV'yi onaltılık diziye dönüştür
     */
    public static String revertToHexStr(TLV tlv) {
        StringBuilder sb = new StringBuilder();
        sb.append(tlv.getTag());
        sb.append(TLVValueLengthToHexString(tlv.getLength()));
        sb.append(tlv.getValue());
        return sb.toString();
    }

    /**
     * TLV verilerini bayt dizisine ters çevirin
     */
    public static byte[] revertToBytes(TLV tlv) {
        String hex = revertToHexStr(tlv);
        return ByteUtil.hexStr2Bytes(hex);
    }

    /**
     * TLV'deki veri uzunluğunu onaltılık bir dizeye dönüştürün
     */
    public static String TLVValueLengthToHexString(int length) {
        if (length < 0) {
            throw new RuntimeException("geçersiz uzunluk");
        }
        if (length <= 0x7f) {
            return String.format("%02x", length);
        } else if (length <= 0xff) {
            return "81" + String.format("%02x", length);
        } else if (length <= 0xffff) {
            return "82" + String.format("%04x", length);
        } else if (length <= 0xffffff) {
            return "83" + String.format("%06x", length);
        } else {
            throw new RuntimeException("TLV 4 bayta kadar uzunluk");
        }
    }

}
