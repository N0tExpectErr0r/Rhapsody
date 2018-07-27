package com.n0texpecterr0r.rhapsody;

import static com.n0texpecterr0r.rhapsody.bean.ImageType.JPEG;
import static com.n0texpecterr0r.rhapsody.bean.ImageType.PNG;

import com.n0texpecterr0r.rhapsody.bean.ImageType;
import com.n0texpecterr0r.rhapsody.engine.ImageEngine;
import com.n0texpecterr0r.rhapsody.engine.impl.RhaposdyEngine;
import java.util.Set;

/**
 * @author Created by Nullptr
 * @date 2018/7/25 10:25
 * @describe 包含选择图片的配置参数
 */
public final class SelectConfig {

    public Set<ImageType> imageTypes;               // 图片类型 默认全部
    public int maxSelectCount;                      // 最大选择图片 默认1
    public float thumbnailScale;                    // 缩略图缩放比例 默认0.5F
    public ImageEngine mEngine;                     // 图片加载引擎 默认为Rhapsody自带引擎
    private volatile static SelectConfig sInstance; // 实例

    /**
     * 防止外部调用构造方法
     */
    private SelectConfig() {
    }

    /**
     * 懒汉式单例获取实例
     *
     * @return 实例对象
     */
    public static SelectConfig getInstance() {
        if (sInstance == null) {
            synchronized (SelectConfig.class) {
                if (sInstance == null) {
                    sInstance = new SelectConfig();
                }
            }
        }
        return sInstance;
    }

    /**
     * 获取初始化过的实例
     *
     * @return 初始化过的实例
     */
    public static SelectConfig getResetInstance() {
        SelectConfig config = getInstance();
        config.reset();
        return config;
    }

    /**
     * 重置配置
     */
    private void reset() {
        imageTypes = ImageType.of(JPEG, PNG);
        maxSelectCount = 1;
        thumbnailScale = 0.5F;
        mEngine = new RhaposdyEngine();
    }
}
