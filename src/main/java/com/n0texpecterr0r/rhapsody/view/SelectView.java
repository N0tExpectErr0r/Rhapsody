package com.n0texpecterr0r.rhapsody.view;

import com.n0texpecterr0r.rhapsody.bean.Floder;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Created by Nullptr
 * @date 2018/7/26 10:30
 * @describe 主界面接口
 */
public interface SelectView {

    /**
     * 接收到文件夹列表的回调
     * @param floders 收到的文件夹列表
     */
    void onFloder(List<Floder> floders);

    /**
     * 接收到图片地址的回调
     * @param imagePaths 收到的图片地址列表
     */
    void onImages(List<String> imagePaths);

    /**
     * 接收到所有图片的地址的回调
     * @param imagePaths 收到的图片地址列表
     */
    void onAllImages(List<String> imagePaths);
}
