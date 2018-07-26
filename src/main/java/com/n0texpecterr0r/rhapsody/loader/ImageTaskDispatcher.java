package com.n0texpecterr0r.rhapsody.loader;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.LruCache;
import android.widget.ImageView;


/**
 * @author Created by Nullptr
 * @date 2018/7/25 16:07
 * @describe 图片任务分发器
 */
public class ImageTaskDispatcher {

    private String mPath;                    // 图片路径
    private int mTargetWidth;               // 目标图片宽度
    private int mTargetHeight;              // 目标图片高度
    private ImageView mImageView;           // 目标ImageView
    private Handler mUIHandler;             // 主线程Handler，用于回调
    private TaskDispatcher mDispatcher;     // 线程调度器，分发任务。
    private static LruCache<String,Bitmap> sImageCache;     //图片缓存容器

    /**
     * 构造函数
     *
     * @param path 图片的path
     */
    ImageTaskDispatcher(String path) {
        mPath = path;
        mUIHandler = new Handler(Looper.getMainLooper());
        mDispatcher = TaskDispatcher.getInstance();
        mTargetWidth = 0;
        mTargetHeight = 0;

        int memory = (int) (Runtime.getRuntime().maxMemory()/8);   // 取1/8内存存储图片
        sImageCache = new LruCache<String,Bitmap>(memory){
            @Override
            protected int sizeOf(String key, Bitmap value) {
                // 计算图片的大小
                return value.getRowBytes()*value.getHeight();
            }
        };
    }

    /**
     * 设置要获取的图片大小
     * @param targetWidth 目标宽度
     * @param targetHeight 目标高度
     */
    public ImageTaskDispatcher setSize(int targetWidth,int targetHeight){
        mTargetWidth = targetWidth;
        mTargetHeight = targetHeight;
        return this;
    }

    /**
     * 加载图片,执行图片任务的分发
     * @param imageView 目标ImageView
     */
    public void into(ImageView imageView){
        mImageView = imageView;
        // 1.先查看内存缓存
        Bitmap bitmap = sImageCache.get(mPath);
        if (bitmap != null){
            // 找到图片
            imageView.setImageBitmap(bitmap);
            return;
        }

        // 2.内存没有缓存，获取本地资源
        // 向图片任务分发器分发新任务
        Runnable loadTask = new Runnable() {
            @Override
            public void run() {
                Bitmap localBitmap = BitmapUtil.getBitmapFromPath(mPath);
                if (mTargetWidth != 0 && mTargetHeight != 0) {
                    // 如果设置了大小,对图片进行缩放
                    assert localBitmap != null;
                    localBitmap = BitmapUtil.scaleBitmap(localBitmap, mTargetWidth, mTargetHeight);
                }
                final Bitmap finalBitmap = localBitmap;
                mUIHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mImageView.setImageBitmap(finalBitmap);
                    }
                });
                sImageCache.put(mPath,finalBitmap);
            }
        };
        mDispatcher.executeTask(loadTask);
    }
}
