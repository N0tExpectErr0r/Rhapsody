package com.n0texpecterr0r.rhapsody;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.ImageView;

/**
 * @author Created by Nullptr
 * @date 2018/7/25 12:29
 * @describe 图片引擎的接口，要加入其它引擎只需实现该接口实现方法
 */
public interface BaseEngine {

    /**
     * 加载缩略图
     * @param context 上下文
     * @param size 缩略图尺寸(固定1:1)
     * @param drawable
     * @param imageView
     * @param uri
     */
    void loadThumbnail(Context context, int size,Drawable drawable, ImageView imageView, Uri uri);

    void loadImage(Context context, int sizeX, int sizeY, ImageView imageView, Uri uri);
}
