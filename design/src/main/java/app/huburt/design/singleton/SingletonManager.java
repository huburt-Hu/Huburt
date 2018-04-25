package app.huburt.design.singleton;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by hubert on 2018/4/25.
 */

public class SingletonManager {
    private static Map<String, Object> objectMap = new HashMap<>();

    private SingletonManager() {
    }

    public static void registerService(String key, Object instance) {
        if (!objectMap.containsKey(key)) {
            objectMap.put(key, instance);
        }
    }

    public static Object getService(String key) {
        return objectMap.get(key);
    }
}
