package com.n0texpecterr0r.rhapsody.loader;

import android.graphics.Bitmap;
import android.graphics.PointF;
import android.util.Log;
import android.util.LruCache;

/**
 * @author Created by Nullptr
 * @date 2018/8/8 9:17
 * @describe 单例模式的图片缓存池
 */
public class BitmapCache {
    private LruCache<String, Bitmap> sImageCache;     // 图片缓存容器
    private volatile static BitmapCache sInstance;  // 单例

    private BitmapCache(){
        int memory = (int) (Runtime.getRuntime().maxMemory() / 8);   // 取1/8内存存储图片

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

    public static BitmapCache getInstance(){
        if (sInstance == null){
            synchronized (BitmapCache.class){
                if (sInstance == null){
                    sInstance = new BitmapCache();
                }
            }
        }
        return sInstance;
    }

    /**
     * 获取缓存中的Bitmap
     * @param key 以url为key
     * @return key对应的Bitmap
     */
    public Bitmap get(String key){
        return sImageCache.get(key);   // 从内存取出对应图片
    }

    /**
     * 将图片加入内存缓存
     * @param key key 图片的key
     * @param bitmap bitmap 对应的图片
     */
    public void add(String key, Bitmap bitmap) {
        if (key == null || bitmap == null) {
            return;
        }
        if (get(key) == null) {
            sImageCache.put(key, bitmap);  //图片没有放入时将图片放入内存
        }
    }

}
