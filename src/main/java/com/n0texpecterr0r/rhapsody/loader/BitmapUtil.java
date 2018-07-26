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
     * 将uri转换为Bitmap
     *
     * @param context 获取ContentResolver的上下文
     * @param uri 要转换的uri
     * @return 转换后的Bitmap
     */
    public static Bitmap getBitmapFromUri(Context context, Uri uri) {
        try {
            // 读取uri所在的图片
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
            return bitmap;
        } catch (Exception e) {
            Log.e("[Android]", e.getMessage());
            Log.e("[Android]", "目录为：" + uri);
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 从图片路径获取图片
     *
     * @param imagePath 图片路径
     * @return 得到的bitmap
     */
    public static Bitmap getBitmapFromPath(String imagePath, int width, int height) {
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
        }else if (height == 0 && width!=0){
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
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
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

    /**
     * 图片质量压缩
     *
     * @param image 要压缩的图片
     * @return 压缩后的图片
     */
    public static Bitmap compressImage(Bitmap image) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > 100) {  // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();
            // 第一个参数 ：图片格式 ，第二个参数： 图片质量，100为最高，0为最差  ，第三个参数：保存压缩后的数据的流
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 压缩options%，把压缩后的数据存放到baos中
            options -= 10;//每次都减少10
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
        return bitmap;
    }

    /**
     * 对图片进行等比例缩放
     *
     * @param bitmap 要缩放的图片
     * @param newWidth 目标宽度
     * @return 缩放后图片
     */
    public static Bitmap scaleBitmap(Bitmap bitmap, int newWidth) {

        // 获得图片的宽高
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        // 计算缩放比例
        float newHeight = newWidth * height / width;
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = newHeight / height;
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // 得到新的图片
        Bitmap outBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
        return outBitmap;
    }

}
