package com.n0texpecterr0r.rhapsody.view;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.n0texpecterr0r.rhapsody.R;
import com.n0texpecterr0r.rhapsody.SelectConfig;
import com.n0texpecterr0r.rhapsody.engine.ImageEngine;
import com.n0texpecterr0r.rhapsody.view.ui.ScaleImageView;

public class PreviewActivity extends AppCompatActivity {
    private ScaleImageView mScaleImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
        Intent intent = getIntent();
        String path = intent.getStringExtra("path");

        mScaleImageView = findViewById(R.id.siv_image);
        ImageEngine engine = SelectConfig.getInstance().mEngine;
        engine.loadImage(this,0,0,mScaleImageView,path);
    }
}
