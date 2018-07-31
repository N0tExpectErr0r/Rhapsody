package com.n0texpecterr0r.rhapsody.view.base;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * @author Created by Nullptr
 * @date 2018/7/31 15:51
 * @describe 所有Activity的基类，将生命周期分为了三个
 */
public abstract class BaseActivity extends AppCompatActivity {

    protected abstract void initVariables();
    protected abstract void initViews(Bundle savedInstanceState);
    protected abstract void loadData();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initVariables();
        initViews(savedInstanceState);
        loadData();
    }
}
