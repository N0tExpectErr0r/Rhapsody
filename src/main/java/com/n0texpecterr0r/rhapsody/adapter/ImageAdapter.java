package com.n0texpecterr0r.rhapsody.adapter;

import static android.graphics.Color.argb;

import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.Log;
import android.widget.ImageView;
import com.n0texpecterr0r.rhapsody.R;
import com.n0texpecterr0r.rhapsody.SelectConfig;
import com.n0texpecterr0r.rhapsody.adapter.base.BaseAdapter;
import com.n0texpecterr0r.rhapsody.adapter.base.CommonViewHolder;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Created by Nullptr
 * @date 2018/7/31 13:30
 * @describe 预览界面底部栏Adapter
 */
public class ImageAdapter extends BaseAdapter<String> {
    private List<Integer> mDeletedIndexs;

    public ImageAdapter(List<String> data, int itemLayoutId) {
        super(data, itemLayoutId);
        mDeletedIndexs = new ArrayList<>();
    }

    @Override
    public void initItemView(CommonViewHolder holder, String path) {
        ImageView ivImage = holder.getView(R.id.preview_item_iv_image);
        ivImage.setImageResource(R.drawable.mock);
        SelectConfig.getInstance().mEngine
                .loadThumbnail(null, 200, ivImage, path);
    }

    public void addDeleteIndex(Integer index){
        mDeletedIndexs.add(index);
        notifyDataSetChanged();
    }

    public void removeDeleteIndex(Integer index){
        mDeletedIndexs.remove(index);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        super.onBindViewHolder(viewHolder, position);
        ImageView imageView = ((CommonViewHolder)viewHolder).getView(R.id.preview_item_iv_image);
        if (mDeletedIndexs.contains(position)){
            imageView.setColorFilter(argb(75, 255, 0, 0));
        }else{
            imageView.setColorFilter(null);
        }
    }
}
