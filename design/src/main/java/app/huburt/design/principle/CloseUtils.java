package app.huburt.design.principle;

import java.io.Closeable;
import java.io.IOException;

/**
 * Created by hubert on 2018/4/24.
 */

public final class CloseUtils {
    private CloseUtils() {
    }

    public static void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
