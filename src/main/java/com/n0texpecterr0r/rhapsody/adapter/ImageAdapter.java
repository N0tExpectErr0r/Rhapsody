package com.n0texpecterr0r.rhapsody.adapter;

import android.util.Log;
import android.widget.ImageView;
import com.n0texpecterr0r.rhapsody.R;
import com.n0texpecterr0r.rhapsody.SelectConfig;
import com.n0texpecterr0r.rhapsody.adapter.base.BaseAdapter;
import com.n0texpecterr0r.rhapsody.adapter.base.CommonViewHolder;
import java.util.List;

/**
 * @author Created by Nullptr
 * @date 2018/7/31 13:30
 * @describe 预览界面底部栏Adapter
 */
public class ImageAdapter extends BaseAdapter<String> {

    public ImageAdapter(List<String> data, int itemLayoutId) {
        super(data, itemLayoutId);
    }

    @Override
    public void initItemView(CommonViewHolder holder, String path) {
        Log.d("initItemView: ",path);
        ImageView ivImage = holder.getView(R.id.preview_item_iv_image);
        ivImage.setImageResource(R.drawable.mock);
        SelectConfig.getInstance().mEngine
                .loadThumbnail(null, 200, ivImage, path);
    }
}
