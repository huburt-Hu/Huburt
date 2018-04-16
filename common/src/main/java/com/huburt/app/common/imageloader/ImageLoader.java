package com.huburt.app.common.imageloader;

import android.view.View;

/**
 * Created by julie on 2017/2/17.
 */

public interface ImageLoader {
    void load(View v, String url);

    void load(View v, String url, ImageOptions options);

    void load(View v, int resId);

    void load(View v, int resId, ImageOptions options);

    void load(View v, Object object);

    void load(View v, Object object, ImageOptions options);
}
