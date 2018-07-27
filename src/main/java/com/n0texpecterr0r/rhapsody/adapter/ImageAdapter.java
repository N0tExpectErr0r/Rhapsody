package com.n0texpecterr0r.rhapsody.adapter;

import static android.graphics.Color.argb;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Toast;
import com.n0texpecterr0r.rhapsody.R;
import com.n0texpecterr0r.rhapsody.SelectConfig;
import com.n0texpecterr0r.rhapsody.adapter.ImageAdapter.ImageViewHolder;
import com.n0texpecterr0r.rhapsody.engine.ImageEngine;
import java.util.List;

/**
 * @author Created by Nullptr
 * @date 2018/7/26 15:32
 * @describe 图片列表的Adapter
 */
public class ImageAdapter extends RecyclerView.Adapter<ImageViewHolder> implements OnClickListener {

    private List<String> mPaths;                // 图片的路径集合
    private SparseBooleanArray mCheckStatus;    // 存储勾选框状态的map集合
    private int mCheckCount;                    // 选择个数
    private ImageEngine mImageEngine;           // 加载图片的引擎
    private int mMaxCheckCount;                 // 最大选择个数
    private Context mContext;                   // 上下文
    private float mScaleValue;                  // 缩略图缩放比例

    public ImageAdapter(List<String> paths, Context context) {
        mPaths = paths;
        mImageEngine = SelectConfig.getInstance().mEngine;
        mScaleValue = SelectConfig.getInstance().thumbnailScale;
        mMaxCheckCount = SelectConfig.getInstance().maxSelectCount;

        mCheckStatus = new SparseBooleanArray();
        for (int i = 0; i < mPaths.size(); i++) {
            mCheckStatus.put(i, false);
        }

        mContext = context;
    }

    /**
     * 更改当前的路径
     *
     * @param paths 路径集合
     */
    public void setPaths(List<String> paths) {
        mPaths = paths;
        notifyDataSetChanged();

        // 重新初始化状态
        mCheckStatus = new SparseBooleanArray();
        for (int i = 0; i < mPaths.size(); i++) {
            mCheckStatus.put(i, false);
        }
        mCheckCount = 0;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_gallery, parent, false);
        view.setOnClickListener(this);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ImageViewHolder holder, final int position) {
        final CheckBox checkBox = holder.mCheckBox;
        final ImageView imageView = holder.mImageView;

        imageView.setImageResource(R.drawable.mock);

        holder.itemView.setTag(position);

        // 计算缩放后的尺寸
        int screenWidth = mContext.getResources().getDisplayMetrics().widthPixels;
        float size = screenWidth * mScaleValue / 4;   // 计算缩略图的大小
        String path = mPaths.get(position);
        mImageEngine.loadThumbnail(mContext, (int) size, holder.mImageView, path);

        // 如果还没有添加过点击事件
        checkBox.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View buttonView) {

                boolean isChecked = checkBox.isChecked();
                if (mCheckCount != mMaxCheckCount || !isChecked) {
                    // 不是添加或者没有超过最大选择数时
                    mCheckStatus.put(position, isChecked);
                    if (isChecked) {
                        // 选择，添加灰色蒙版，并且添加checkCount
                        mCheckCount++;
                        imageView.setColorFilter(argb(70, 0, 0, 0));
                    } else {
                        // 取消选择，去除蒙版，并且减少checkCount
                        mCheckCount--;
                        imageView.setColorFilter(null);
                    }
                } else {
                    // 不能选择
                    checkBox.setChecked(false);
                    Toast.makeText(mContext, "最多只能选择" + mMaxCheckCount + "张图片",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        checkBox.setChecked(mCheckStatus.get(position));
        if (checkBox.isChecked()) {
            imageView.setColorFilter(argb(70, 0, 0, 0));
        } else {
            imageView.setColorFilter(null);
        }
    }

    @Override
    public int getItemCount() {
        return mPaths.size();
    }

    @Override
    public void onClick(View v) {
        // 图片部分的点击事件
        int position = (int) v.getTag();
    }

    class ImageViewHolder extends ViewHolder {

        private CheckBox mCheckBox;
        private ImageView mImageView;

        public ImageViewHolder(View itemView) {
            super(itemView);
            mCheckBox = itemView.findViewById(R.id.item_cb_select);
            mImageView = itemView.findViewById(R.id.item_iv_image);
        }
    }

}
