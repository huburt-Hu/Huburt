package app.huburt.design.singleton;

/**
 * Created by hubert on 2018/4/25.
 * <p>
 * 懒汉式
 */

public class LazySingleton {
    private static LazySingleton instance;

    private LazySingleton() {
    }

    public static synchronized LazySingleton getInstance() {
        if (instance == null) {
            instance = new LazySingleton();
        }
        return instance;
    }
}
