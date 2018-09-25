package com.huburt.algorithm;

import java.util.Arrays;

public class MyClass {

    public static void main(String... args) {
        int[] array = {2, 3, 15, 16, 74, 1};
        Arrays.sort(array);
        int rank = rank(3, array);
        System.out.println(rank);

    }

    public static int rank(int key, int[] a) {
        int lo = 0;
        int hi = a.length - 1;
        while (lo < hi) {
            int mid = lo + (hi - lo) / 2;
            if (key < a[mid]) {
                hi = mid - 1;
            } else if (key > a[mid]) {
                lo = mid + 1;
            } else {
                return mid;
            }
        }
        return -1;
    }
}
