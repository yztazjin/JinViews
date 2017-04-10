package ttyy.com.jinviews.demo;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import ttyy.com.jinviews.BeizerCircleIndicatedViewPager;

public class IndicatorBeizerCircleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_indicator_beizer);

        ArrayList<TextView> textViews = new ArrayList<>();
        for(int i = 0; i < 5 ; i++){
            TextView tv = new TextView(this);
            tv.setGravity(Gravity.CENTER);
            tv.setTextSize(20);
            tv.setText("TextItem"+i);
            tv.setTextColor(Color.WHITE);
            textViews.add(tv);

            switch (i){
                case 0:
                    tv.setBackgroundColor(Color.BLACK);
                    break;
                case 1:
                    tv.setBackgroundColor(Color.parseColor("#333333"));
                    break;
                case 2:
                    tv.setBackgroundColor(Color.parseColor("#555555"));
                    break;
                case 3:
                    tv.setBackgroundColor(Color.parseColor("#777777"));
                    break;
                case 4:
                    tv.setBackgroundColor(Color.parseColor("#999999"));
                    break;
            }
        }


        BeizerCircleIndicatedViewPager pager = (BeizerCircleIndicatedViewPager) findViewById(R.id.pager);

        Adapter adapter = new Adapter();
        adapter.views = textViews;
        pager.setAdapter(adapter);
    }

    static class Adapter extends PagerAdapter {

        ArrayList<? extends View> views;

        @Override
        public int getCount() {
            if(views != null)
                return views.size();
            return 0;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(views.get(position));
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(views.get(position));
            return views.get(position);
        }
    }
}
