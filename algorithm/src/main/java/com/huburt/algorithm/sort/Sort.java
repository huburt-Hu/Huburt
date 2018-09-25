package com.huburt.algorithm.sort;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by hubert on 2018/7/19.
 */
public class Sort {
    public static void main(String... args) {
        List<Long> list = new ArrayList<>();
        Random random = new Random();
        long N = 10000;
        for (int i = 0; i < N; i++) {
            list.add((long) (random.nextDouble() * N));
        }
        Long[] a = list.toArray(new Long[list.size() - 1]);

//        Integer[] a = {2, 6, 8, 1, 9, 3, 4, 7};
        long start = System.currentTimeMillis();
        sort(a);
        long end = System.currentTimeMillis();
        System.out.println("sort used time:" + (end - start));
        assert isSorted(a);
        show(a);
    }

    private static void sort(Comparable[] a) {
        bubbleSort(a);
//        selectionSort(a);
//        insertionSort(a);
//        shellSort(a);
//        quickSort(a, 0, a.length - 1);
//        quick3waySort(a, 0, a.length - 1);
    }

    /**
     * 冒泡排序
     * 时间复杂度O(N²)
     */
    private static void bubbleSort(Comparable[] a) {
        int len = a.length;
        for (int i = 0; i < len - 1; i++) {
            for (int j = 0; j < len - 1 - i; j++) {
                if (less(a[j + 1], a[j])) {        // 相邻元素两两对比
                    exch(a, j, j + 1);
                }
            }
        }
    }

    /**
     * 选择排序
     * 时间复杂度O(N²)
     */
    private static void selectionSort(Comparable[] a) {
        int N = a.length;
        for (int i = 0; i < N; i++) {
            int min = i;
            for (int j = i + 1; j < N; j++) {
                if (less(a[j], a[min])) {
                    min = j;
                }
            }
            exch(a, i, min);
        }
    }

    /**
     * 插入排序;
     * 对部分有序的数组十分高效，也很适合小规模数组；
     * 时间复杂度O(N²)
     */
    private static void insertionSort(Comparable[] a) {
        for (int i = 1; i < a.length; i++) {
            for (int j = i; j > 0 && less(a[j], a[j - 1]); j--) {
                exch(a, j, j - 1);
            }
        }
    }

    /**
     * 希尔排序；
     * 插入排序的升级版；可以交换相隔较远的元素，并最终用插入排序将局部有序的数组排序
     * 时间复杂度O(N^3/2)
     */
    private static void shellSort(Comparable[] a) {
        int N = a.length;
        int h = 1;
        while (h < N / 3) {
            h = 3 * h + 1;
        }
        while (h >= 1) {
            for (int i = h; i < N; i++) {
                for (int j = i; j >= h && less(a[j], a[j - h]); j -= h) {
                    exch(a, j, j - h);
                }
            }
            h = h / 3;
        }
    }

    /**
     * 快速排序；
     * 在数据量较小的时候性能不如插入排序；
     * 时间复杂度O(NlgN);
     * 使用递归，数据量过大会导致StackOverflow
     */
    private static void quickSort(Comparable[] a, int lo, int hi) {
        int M = 10;//小数据量判定
//        if (hi <= lo) return;
        if (hi <= lo + M) {//优化1①小数据量使用插入排序
            insertionSort(a);
            return;
        }
        int j = partition(a, lo, hi);
        quickSort(a, lo, j - 1);//左半部分排序
        quickSort(a, j + 1, hi);//右半部分排序
    }

    /**
     * 将数组切分为a[lo..i-1],a[i],a[i+1..hi]
     */
    private static int partition(Comparable[] a, int lo, int hi) {
        int i = lo, j = hi + 1;
        Comparable v = a[lo];
//        Comparable v = min(a[lo], a[hi], a[(hi - lo) / 2]);//优化②：三分取样
        while (true) {
            while (less(a[++i], v)) {
                if (i == hi) break;
            }
            while (less(v, a[--j])) {
                if (j == lo) break;
            }
            if (i >= j) break;
            exch(a, i, j);
        }
        exch(a, lo, j);
        return j;
    }


    /**
     * 三向切分的快速排序；
     * 它从左到右遍历数组一次，维护一个指针lt使得a[lo..lt-1]中的元素都小于v，
     * 一个指针gt使得a[gt+1..hi]中的元素都大于v，一个指针i使得a[lt..i-1]中的元素都等于v。
     */
    private static void quick3waySort(Comparable[] a, int lo, int hi) {
        if (hi <= lo) return;
        int lt = lo, i = lo + 1, gt = hi;
        Comparable v = a[lo];
        while (i < gt) {
            int cmp = a[i].compareTo(v);
            if (cmp < 0) exch(a, lt++, i++);
            else if (cmp > 0) exch(a, i, gt--);
            else i++;
        }
        quick3waySort(a, lo, lt - 1);
        quick3waySort(a, gt + 1, hi);
    }

    public static Comparable min(Comparable... a) {
        if (a == null) {
            return null;
        } else if (a.length == 1) {
            return a[0];
        }
        Comparable min = a[0];
        for (int i = 1; i < a.length; i++) {
            if (less(min, a[i])) {
                min = a[i];
            }
        }
        return min;
    }

    /**
     * @return true:v小于等于w
     */
    private static boolean less(Comparable v, Comparable w) {
        return v.compareTo(w) < 0;
    }

    /**
     * 交换a数组中i和j元素的位置
     */
    private static void exch(Comparable[] a, int i, int j) {
        Comparable t = a[i];
        a[i] = a[j];
        a[j] = t;
    }

    public static void show(Comparable[] a) {
        for (int i = 0; i < a.length; i++) {
            System.out.print(a[i]);
            System.out.print(" ");
        }
        System.out.println();
    }

    public static boolean isSorted(Comparable[] a) {
        for (int i = 1; i < a.length; i++) {
            if (less(a[i], a[i - 1])) {
                return false;
            }
        }
        return true;
    }

}
