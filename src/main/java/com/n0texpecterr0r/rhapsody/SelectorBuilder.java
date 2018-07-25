package com.n0texpecterr0r.rhapsody;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import java.util.Set;

/**
 * @author Created by Nullptr
 * @date 2018/7/25 10:23
 * @describe 图片选择配置的Builder
 */
public final class SelectorBuilder {

    private final ImageSelector mSelector;
    private final SelectConfig mConfig;

    /**
     * 构造方法
     *
     * @param selector 传入的选择器
     * @param imageTypes 传入的图片类型
     */
    SelectorBuilder(ImageSelector selector, @NonNull Set<ImageType> imageTypes) {
        mSelector = selector;
        mConfig = SelectConfig.getResetInstance();
        mConfig.imageTypes = imageTypes;
    }

    /**
     * 设置最大选择个数
     *
     * @param maxSelectCount 最大选择个数
     */
    public SelectorBuilder setMaxSelect(int maxSelectCount) {
        if (maxSelectCount < 1) {
            maxSelectCount = 1;
        }
        mConfig.maxSelectCount = maxSelectCount;
        return this;
    }

    /**
     * 设置缩略图缩放率
     *
     * @param thumbnailScale 缩略图缩放率
     */
    public SelectorBuilder setThumbnailScale(float thumbnailScale) {
        if (thumbnailScale > 0F && thumbnailScale <= 1F) {
            mConfig.thumbnailScale = thumbnailScale;
        } else {
            throw new IllegalArgumentException("缩略图缩放率应当介于 (0.0F , 1.0F] ");
        }

        return this;
    }

    /**
     * 开启选择界面并有返回结果
     * @param requestCode 请求码
     */
    public void startForResult(int requestCode){
        Activity activity = mSelector.getActivity();
        if (activity != null){
            Fragment fragment = mSelector.getFragment();
            //创建Activity
            Intent intent = new Intent(activity, SelectActivity.class);
            if (fragment == null){
                //如果Fragment为null说明从Acitivty启动
                activity.startActivityForResult(intent,requestCode);
            }else {
                //如果Fragment不为null，说明从Fragment启动
                fragment.startActivityForResult(intent,requestCode);
            }
        }
    }
}
