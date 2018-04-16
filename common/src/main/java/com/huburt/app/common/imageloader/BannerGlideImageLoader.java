package com.huburt.app.common.imageloader;

import android.content.Context;
import android.widget.ImageView;

import com.youth.banner.loader.ImageLoader;

/**
 * Created by Yune on 2017/8/30.
 * Description:
 */

public class BannerGlideImageLoader extends ImageLoader {

    @Override
    public void displayImage(Context context, Object path, ImageView imageView) {
        ImageLoaderManager.getInstance().load(imageView, path);
//                , ImageOptions.newInstance().placeHolder(R.drawable.default_img)
//                        .error(R.drawable.banner_defalt));
    }

}
