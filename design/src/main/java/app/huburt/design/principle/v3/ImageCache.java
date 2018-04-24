package app.huburt.design.principle.v3;

import android.graphics.Bitmap;

/**
 * Created by hubert on 2018/4/24.
 */

public interface ImageCache {
    void put(String url, Bitmap bitmap);

    Bitmap get(String url);
}
