package com.n0texpecterr0r.rhapsody.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CheckBox;
import com.n0texpecterr0r.rhapsody.Constants;
import com.n0texpecterr0r.rhapsody.R;
import com.n0texpecterr0r.rhapsody.adapter.ImageAdapter;
import com.n0texpecterr0r.rhapsody.adapter.ScaleImageAdapter;
import com.n0texpecterr0r.rhapsody.adapter.base.BaseAdapter.OnItemClickListener;
import com.n0texpecterr0r.rhapsody.view.base.BaseActivity;
import java.util.ArrayList;

public class PreviewActivity extends BaseActivity implements CheckBox.OnClickListener, OnItemClickListener,
        ViewPager.OnPageChangeListener {

    private ArrayList<String> mPaths;               // 传入的地址List
    private ArrayList<String> mCheckedPaths;        // 选中的List
    private int currentIndex;                       // 当前index
    private ViewPager mVpImageArea;                 // 显示图片的ViewPager
    private RecyclerView mRvImageList;              // 显示图片列表的RecyclerView
    private Toolbar mToolbar;                       // ToolBar
    private CheckBox mCbSelect;                     // 显示当前是否被选中的CheckBox
    private ImageAdapter mImageAdapter;             // RecyclerView的Adapter
    private ScaleImageAdapter mScaleImageAdapter;   // ViewPager的Adapter

    public static void actionStart(Context context, ArrayList<String> paths) {
        Intent intent = new Intent(context, PreviewActivity.class);
        intent.putStringArrayListExtra("paths", paths);
        context.startActivity(intent);
    }

    @Override
    protected void initVariables() {
        // 获取数据
        Intent intent = getIntent();
        mPaths = intent.getStringArrayListExtra("paths");
        mCheckedPaths = new ArrayList<>(mPaths);
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setContentView(R.layout.activity_preview);

        // 初始化底部图片栏
        mImageAdapter = new ImageAdapter(mPaths, R.layout.item_image);
        mRvImageList = findViewById(R.id.preview_rv_bottom);
        mRvImageList.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false));
        mRvImageList.setAdapter(mImageAdapter);
        mImageAdapter.setOnItemClickListener(this);

        // 初始化ViewPager
        mVpImageArea = findViewById(R.id.preview_vp_image_area);
        mScaleImageAdapter = new ScaleImageAdapter(this);
        mScaleImageAdapter.setDatas(mPaths);
        mVpImageArea.setAdapter(mScaleImageAdapter);
        mVpImageArea.addOnPageChangeListener(this);

        // 初始化Toolbar
        mToolbar = findViewById(R.id.preview_toolbar);
        setSupportActionBar(mToolbar);

        // 初始化Checkbox
        mCbSelect = findViewById(R.id.preview_cb_select);
        mCbSelect.setOnClickListener(this);
    }

    @Override
    protected void loadData() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        setCurrent(0);
    }

    /**
     * 设置当前index
     *
     * @param index 当前index
     */
    private void setCurrent(int index) {
        currentIndex = index;
        mToolbar.setTitle((currentIndex + 1) + "/" + mPaths.size());
        mCbSelect.setChecked(checkChecked(index));
        mImageAdapter.setCurrentIndex(currentIndex);
    }

    /**
     * 检查index是否是被选中的
     *
     * @param index 对应index
     * @return 是否被选中
     */
    private boolean checkChecked(int index) {
        return mCheckedPaths.contains(mPaths.get(index));
    }

    @Override
    public void onClick(View v) {
        if (checkChecked(currentIndex)) {
            mCheckedPaths.remove(mPaths.get(currentIndex));
            mImageAdapter.addDeleteIndex(currentIndex);
        } else {
            mCheckedPaths.add(mPaths.get(currentIndex));
            mImageAdapter.removeDeleteIndex(currentIndex);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 关闭界面，通知主界面更新状态
        Intent intent = new Intent();
        intent.setAction(Constants.SELECT_CHANGE);
        intent.putStringArrayListExtra("select_paths", mCheckedPaths);
        sendBroadcast(intent);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        setCurrent(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    // 底部RecyclerView的item点击事件
    @Override
    public void onItemClick(View view, int position) {
        setCurrent(position);
        mVpImageArea.setCurrentItem(position);
    }
}
