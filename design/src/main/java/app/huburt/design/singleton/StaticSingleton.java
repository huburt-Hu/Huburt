package app.huburt.design.singleton;

/**
 * Created by hubert on 2018/4/25.
 */

public class StaticSingleton {
    private StaticSingleton() {
    }

    public static StaticSingleton getInstance() {
        return SingletonHolder.sInstance;
    }

    private static class SingletonHolder {
        private static final StaticSingleton sInstance = new StaticSingleton();
    }
}
