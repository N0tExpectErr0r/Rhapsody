package com.n0texpecterr0r.rhapsody.loader;

import android.graphics.Bitmap;
import android.widget.ImageView;
import com.n0texpecterr0r.rhapsody.loader.base.AbsTaskResolver;

/**
 * @author Created by Nullptr
 * @date 2018/8/8 9:16
 * @describe 图片缓存加载部分
 */
public class CacheTaskResolver extends AbsTaskResolver {
    private final LoadConfig mConfig;   // 配置
    private ImageView mImageView;
    private String mPath;

    public CacheTaskResolver(String path, ImageView imageView){
        mConfig = LoadConfig.getInstance();
        mImageView = imageView;
        mPath = path;
    }

    @Override
    public void handleTask() {
        if (mConfig.isUseCache){
            // 如果要复用缓存
            Bitmap bitmap = BitmapCache.getInstance().get(mPath);
            if (bitmap != null) {
                // 找到图片
                mImageView.setImageBitmap(bitmap);
                return;
            }else{
                // 找不到图片，让下一个责任链负责
                mNextResolver.handleTask();
            }
        }else{
            // 不采用缓存，让下一个责任链负责
            mNextResolver.handleTask();
        }
    }
}
