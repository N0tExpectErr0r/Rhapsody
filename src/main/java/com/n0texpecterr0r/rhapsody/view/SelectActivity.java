package com.n0texpecterr0r.rhapsody.view;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.n0texpecterr0r.rhapsody.R;
import com.n0texpecterr0r.rhapsody.SelectConfig;
import com.n0texpecterr0r.rhapsody.adapter.ImageAdapter;
import com.n0texpecterr0r.rhapsody.adapter.base.BaseAdapter.OnItemClickListener;
import com.n0texpecterr0r.rhapsody.bean.Floder;
import com.n0texpecterr0r.rhapsody.model.SelectModel;
import com.n0texpecterr0r.rhapsody.permission.PermissionActivity;
import com.n0texpecterr0r.rhapsody.permission.PermissionChecker;
import com.n0texpecterr0r.rhapsody.view.ui.FloderPopWindow;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Created by Nullptr
 * @date 2018/7/25 10:07
 * @describe 选择图片界面
 */
public class SelectActivity extends AppCompatActivity implements SelectView {

    private static final int REQUEST_PERMISSION_CODE = 0;  // 请求权限Code
    private List<Floder> mFloders;          // Floder列表
    private ProgressBar mPbLoading;         // 加载进度View
    private SelectModel mModel;             // 加载数据Model层
    private RecyclerView mRvGallery;        // Gallery展示View
    private ImageAdapter mAdapter;          // GalleryAdapter
    private MenuItem mItemConfirm;          // 右上角确定按钮
    private TextView mTvFloderName;         // 左下角文件夹名
    private FloderPopWindow mFloderWindow;  // 弹出文件夹菜单
    private Handler mUIHandler;             // 主线程调度Handler


    private BroadcastReceiver mBroadcastReceiver;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_select, menu);
        mItemConfirm = menu.findItem(R.id.confirm);
        mItemConfirm.setVisible(false);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);

        // 初始化RecyclerView
        mRvGallery = findViewById(R.id.select_rv_gallery);
        mRvGallery.setLayoutManager(new GridLayoutManager(this, 4));
        mAdapter = new ImageAdapter(new ArrayList<String>(), this);
        mRvGallery.setAdapter(mAdapter);
        // 初始化Toolbar
        Toolbar toolbar = findViewById(R.id.select_toolbar);
        setSupportActionBar(toolbar);

        // 初始化选择文件夹列表
        mTvFloderName = findViewById(R.id.select_tv_floder_name);
        mFloderWindow = new FloderPopWindow(SelectActivity.this);

        mPbLoading = findViewById(R.id.select_pb_loading);
        mModel = new SelectModel(getContentResolver(), this);
        
        // 缺少权限时, 进入权限配置页面
        PermissionChecker checker = new PermissionChecker(this);
        if (checker.lackPermissions(READ_EXTERNAL_STORAGE)) {
            PermissionActivity.startActivityForResult(this,
                    REQUEST_PERMISSION_CODE, READ_EXTERNAL_STORAGE);
        }else{
            mModel.getFloderList();
        }

        // UI线程调度Handler
        mUIHandler = new Handler(Looper.getMainLooper());
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case REQUEST_PERMISSION_CODE:
                // 请求权限回调
                if (resultCode == PermissionActivity.PERMISSIONS_DENIED) {
                    // 拒绝时, 关闭页面
                    finish();
                }else{
                    // 接收了，加载数据
                    mModel.getFloderList();
                }
                break;
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        mBroadcastReceiver = new GalleryReceiver();
        IntentFilter intentFilter = new IntentFilter();
        // 设置接收广播的类型
        intentFilter.addAction("select_image");
        // 注册广播
        registerReceiver(mBroadcastReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 注销广播
        unregisterReceiver(mBroadcastReceiver);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // 如果被意外销毁，重新加载数据
        mAdapter.setPaths(new ArrayList<String>());
        mModel.getFloderList();
    }

    /**
     * 获取到文件夹列表的回调
     * @param floders 获取到的文件夹列表
     */
    @Override
    public void onFloder(final List<Floder> floders) {
        // 给当前存放的文件夹列表赋值
        mFloders = floders;
        // 获取图片列表
        mModel.getImageFromFloderList(floders);

        // 回到主线程做UI操作
        mUIHandler.post(new Runnable() {
            @Override
            public void run() {
                // 初始化文件夹列表PopupWindow
                mFloderWindow.setDatas(floders);
                mFloderWindow.setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        // 将对应文件夹所有图片应用于图库
                        mModel.getImageFromFloderSync(mFloders.get(position));
                        mTvFloderName.setText(mFloders.get(position).getName());
                    }
                });
                // 初始化文件夹名Spinner
                mTvFloderName.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 展示PopupWindow
                        mFloderWindow.showUp(mTvFloderName);
                    }
                });
            }
        });
    }

    /**
     * 获取到图片列表的回调
     * @param imagePaths 图片路径列表
     */
    @Override
    public void onImages(final List<String> imagePaths) {
        mUIHandler.post(new Runnable() {
            @Override
            public void run() {
                mAdapter.setPaths(imagePaths);
                mPbLoading.setVisibility(View.GONE);
            }
        });
    }

    /**
     * 广播接收器，监听选择改变广播并更新UI
     */
    class GalleryReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getStringExtra("type")) {
                case "select_change":
                    int selectNum = intent.getIntExtra("select_num", 0);
                    if (selectNum == 0) {
                        mItemConfirm.setVisible(false);
                    } else {
                        mItemConfirm.setVisible(true);
                        mItemConfirm.setTitle("确定(" + selectNum + "/" +
                                SelectConfig.getInstance().maxSelectCount + ")");
                    }
                    break;
            }
        }
    }

}
