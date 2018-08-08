package com.n0texpecterr0r.rhapsody.loader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Matrix;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * @author Created by Nullptr
 * @date 2018/7/25 16:36
 * @describe Bitmap工具类
 */
public class BitmapUtil {

    /**
     * 从图片路径获取图片
     *
     * @param imagePath 图片路径
     * @return 得到的bitmap
     */
    static Bitmap getBitmapFromPath(String imagePath, int width, int height) {
        Bitmap bitmap = null;
        BitmapFactory.Options options = new Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, options);
        if (width != 0 && height != 0) {
            // 缩放为指定大小
            options.inSampleSize = calculateInSampleSize(options, width, height);
        }else if (width == 0 && height!=0){
            // 以高度为衡量标准缩放为正方形
            options.inSampleSize = calculateInSampleSize(options,height,height);
        }else if (width != 0){
            // 以宽度为衡量标准缩放为正方形
            options.inSampleSize = calculateInSampleSize(options,width,width);
        }
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(imagePath,options);
    }

    /**
     * 计算Option的宽高
     *
     * @param options 对应option
     * @param reqWidth 需要的宽
     * @param reqHeight 需要的高
     * @return 计算结果
     */
    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // 原始图片的宽高
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // 在保证解析出的bitmap宽高分别大于目标尺寸宽高的前提下，取可能的inSampleSize的最大值
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
}
