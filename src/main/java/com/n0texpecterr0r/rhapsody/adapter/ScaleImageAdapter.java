package com.n0texpecterr0r.rhapsody.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import com.n0texpecterr0r.rhapsody.SelectConfig;
import com.n0texpecterr0r.rhapsody.view.ui.ScaleImageView;
import java.util.List;

/**
 * @author Created by Nullptr
 * @date 2018/7/31 14:03
 * @describe 含有ScaleImageView的ViewPager的Adapter
 */
public class ScaleImageAdapter extends PagerAdapter {

    private List<String> mPaths;
    private Context mContext;

    public ScaleImageAdapter(Context context) {
        mContext = context.getApplicationContext();
    }

    public void setDatas(List<String> paths) {
        mPaths = paths;
    }

    @Override
    public int getCount() {
        return mPaths.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object obj) {
        return view == obj;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ScaleImageView imageView = new ScaleImageView(mContext);
        SelectConfig.getInstance().mEngine.loadImage(mContext,1080,0,imageView,mPaths.get(position));

        container.addView(imageView, 0);
        return imageView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
