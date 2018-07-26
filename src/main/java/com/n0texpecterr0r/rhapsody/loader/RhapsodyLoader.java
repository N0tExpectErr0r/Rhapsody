package com.n0texpecterr0r.rhapsody.loader;

/**
 * @author Created by Nullptr
 * @date 2018/7/25 13:04
 * @describe 图片加载库
 */
public class RhapsodyLoader {

    private volatile static RhapsodyLoader sInstance;   // 单例实例

    /**
     * 初始化RhapsodyLoader
     */
    private RhapsodyLoader() {
    }

    /**
     * 单例获取实例
     */
    public static RhapsodyLoader get() {
        if (sInstance == null) {
            synchronized (RhapsodyLoader.class) {
                if (sInstance == null) {
                    sInstance = new RhapsodyLoader();
                }
            }
        }
        return sInstance;
    }

    /**
     * 加载对应path的图片
     *
     * @param path 图片的path
     */
    public ImageTaskDispatcher load(String path) {
        return new ImageTaskDispatcher(path);
    }
}
