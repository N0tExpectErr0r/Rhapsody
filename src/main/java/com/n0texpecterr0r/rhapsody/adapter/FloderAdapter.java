package com.n0texpecterr0r.rhapsody.adapter;

import android.widget.ImageView;
import android.widget.TextView;
import com.n0texpecterr0r.rhapsody.R;
import com.n0texpecterr0r.rhapsody.SelectConfig;
import com.n0texpecterr0r.rhapsody.adapter.base.BaseAdapter;
import com.n0texpecterr0r.rhapsody.adapter.base.CommonViewHolder;
import com.n0texpecterr0r.rhapsody.bean.Floder;
import java.util.List;

/**
 * @author Created by Nullptr
 * @date 2018/7/27 20:43
 * @describe 文件夹Adapter
 */
public class FloderAdapter extends BaseAdapter<Floder> {

    public FloderAdapter(List<Floder> data, int itemLayoutId) {
        super(data, itemLayoutId);
    }

    @Override
    public void initItemView(CommonViewHolder holder, Floder floder) {
        TextView tvName = holder.getView(R.id.floder_tv_name);
        TextView tvCount = holder.getView(R.id.floder_tv_count);
        ImageView ivCover = holder.getView(R.id.floder_iv_cover);

        tvName.setText(floder.getName());
        tvCount.setText(floder.getPictureCount() + "张图片");
        ivCover.setImageResource(R.drawable.mock);
        SelectConfig.getInstance().mEngine
                .loadThumbnail(null, 200, ivCover, floder.getCoverPath());
    }
}
