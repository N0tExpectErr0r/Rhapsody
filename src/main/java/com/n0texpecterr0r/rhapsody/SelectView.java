package com.n0texpecterr0r.rhapsody;

import com.n0texpecterr0r.rhapsody.bean.Floder;
import java.util.List;

/**
 * @author Created by Nullptr
 * @date 2018/7/26 10:30
 * @describe 主界面接口
 */
public interface SelectView {

    /**
     * 接收到文件夹列表的回调
     * @param floders
     */
    void onFloder(List<Floder> floders);
}
