package com.n0texpecterr0r.rhapsody.bean;

import android.support.v4.util.ArraySet;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;

/**
 * @author Created by Nullptr
 * @date 2018/7/25 10:07
 * @describe 选择的图片的类型枚举
 */
public enum ImageType {
    JPEG("image/jpeg", arraySetOf("jpg", "jpeg")),
    PNG("image/png",arraySetOf("png")),
    GIF("image/gif",arraySetOf("gif"));

    //类型名
    private final String mTypeName;
    //类型包含的后缀名
    private final Set<String> mExtensions;

    /**
     * 构造方法
     * @param mimeTypeName mimeType的name
     * @param extensions 包括的后缀名
     */
    ImageType(String mimeTypeName, Set<String> extensions) {
        this.mTypeName = mimeTypeName;
        this.mExtensions = extensions;
    }

    /**
     * 构造包括后缀名Set的方法
     * @param extensions 后缀名
     * @return 后缀名的Set
     */
    private static Set<String> arraySetOf(String... extensions) {
        return new ArraySet(Arrays.asList(extensions));
    }

    /**
     * 构造多个ImageType的Set的方法
     * @param type ImageType
     * @param rest 剩余的ImageType
     * @return 构造好的Set
     */
    public static Set<ImageType> of(ImageType type, ImageType... rest) {
        return EnumSet.of(type, rest);
    }

    public String getTypeName() {
        return mTypeName;
    }

    public Set<String> getExtensions() {
        return mExtensions;
    }
}
