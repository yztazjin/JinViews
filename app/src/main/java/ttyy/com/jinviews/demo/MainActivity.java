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
        }
    }
}
