package com.huburt.app.common.imageloader;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.FitCenter;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

public class GlideImageLoaderStrategy implements ImageLoader {

    @Override
    public void load(View v, String url) {
        load(v, url, null);
    }

    @Override
    public void load(View v, String url, ImageOptions options) {
        if (v instanceof ImageView) {
            ImageView imageView = (ImageView) v;
            Glide.with(imageView.getContext()).load(url)
                    .apply(wrapOptions(options, true))
                    .into(imageView);
        } else {
            throw new IllegalArgumentException("请传入ImageView或其子类！");
        }
    }

    @Override
    public void load(View v, int resId) {
        load(v, resId, null);
    }

    @Override
    public void load(View v, int resId, ImageOptions options) {
        if (v instanceof ImageView) {
            ImageView imageView = (ImageView) v;
            Glide.with(imageView.getContext()).load(resId)
                    .apply(wrapOptions(options, false))
                    .into(imageView);
        } else {
            throw new IllegalArgumentException("请传入ImageView或其子类！");
        }
    }

    @Override
    public void load(View v, Object object) {
        load(v, object, null);
    }

    @Override
    public void load(View v, Object object, ImageOptions options) {
        if (v instanceof ImageView) {
            ImageView imageView = (ImageView) v;
            Glide.with(imageView.getContext()).load(object)
                    .apply(wrapOptions(options, true))
                    .into(imageView);
        } else {
            throw new IllegalArgumentException("请传入ImageView或其子类！");
        }
    }

    @NonNull
    private RequestOptions wrapOptions(ImageOptions options, boolean diskCache) {
        RequestOptions requestOptions = new RequestOptions();
        if (options != null) {
            if (options.error != -1) {
                requestOptions = requestOptions.error(options.error);
            }
            if (options.errorDrawable != null) {
                requestOptions = requestOptions.error(options.errorDrawable);
            }
            if (options.placeHolder != -1) {
                requestOptions = requestOptions.placeholder(options.placeHolder);
            }
            if (options.placeHolderDrawable != null) {
                requestOptions = requestOptions.placeholder(options.placeHolderDrawable);
            }
            if (options.shape == ImageOptions.ImageShape.CIRCLE) {
                requestOptions = requestOptions.circleCrop();
            } else if (options.shape == ImageOptions.ImageShape.ROUND && options.cornerRadius > 0) {
                RoundedCorners roundedCorners = new RoundedCorners((int) (Resources.getSystem().getDisplayMetrics().density * options.cornerRadius));
                if (options.centerCrop) {
                    requestOptions = requestOptions.transform(new MultiTransformation<Bitmap>(new CenterCrop(), roundedCorners));
                } else if (options.fitCenter) {
                    requestOptions = requestOptions.transform(new MultiTransformation<Bitmap>(new FitCenter(), roundedCorners));
                } else {
                    requestOptions = requestOptions.transform(roundedCorners);
                }
            } else {
                if (options.centerCrop) {
                    requestOptions = requestOptions.centerCrop();
                } else if (options.fitCenter) {
                    requestOptions = requestOptions.fitCenter();
                }
            }
        }
        if (!diskCache) {
            requestOptions = requestOptions.diskCacheStrategy(DiskCacheStrategy.NONE);
        }
        return requestOptions;
    }
}