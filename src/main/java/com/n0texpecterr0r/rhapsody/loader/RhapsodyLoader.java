package com.n0texpecterr0r.rhapsody.loader;

import android.content.Context;
import android.net.Uri;

/**
 * @author Created by Nullptr
 * @date 2018/7/25 13:04
 * @describe 图片加载库
 */
public class RhapsodyLoader {

    private Context mContext;                           // 上下文
    private volatile static RhapsodyLoader sInstance;   // 单例实例

    /**
     * 初始化RhapsodyLoader
     *
     * @param context 传入的上下文
     */
    private RhapsodyLoader(Context context) {
        // 获取Application级的Context
        mContext = context.getApplicationContext();
    }

    /**
     * 单例获取实例
     *
     * @param context 传入的上下文
     */
    public static RhapsodyLoader get(Context context) {
        if (sInstance == null) {
            synchronized (RhapsodyLoader.class) {
                if (sInstance == null) {
                    sInstance = new RhapsodyLoader(context);
                }
            }
        }
        return sInstance;
    }

    /**
     * 加载对应uri的图片
     *
     * @param uri 图片的uri
     */
    public ImageTaskDispatcher load(Uri uri) {
        return new ImageTaskDispatcher(uri, mContext);
    }
}
