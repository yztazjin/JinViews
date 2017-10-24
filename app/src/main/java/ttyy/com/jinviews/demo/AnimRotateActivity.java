package ttyy.com.jinviews.demo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import ttyy.com.jinviews.toy.Rotate3DImageView;

/**
 * Author: hjq
 * Date  : 2017/10/23 20:51
 * Name  : AnimRotateActivity
 * Intro : Edit By hjq
 * Version : 1.0
 */
public class AnimRotateActivity extends AppCompatActivity {

    Rotate3DImageView image;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anim_rotate);
        image = (Rotate3DImageView) findViewById(R.id.image);

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                image.rollAnim();
            }
        });
    }
}
