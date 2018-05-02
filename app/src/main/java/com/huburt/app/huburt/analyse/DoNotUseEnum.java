package com.huburt.app.huburt.analyse;

import android.support.annotation.IntDef;
import android.support.annotation.IntRange;

/**
 * Created by hubert on 2018/5/2.
 */
public class DoNotUseEnum {
    public static final int VALUE1 = 1;
    public static final int VALUE2 = 2;
    public static final int VALUE3 = 3;

    @IntDef({VALUE1, VALUE2, VALUE3})
    public @interface Values {
    }

    int func(@Values int value) {
        int result;
        switch (value) {
            case VALUE1:
                result = -1;
                break;
            case VALUE2:
                result = -2;
                break;
            case VALUE3:
            default:
                result = -3;
                break;
        }
        return result;
    }

    void test() {
        func(VALUE1);
//        func(1);

//        setAlpha(101);
    }

    @IntRange(from = 0, to = 100)
    public @interface Alpha {
    }

    void setAlpha(@Alpha int alpha) {

    }

    public static enum Value {
        VALUE1, VALUE2, VALUE3
    }

    int funcE(Value value) {
        int result;
        switch (value) {
            case VALUE1:
                result = -1;
                break;
            case VALUE2:
                result = -2;
                break;
            case VALUE3:
            default:
                result = -3;
                break;
        }
        return result;
    }

}
