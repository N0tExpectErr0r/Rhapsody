package com.n0texpecterr0r.rhapsody.adapter;

import static android.graphics.Color.argb;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Created by Nullptr
 * @date 2018/7/26 15:32
 * @describe 图片列表的Adapter
 */
public class ImageAdapter extends RecyclerView.Adapter<ImageViewHolder> implements OnClickListener {

    private List<String> mPaths;        // 图片的路径集合
    private boolean[] mCheckStatus;     // 对应checkbox选择状态
    private int mCheckCount;            // 选择个数
    private ImageEngine mImageEngine;   // 加载图片的引擎
    private int mMaxCheckCount;         // 最大选择个数
    private Context mContext;           // 上下文
    private float mScaleValue;          // 缩略图缩放比例

    public ImageAdapter(List<String> paths, Context context) {
        mPaths = paths;
        mImageEngine = SelectConfig.getInstance().mEngine;
        mScaleValue = SelectConfig.getInstance().thumbnailScale;
        mMaxCheckCount = SelectConfig.getInstance().maxSelectCount;
        mCheckStatus = new boolean[mPaths.size()];
        mContext = context;
    }

    /**
     * 更改当前的路径
     * @param paths 路径集合
     */
    public void setPaths(List<String> paths) {
        mPaths = paths;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_gallery, parent, false);
        view.setOnClickListener(this);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        holder.mCheckBox.setChecked(mCheckStatus[position]);
        holder.mImageView.setImageResource(R.drawable.mock);
        if (holder.mCheckBox.isChecked())
            holder.mImageView.setColorFilter(argb(70,0,0,0));
        else
            holder.mImageView.setColorFilter(null);
        // 计算缩放后的尺寸
        float size;
        int screenWidth = mContext.getResources().getDisplayMetrics().widthPixels;
        size = screenWidth * mScaleValue / 4;   // 计算缩略图的大小
        String path = mPaths.get(position);
        mImageEngine.loadThumbnail(mContext, (int) size, holder.mImageView, path);
        holder.itemView.setTag(position);
    }

    @Override
    public int getItemCount() {
        return mPaths.size();
    }

    @Override
    public void onClick(View v) {
        int position = (int) v.getTag();
        CheckBox checkBox = v.findViewById(R.id.item_cb_select);
        ImageView imageView = v.findViewById(R.id.item_iv_image);
        if (mCheckStatus[position]){
            imageView.setColorFilter(null);
            mCheckStatus[position] = false;
            mCheckCount--;
            imageView.setColorFilter(null);
            checkBox.setChecked(false);
            return;
        }
        if (mCheckCount == mMaxCheckCount){
            Toast.makeText(mContext,"最多只能选择"+mMaxCheckCount+"张",Toast.LENGTH_SHORT).show();
        }else{
            imageView.setColorFilter(argb(70,0,0,0));
            mCheckStatus[position] = true;
            mCheckCount++;
            checkBox.setChecked(true);
        }
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
