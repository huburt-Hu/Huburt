package app.huburt.design.singleton;

import java.io.ObjectStreamException;
import java.io.Serializable;

/**
 * Created by hubert on 2018/4/25.
 * <p>
 * 饿汉式单例
 */

public class EagerSingleton implements Serializable {
    private static final EagerSingleton sInstance = new EagerSingleton();

    private EagerSingleton() {
    }

    public static EagerSingleton getInstance() {
        return sInstance;
    }

    //支持序列化的单例
    private Object readResolve() throws ObjectStreamException {
        return sInstance;
    }
}
