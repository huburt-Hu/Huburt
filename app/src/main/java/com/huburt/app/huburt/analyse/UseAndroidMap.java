package com.huburt.app.huburt.analyse;

import android.support.v4.util.ArrayMap;
import android.util.SparseArray;

/**
 * Created by hubert on 2018/5/2.
 */
public class UseAndroidMap {
    ArrayMap<String, String> arrayMap = new ArrayMap<>();
    SparseArray<String> sparseArray = new SparseArray<>();

    void test() {
        for (int i = 0; i < arrayMap.size(); i++) {
            String key = arrayMap.keyAt(i);
            String value = arrayMap.valueAt(i);
        }


        for (int i = 0; i < sparseArray.size(); i++) {
            int key = sparseArray.keyAt(i);
            String value = sparseArray.valueAt(i);
        }
        sparseArray.put(1, "abc");
        sparseArray.get(1);
    }
}
