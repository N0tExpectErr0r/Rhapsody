package com.n0texpecterr0r.rhapsody;

import android.app.Activity;
import android.support.v4.app.Fragment;
import com.n0texpecterr0r.rhapsody.bean.ImageType;
import java.lang.ref.WeakReference;
import java.util.Set;

/**
 * @author Created by Nullptr
 * @date 2018/7/25 9:49
 * @describe 图片选择器的入口
 */
public class Rhapsody {

    //持有Activity与Fragment的弱引用
    private final WeakReference<Activity> mActivity;
    private final WeakReference<Fragment> mFragment;

    /**
     * 调用from(Activity)时的构造函数
     * @param activity 传入的Activity
     */
    private Rhapsody(Activity activity) {
        this(activity, (Fragment)null);

    }

    /**
     * 调用from(Fragment)时的构造函数
     * @param fragment 传入的Fragment
     */
    private Rhapsody(Fragment fragment) {
        this(fragment.getActivity(),fragment);
    }

    /**
     * 初始化Activity与Fragment的弱引用
     * @param activity Activity
     * @param fragment Fragment
     */
    private Rhapsody(Activity activity, Fragment fragment) {
        mActivity = new WeakReference<>(activity);
        mFragment = new WeakReference<>(fragment);
    }

    /**
     * 从Activity调用时的方法
     * @param activity 该Activity
     */
    public static Rhapsody from(Activity activity) {
        return new Rhapsody(activity);
    }

    /**
     * 从Fragment调用时的方法
     * @param fragment 该Fragment
     */
    public static Rhapsody from(Fragment fragment) {
        return new Rhapsody(fragment);
    }

    /**
     * 设置图片选择格式
     * @param type 类型
     * @param rest 剩余类型
     */
    public SelectCreator setImageType(ImageType type, ImageType... rest){
        return new SelectCreator(this,ImageType.of(type,rest));
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
