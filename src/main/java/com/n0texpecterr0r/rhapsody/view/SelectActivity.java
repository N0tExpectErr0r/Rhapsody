package com.n0texpecterr0r.rhapsody.view;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.Adapter;
import android.widget.ImageView;
import com.n0texpecterr0r.rhapsody.R;
import com.n0texpecterr0r.rhapsody.SelectConfig;
import com.n0texpecterr0r.rhapsody.adapter.ImageAdapter;
import com.n0texpecterr0r.rhapsody.bean.Floder;
import com.n0texpecterr0r.rhapsody.loader.RhapsodyLoader;
import com.n0texpecterr0r.rhapsody.model.SelectModel;
import java.util.List;

/**
 * @author Created by Nullptr
 * @date 2018/7/25 10:07
 * @describe 选择图片界面
 */
public class SelectActivity extends AppCompatActivity implements SelectView {
    private List<Floder> mFloders;
    private List<String> mImagePaths;
    private SelectModel mModel;
    private RecyclerView mRvGallery;
    private ImageAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);
        mModel = new SelectModel(getContentResolver(),this);
        mModel.getFloderList();
        mRvGallery = findViewById(R.id.select_rv_gallery);
        mRvGallery.setLayoutManager(new GridLayoutManager(this,4));
        Toolbar toolbar = findViewById(R.id.select_toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public void onFloder(List<Floder> floders) {
        mFloders = floders;
        mImagePaths = mModel.getImageFromFloderList(floders);
        mAdapter = new ImageAdapter(mImagePaths,this);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mRvGallery.setAdapter(mAdapter);
            }
        });
    }
}