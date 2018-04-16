package com.huburt.app.common.imageloader;

import android.view.View;

/**
 * Created by hubert
 * <p>
 * Created on 2017/7/20.
 */

public class ImageLoaderManager implements ImageLoader {
    private static final ImageLoaderManager INSTANCE = new ImageLoaderManager();
    private ImageLoader imageLoader;

    private ImageLoaderManager() {
        imageLoader = new GlideImageLoaderStrategy();
    }

    public static ImageLoaderManager getInstance() {
        return INSTANCE;
    }

    public void setImageLoader(ImageLoader loader) {
        if (loader != null)
            imageLoader = loader;
    }

    @Override
    public void load(View v, String url) {
        imageLoader.load(v, url);
    }

    @Override
    public void load(View v, String url, ImageOptions options) {
        imageLoader.load(v, url, options);
    }

    @Override
    public void load(View v, int resId) {
        imageLoader.load(v, resId);
    }

    @Override
    public void load(View v, int resId, ImageOptions options) {
        imageLoader.load(v, resId, options);
    }

    @Override
    public void load(View v, Object object) {
        imageLoader.load(v, object);
    }

    @Override
    public void load(View v, Object object, ImageOptions options) {
        imageLoader.load(v, object, options);
    }
}
