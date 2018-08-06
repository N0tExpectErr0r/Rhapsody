package com.n0texpecterr0r.rhapsody.engine.impl;

import android.content.Context;
import android.view.MenuItem;
import android.widget.ImageView;
import com.n0texpecterr0r.rhapsody.engine.ImageEngine;
import com.n0texpecterr0r.rhapsody.loader.RhapsodyLoader;

/**
 * @author Created by Nullptr
 * @date 2018/7/25 20:28
 * @describe Rhaposdy库自带图片加载引擎。
 */
public class RhaposdyEngine implements ImageEngine {

    @Override
    public void loadThumbnail(Context context, int size, ImageView imageView, String path) {
        RhapsodyLoader.get().load(path).setSize(size,size).into(imageView);
    }

    @Override
    public void loadImage(Context context, int width, int height, ImageView imageView, String path) {
        RhapsodyLoader.get().load(path).setSize(width,height).useCache(false).into(imageView);
    }
}
