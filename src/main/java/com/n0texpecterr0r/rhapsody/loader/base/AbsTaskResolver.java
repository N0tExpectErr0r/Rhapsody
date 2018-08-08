package com.n0texpecterr0r.rhapsody.loader.base;

/**
 * @author Created by Nullptr
 * @date 2018/8/8 9:13
 * @describe 抽象图像任务处理类
 */
public abstract class AbsTaskResolver {
    protected AbsTaskResolver mNextResolver;
    public void setNextResolver(AbsTaskResolver nextResolver){
        mNextResolver = nextResolver;
    }

    public abstract void handleTask();
}
