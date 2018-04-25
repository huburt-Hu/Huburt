package app.huburt.design.singleton;

/**
 * Created by hubert on 2018/4/25.
 */

public class DCLSingleton {
    private volatile static DCLSingleton instance;

    private DCLSingleton() {
    }

    public static DCLSingleton getInstance() {
        if (instance == null) {
            synchronized (DCLSingleton.class) {
                if (instance == null) {
                    instance = new DCLSingleton();
                }
            }
        }
        return instance;
    }
}
