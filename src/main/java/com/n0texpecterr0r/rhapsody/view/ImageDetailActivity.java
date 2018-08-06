package com.n0texpecterr0r.rhapsody.view;

import static com.n0texpecterr0r.rhapsody.Constants.REQUEST_PREVIEW_CODE;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;
import com.n0texpecterr0r.rhapsody.Constants;
import com.n0texpecterr0r.rhapsody.R;
import com.n0texpecterr0r.rhapsody.SelectConfig;
import com.n0texpecterr0r.rhapsody.adapter.ImageAdapter;
import com.n0texpecterr0r.rhapsody.adapter.ScaleImageAdapter;
import com.n0texpecterr0r.rhapsody.adapter.base.BaseAdapter.OnItemClickListener;
import com.n0texpecterr0r.rhapsody.util.ToastUtil;
import com.n0texpecterr0r.rhapsody.view.base.BaseActivity;
import java.util.ArrayList;

/**
 * @author Created by Nullptr
 * @date 2018/8/1 10:06
 * @describe 图片详情界面
 */
public class ImageDetailActivity extends BaseActivity implements OnItemClickListener,
        OnPageChangeListener, CheckBox.OnClickListener {


    private static ArrayList<String> mPaths;        // 所有图片的Paths
    private static ArrayList<String> mCheckedPaths; // 被选中的Paths
    private static int mCurrentIndex;               // 当前Index
    private ViewPager mVpImageArea;                 // 显示图片的ViewPager
    private RecyclerView mRvImageList;              // 显示图片列表的RecyclerView
    private Toolbar mToolbar;                       // ToolBar
    private CheckBox mCbSelect;                     // 显示当前是否被选中的CheckBox
    private ImageAdapter mImageAdapter;             // RecyclerView的Adapter
    private ScaleImageAdapter mScaleImageAdapter;   // ViewPager的Adapter
    private MenuItem mItemConfirm;                  // 确认选择Item

    public static void actionStart(Activity context, int currentIndex, ArrayList<String> paths,
            ArrayList<String> checkedPaths) {
        Intent intent = new Intent(context, ImageDetailActivity.class);
        intent.putExtra("index", currentIndex);
        intent.putStringArrayListExtra("paths", paths);
        intent.putStringArrayListExtra("checked_paths", checkedPaths);
        context.startActivityForResult(intent,REQUEST_PREVIEW_CODE);
    }

    @Override
    protected void initVariables() {
        // 获取数据
        Intent intent = getIntent();
        mPaths = intent.getStringArrayListExtra("paths");
        mCheckedPaths = intent.getStringArrayListExtra("checked_paths");
        mCurrentIndex = intent.getIntExtra("index", 0);
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setContentView(R.layout.activity_preview);

        // 初始化底部图片栏
        mImageAdapter = new ImageAdapter(mCheckedPaths, R.layout.item_image);
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
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);

        // 初始化Checkbox
        mCbSelect = findViewById(R.id.preview_cb_select);
        mCbSelect.setOnClickListener(this);
    }

    @Override
    protected void loadData() {
        super.onResume();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setCurrent(mCurrentIndex);
        mVpImageArea.setCurrentItem(mCurrentIndex);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_select, menu);
        mItemConfirm = menu.findItem(R.id.confirm);
        if (mCheckedPaths.size()>0) {
            // 如果已经有选择
            mItemConfirm.setTitle("确定(" + mCheckedPaths.size() + "/" +
                    SelectConfig.getInstance().maxSelectCount + ")");
        }else{
            // 如果没有选择
            mItemConfirm.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else if (item.getItemId() == R.id.confirm) {
            confirmSelect();
        }
        return true;
    }

    /**
     * 确认选择，返回数据
     */
    private void confirmSelect() {
        setResult(RESULT_OK);
        finish();
    }

    /**
     * 设置当前index
     *
     * @param index 当前index
     */
    private void setCurrent(int index) {
        mCurrentIndex = index;
        mToolbar.setTitle((mCurrentIndex + 1) + "/" + mPaths.size());
        mCbSelect.setChecked(checkChecked(index));
        int checkedIndex = mCheckedPaths.indexOf(mPaths.get(index));
        mImageAdapter.setCurrentIndex(checkedIndex);
        mRvImageList.scrollToPosition(checkedIndex);
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
        if (checkChecked(mCurrentIndex)) {
            mCheckedPaths.remove(mPaths.get(mCurrentIndex));
            setCurrent(mCurrentIndex);  // 刷新RecyclerView
        } else {
            int maxCount = SelectConfig.getInstance().maxSelectCount;
            if (mCheckedPaths.size()< maxCount) {
                // 如果没有超过最大个数
                mCheckedPaths.add(mPaths.get(mCurrentIndex));
                setCurrent(mCurrentIndex);  // 刷新RecyclerView
            }else{
                // 已经超过最大选择个数
                mCbSelect.setChecked(false);
                ToastUtil.show(this, "最多只能选择" + maxCount + "张图片");
            }
        }
        if (mCheckedPaths.size()>0){
            mItemConfirm.setVisible(true);
            mItemConfirm.setTitle("确定(" + mCheckedPaths.size() + "/" +
                    SelectConfig.getInstance().maxSelectCount + ")");
        }else{
            mItemConfirm.setVisible(false);
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

    @Override
    public void onItemClick(View view, int position) {
        int index = 0;
        if (mPaths.contains(mCheckedPaths.get(position))) {
            index = mPaths.indexOf(mCheckedPaths.get(position));    // 计算在总的Index中的index
        }else{
            ToastUtil.show(this,"点击的图片不在当前文件夹");
        }
        setCurrent(index);
        mVpImageArea.setCurrentItem(index);
    }

}
