package com.n0texpecterr0r.rhapsody;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import com.n0texpecterr0r.rhapsody.engine.impl.RhaposdyEngine;
import android.widget.ImageView;
import com.n0texpecterr0r.rhapsody.bean.Floder;
import com.n0texpecterr0r.rhapsody.loader.RhapsodyLoader;
import java.util.List;

/**
 * @author Created by Nullptr
 * @date 2018/7/25 10:07
 * @describe 选择图片界面
 */
public class SelectActivity extends AppCompatActivity implements SelectView{
    private SelectConfig mConfig;
    private List<Floder> mFloders;
    private ImageView mImageView;
    private SelectModel mModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);
        mImageView = findViewById(R.id.iv_image);
        mModel = new SelectModel(getContentResolver(),this);
        mModel.getFloderList();
        mConfig = SelectConfig.getInstance();

    }

    @Override
    public void onFloder(List<Floder> floders) {
        mFloders = floders;
        String picPath = mModel.getImageFromFloder(floders.get(0)).get(0);
        RhapsodyLoader.get().load(floders.get(0).getDir()+"/"+picPath).into(mImageView);
    }
}
