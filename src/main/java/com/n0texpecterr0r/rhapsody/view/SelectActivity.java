package com.n0texpecterr0r.rhapsody.view;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.n0texpecterr0r.rhapsody.R;
import com.n0texpecterr0r.rhapsody.SelectConfig;
import com.n0texpecterr0r.rhapsody.adapter.ImageAdapter;
import com.n0texpecterr0r.rhapsody.bean.Floder;
import com.n0texpecterr0r.rhapsody.model.SelectModel;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Created by Nullptr
 * @date 2018/7/25 10:07
 * @describe 选择图片界面
 */
public class SelectActivity extends AppCompatActivity implements SelectView {

    private static final int PERMISSIONS_READ_STORAGE = 1;
    private List<Floder> mFloders;
    private List<String> mImagePaths;
    private ProgressBar mPbLoading;
    private SelectModel mModel;
    private RecyclerView mRvGallery;
    private ImageAdapter mAdapter;
    private MenuItem mItemConfirm;

    private BroadcastReceiver mBroadcastReceiver;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_select,menu);
        mItemConfirm = menu.findItem(R.id.confirm);
        mItemConfirm.setVisible(false);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkStoragePermission();
        setContentView(R.layout.activity_select);
        // 初始化RecyclerView
        mRvGallery = findViewById(R.id.select_rv_gallery);
        mRvGallery.setLayoutManager(new GridLayoutManager(this,4));
        // 初始化Toolbar
        Toolbar toolbar = findViewById(R.id.select_toolbar);
        setSupportActionBar(toolbar);

        mPbLoading = findViewById(R.id.select_pb_loading);
        mModel = new SelectModel(getContentResolver(),this);
        mModel.getFloderList();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mBroadcastReceiver = new GalleryReceiver();
        IntentFilter intentFilter = new IntentFilter();
        //设置接收广播的类型
        intentFilter.addAction("select_image");
        registerReceiver(mBroadcastReceiver,intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mBroadcastReceiver);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList("paths",(ArrayList<String>) mImagePaths);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mImagePaths = savedInstanceState.getStringArrayList("paths");
        mAdapter.setPaths(mImagePaths);
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
                mPbLoading.setVisibility(View.GONE);
            }
        });
    }

    class GalleryReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getStringExtra("type")){
                case "select_change":
                    int selectNum = intent.getIntExtra("select_num",0);
                    if (selectNum == 0){
                        mItemConfirm.setVisible(false);
                    }else {
                        mItemConfirm.setVisible(true);
                        mItemConfirm.setTitle("确定(" +
                                selectNum + "/" + SelectConfig.getInstance().maxSelectCount + ")");
                    }
                    break;
            }
        }
    }

    /**
     * android6.0动态权限申请：SD卡读写权限
     */
    public void checkStoragePermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            int checkResult = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (checkResult != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PERMISSIONS_READ_STORAGE);
            }
        }
    }

    /**
     * 权限申请回调
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        boolean permit = grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED;
        switch (requestCode) {
            case PERMISSIONS_READ_STORAGE:
                if (!permit) {
                    Toast.makeText(this,"需要读取权限才可正常使用",Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
        }
    }
}
