package com.huburt.app.huburt.test;

/**
 * Created by hubert on 2018/5/3.
 */
public class TestLambda {

    public static void main(String[] arg) {
        MathOperation addition = (int a, int b) -> a + b;

        final int num = 1;
        Converter<Integer, String> s = (param) -> System.out.println(String.valueOf(param + num));
        s.convert(2);  // 输出结果为 3

    }

    interface MathOperation {
        int operation(int a, int b);
    }

    public interface Converter<T1, T2> {
        void convert(int i);
    }

    @FunctionalInterface
    public interface Supplier<T> {
        T get();
    }

    static class Car {
        //Supplier是jdk1.8的接口，这里和lamda一起使用了
        public static Car create(final Supplier<Car> supplier) {
            return supplier.get();
        }

        public static void collide(final Car car) {
            System.out.println("Collided " + car.toString());
        }

        public void follow(final Car another) {
            System.out.println("Following the " + another.toString());
        }

        public void repair() {
            System.out.println("Repaired " + this.toString());
        }
    }
}
