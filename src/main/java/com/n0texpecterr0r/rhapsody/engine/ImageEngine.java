package com.n0texpecterr0r.rhapsody.engine;

import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;

/**
 * @author Created by Nullptr
 * @date 2018/7/25 12:29
 * @describe 图片引擎的接口，要加入其它引擎只需实现该接口实现方法
 */
public interface ImageEngine {

    /**
     * 加载缩略图
     * @param context 上下文
     * @param size 缩略图尺寸(固定1:1)
     * @param imageView 要加载的ImageView
     * @param path 图片的路径
     */
    void loadThumbnail(Context context, int size, ImageView imageView, String path);

    /**
     * 加载图片
     * @param context 上下文
     * @param sizeX X方向图片大小
     * @param sizeY Y方向图片大小
     * @param imageView 要加载的ImageView
     * @param path 图片路径
     */
    void loadImage(Context context, int sizeX, int sizeY, ImageView imageView, String path);
}
