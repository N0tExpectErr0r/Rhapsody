package com.n0texpecterr0r.rhapsody;

import android.app.Activity;
import android.support.v4.app.Fragment;
import java.lang.ref.WeakReference;
import java.util.Set;

/**
 * @author Created by Nullptr
 * @date 2018/7/25 9:49
 * @describe 图片选择器的入口
 */
public class ImageSelector {

    //持有Activity与Fragment的弱引用
    private final WeakReference<Activity> mActivity;
    private final WeakReference<Fragment> mFragment;

    /**
     * 调用from(Activity)时的构造函数
     * @param activity 传入的Activity
     */
    private ImageSelector(Activity activity) {
        this(activity, (Fragment)null);

    }

    /**
     * 调用from(Fragment)时的构造函数
     * @param fragment 传入的Fragment
     */
    private ImageSelector(Fragment fragment) {
        this(fragment.getActivity(),fragment);
    }

    /**
     * 初始化Activity与Fragment的弱引用
     * @param activity Activity
     * @param fragment Fragment
     */
    private ImageSelector(Activity activity, Fragment fragment) {
        mActivity = new WeakReference<>(activity);
        mFragment = new WeakReference<>(fragment);
    }

    /**
     * 从Activity调用时的方法
     * @param activity 该Activity
     */
    public static ImageSelector from(Activity activity) {
        return new ImageSelector(activity);
    }

    /**
     * 从Fragment调用时的方法
     * @param fragment 该Fragment
     */
    public static ImageSelector from(Fragment fragment) {
        return new ImageSelector(fragment);
    }

    /**
     * 设置选择图片的格式
     * @param imageTypes 图片格式的Set，需要用ImageType.of方法创建
     */
    public SelectorBuilder setImageType(Set<ImageType> imageTypes){
        return new SelectorBuilder(this,imageTypes);
    }

    /**
     * 获取传入的Activity
     * @return 传入的Activity
     */
    public Activity getActivity() {
        return mActivity.get();
    }

    /**
     * 获取传入的Fragment
     * @return 传入的Fragment
     */
    public Fragment getFragment() {
        return mFragment.get();
    }
}
