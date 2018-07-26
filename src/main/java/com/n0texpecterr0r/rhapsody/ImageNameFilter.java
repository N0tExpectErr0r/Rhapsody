package com.n0texpecterr0r.rhapsody;

import com.n0texpecterr0r.rhapsody.bean.ImageType;
import java.io.File;
import java.io.FilenameFilter;
import java.util.Set;

/**
 * @author Created by Nullptr
 * @date 2018/7/26 11:29
 * @describe 图片名称过滤器
 */
public class ImageNameFilter implements FilenameFilter {
    private Set<ImageType> mImageTypes;

    public ImageNameFilter(Set<ImageType> imageTypes){
        mImageTypes = imageTypes;
    }

    @Override
    public boolean accept(File dir, String name) {
        // 遍历所有选择的后缀名
        for (ImageType imageType : mImageTypes) {
            for (String extension : imageType.getExtensions()) {
                if (name.endsWith(extension)) {
                    //如果是以该后缀名结尾，返回true
                    return true;
                }
            }
        }
        return false;
    }
}