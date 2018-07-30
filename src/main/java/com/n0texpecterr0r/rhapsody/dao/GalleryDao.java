package com.n0texpecterr0r.rhapsody.dao;

import static android.provider.MediaStore.Images.ImageColumns.DATE_TAKEN;
import static android.provider.MediaStore.MediaColumns.DATE_ADDED;
import static android.provider.MediaStore.MediaColumns.DATE_MODIFIED;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore.Images.Media;
import com.n0texpecterr0r.rhapsody.bean.ImageType;
import com.n0texpecterr0r.rhapsody.SelectConfig;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Created by Nullptr
 * @date 2018/7/26 9:28
 * @describe 用ContentProvider获取图库图片的Dao层
 */
public class GalleryDao {

    private SelectConfig mConfig;               // 配置信息
    private ContentResolver mResolver;          // 传入的ContentResolver
    private static final String SINGLE_SELECTION = Media.MIME_TYPE + " = ?";    //单个图片类型的选项

    public GalleryDao(ContentResolver resolver) {
        mConfig = SelectConfig.getInstance();
        mResolver = resolver;
    }

    /**
     * 获取图库的Cursor
     * @return 查询图库获得的cursor
     */
    public Cursor getGalleryCursor() {
        Uri imageUri = Media.EXTERNAL_CONTENT_URI;
        List<String> args = new ArrayList<>();
        for (ImageType imageType : mConfig.imageTypes) {
            args.add(imageType.getTypeName());
        }
        String[] argArr = new String[mConfig.imageTypes.size()];
        args.toArray(argArr);
        return mResolver.query(imageUri, null,
                getSelection(), argArr, DATE_ADDED);
    }

    /**
     * 根据图像类型个数返回对应的字符串
     * @return 处理后的字符串
     */
    private String getSelection() {
        int size = mConfig.imageTypes.size();
        if (size == 1) {
            // 单个图片类型选项
            return SINGLE_SELECTION;
        } else {
            // 多个图片类型，拼接选项字符串
            StringBuilder selection = new StringBuilder();
            for (int i = 0; i < size - 1; i++) {
                selection.append(SINGLE_SELECTION);
                selection.append(" or ");
            }
            selection.append(SINGLE_SELECTION);
            return selection.toString();
        }
    }
}
