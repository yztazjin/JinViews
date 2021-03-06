package ttyy.com.jinviews.demo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.tv_menu_circle).setOnClickListener(this);
        findViewById(R.id.tv_indicator_beizercicle).setOnClickListener(this);
        findViewById(R.id.tv_menu_left).setOnClickListener(this);
        findViewById(R.id.tv_menu_right).setOnClickListener(this);
        findViewById(R.id.tv_menu_anim_rotate).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_menu_circle:
                startActivity(new Intent(this, MenuCircleActivity.class));
                break;
            case R.id.tv_indicator_beizercicle:
                startActivity(new Intent(this, IndicatorBeizerCircleActivity.class));
                break;
            case R.id.tv_menu_left:
                startActivity(new Intent(this, MenuDemoLeftActivity.class));
                break;
            case R.id.tv_menu_right:
                startActivity(new Intent(this, MenuDemoRithtActivity.class));
                break;
            case R.id.tv_menu_anim_rotate:
                startActivity(new Intent(this, AnimRotateActivity.class));
                break;
        }
    }
}
