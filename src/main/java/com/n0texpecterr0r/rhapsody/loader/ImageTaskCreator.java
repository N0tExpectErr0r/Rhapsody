package com.n0texpecterr0r.rhapsody.loader;

import android.widget.ImageView;


/**
 * @author Created by Nullptr
 * @date 2018/7/25 16:07
 * @describe 图片任务创建器
 */
public class ImageTaskCreator {
    private String path;
    private LoadConfig mConfig;

    /**
     * 构造函数
     *
     * @param path 图片的path
     */
    ImageTaskCreator(String path) {

        mConfig = LoadConfig.getResetInstance();
        this.path = path;
    }

    /**
     * 设置要获取的图片大小
     *
     * @param targetWidth 目标宽度
     * @param targetHeight 目标高度
     */
    public ImageTaskCreator setSize(int targetWidth, int targetHeight) {
        mConfig.mTargetWidth = targetWidth;
        mConfig.mTargetHeight = targetHeight;
        return this;
    }


    /**
     * 设置是否复用缓存
     *
     * @param useCache 是否复用缓存
     */
    public ImageTaskCreator useCache(boolean useCache) {
        mConfig.isUseCache = useCache;
        return this;
    }

    /**
     * 加载图片,执行图片任务的分发
     *
     * @param imageView 目标ImageView
     */
    public void into(final ImageView imageView) {
        imageView.setTag(path);
        CacheTaskResolver cacheTaskResolver = new CacheTaskResolver(path,imageView);
        LocalTaskResolver localTaskResolver = new LocalTaskResolver(path,imageView);
        cacheTaskResolver.setNextResolver(localTaskResolver);
        cacheTaskResolver.handleTask();
    }


}
