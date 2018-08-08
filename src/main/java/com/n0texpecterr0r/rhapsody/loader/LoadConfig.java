package com.n0texpecterr0r.rhapsody.loader;

import android.widget.ImageView;

/**
 * @author Created by Nullptr
 * @date 2018/8/8 9:35
 * @describe 加载配置
 */
public class LoadConfig {

    private static LoadConfig sInstance;    // 单例
    public int mTargetWidth;               // 目标图片宽度
    public int mTargetHeight;              // 目标图片高度
    public boolean isUseCache;             // 是否复用缓存

    private LoadConfig(){
    }

    public static LoadConfig getInstance(){
        if(sInstance == null){
            synchronized (LoadConfig.class){
                if (sInstance == null){
                    sInstance = new LoadConfig();
                }
            }
        }
        return sInstance;
    }

    public static LoadConfig getResetInstance(){
        LoadConfig config = getInstance();
        config.reset();
        return config;
    }

    private void reset() {
        mTargetWidth = 0;
        mTargetHeight = 0;
        isUseCache = true;
    }
}
