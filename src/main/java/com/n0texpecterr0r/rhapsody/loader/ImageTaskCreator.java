package com.n0texpecterr0r.rhapsody.loader;

import android.animation.ObjectAnimator;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.util.LruCache;
import android.widget.ImageView;
import com.n0texpecterr0r.rhapsody.loader.TaskDispatcher.Type;


/**
 * @author Created by Nullptr
 * @date 2018/7/25 16:07
 * @describe 图片任务创建器
 */
public class ImageTaskCreator {

    private String mPath;                    // 图片路径
    private int mTargetWidth;               // 目标图片宽度
    private int mTargetHeight;              // 目标图片高度
    private Handler mUIHandler;             // 主线程Handler，用于回调
    private TaskDispatcher mDispatcher;     // 线程调度器，分发任务。
    private volatile static LruCache<String, Bitmap> sImageCache;     //图片缓存容器

    /**
     * 构造函数
     *
     * @param path 图片的path
     */
    ImageTaskCreator(String path) {
        mPath = path;
        mUIHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                ImageHolder holder = (ImageHolder) msg.obj;
                Bitmap bitmap = holder.bitmap;
                ImageView imgView = holder.imageView;
                String path = holder.path;
                //防止错乱，比较tag后再进行设置
                if (imgView.getTag().toString().equals(path)) {
                    imgView.setImageBitmap(bitmap);
                    ObjectAnimator.ofFloat(imgView,"alpha",0F,1F)
                            .setDuration(500)
                            .start();
                    addBitmapToMemoryCache(path, bitmap);
                }
            }
        };
        mDispatcher = TaskDispatcher.getInstance(4, Type.LIFO);
        mTargetWidth = 0;
        mTargetHeight = 0;

        int memory = (int) (Runtime.getRuntime().maxMemory()/4);   // 取1/4内存存储图片

        if (sImageCache == null) {
            sImageCache = new LruCache<String, Bitmap>(memory) {
                @Override
                protected int sizeOf(String key, Bitmap value) {
                    // 计算图片的大小
                    return value.getByteCount();
                }
            };
        }
    }

    /**
     * 设置要获取的图片大小
     *
     * @param targetWidth 目标宽度
     * @param targetHeight 目标高度
     */
    public ImageTaskCreator setSize(int targetWidth, int targetHeight) {
        mTargetWidth = targetWidth;
        mTargetHeight = targetHeight;
        return this;
    }

    /**
     * 加载图片,执行图片任务的分发
     *
     * @param imageView 目标ImageView
     */
    public void into(final ImageView imageView) {
        // 目标ImageView
        imageView.setTag(mPath);
        // 1.先查看内存缓存
        Bitmap bitmap = getBitmapFromMemoryCache(mPath);
        final String path = mPath;
        if (bitmap != null) {
            // 找到图片
            imageView.setImageBitmap(bitmap);
            return;
        }

        // 2.内存没有缓存，获取本地资源
        // 向图片任务分发器分发新任务
        Runnable loadTask = new Runnable() {
            @Override
            public void run() {
                Bitmap localBitmap = BitmapUtil.getBitmapFromPath(mPath,mTargetWidth,mTargetHeight);

                ImageHolder imageHolder = new ImageHolder(localBitmap, imageView, path);
                Message message = Message.obtain();
                message.obj = imageHolder;
                //回到主线程更新ui
                mUIHandler.sendMessage(message);
            }
        };
        mDispatcher.executeTask(loadTask);
    }


    /**
     * 将图片加入内存缓存
     * @param key key
     * @param bitmap bitmap
     */
    private synchronized void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemoryCache(key) == null) {
            sImageCache.put(key, bitmap);  //图片没有放入时将图片放入内存
        }
    }


    /**
     * 从内存缓存获取图片
     * @param key key
     * @return 得到的图片
     */
    private synchronized Bitmap getBitmapFromMemoryCache(String key) {
        return sImageCache.get(key);   //从内存取出对应图片
    }

    /**
     * 为了防止图片错乱，在message内判断后再加载图片
     */
    private class ImageHolder {
        Bitmap bitmap;
        ImageView imageView;
        String path;

        ImageHolder(Bitmap bitmap, ImageView imageView, String path) {
            this.bitmap = bitmap;
            this.imageView = imageView;
            this.path = path;
        }
    }
}
