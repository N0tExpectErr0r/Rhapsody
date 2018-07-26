package com.n0texpecterr0r.rhapsody.bean;

import android.graphics.Bitmap;

/**
 * @author Created by Nullptr
 * @date 2018/7/26 10:09
 * @describe 文件夹bean类
 */
public class Floder {
    private String dir;         // 文件夹路径
    private String name;        // 文件夹名
    private String coverPath;   // 首个图片地址
    private int pictureCount;   // 图片个数

    public String getDir() {
        return dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
        this.name = dir.substring(dir.lastIndexOf("/")+1);    //用dir初始化文件夹名
    }

    public String getName() {
        return name;
    }

    public String getCoverPath() {
        return coverPath;
    }

    public void setCoverPath(String coverPath) {
        this.coverPath = coverPath;
    }

    public int getPictureCount() {
        return pictureCount;
    }

    public void setPictureCount(int pictureCount) {
        this.pictureCount = pictureCount;
    }
}
