package com.huburt.app.common.imageloader;

import android.graphics.drawable.Drawable;

/**
 * Created by hubert on 2017/12/18.
 */

public class ImageOptions {
    int placeHolder = -1; //当没有成功加载的时候显示的图片
    Drawable placeHolderDrawable;

    int error = -1;  //加载错误的时候显示的drawable
    Drawable errorDrawable;

    ImageShape shape;
    int cornerRadius = 4;//默认圆角4，只有在shape为ROUND时才起效

    boolean centerCrop;
    boolean fitCenter;

    private ImageOptions() {

    }

    public static ImageOptions newInstance() {
        return new ImageOptions();
    }


    public ImageOptions placeHolder(int placeHolder) {
        this.placeHolder = placeHolder;
        return this;
    }

    public ImageOptions placeHolderDrawable(Drawable placeHolderDrawable) {
        this.placeHolderDrawable = placeHolderDrawable;
        return this;
    }

    public ImageOptions error(int error) {
        this.error = error;
        return this;
    }

    public ImageOptions errorDrawable(Drawable errorDrawable) {
        this.errorDrawable = errorDrawable;
        return this;
    }

    /**
     * 指定图片类型，如圆形 圆角
     *
     * @param shape {@link ImageShape}
     */
    public ImageOptions shape(ImageShape shape) {
        this.shape = shape;
        return this;
    }

    /**
     * 指定图片的圆角，只有在shape指定为{@link ImageShape#ROUND}才生效
     *
     * @param cornerRadius 圆角半径 单位dp
     */
    public ImageOptions cornerRadius(int cornerRadius) {
        this.cornerRadius = cornerRadius;
        return this;
    }

    public ImageOptions centerCrop() {
        this.centerCrop = true;
        return this;
    }

    public ImageOptions fitCenter() {
        this.fitCenter = true;
        return this;
    }

    public enum ImageShape {
        NORMAL, ROUND, CIRCLE
    }
}
