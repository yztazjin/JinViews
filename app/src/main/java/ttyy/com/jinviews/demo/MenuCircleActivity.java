package ttyy.com.jinviews.demo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import ttyy.com.jinviews.CircleLayout;
import ttyy.com.jinviews.CircleLayoutAdapter;

/**
 * Author: hjq
 * Date  : 2017/04/10 21:50
 * Name  : MenuCircleActivity
 * Intro : Edit By hjq
 * Version : 1.0
 */
public class MenuCircleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_circle);

        CircleLayout cl_test = (CircleLayout) findViewById(R.id.cl_test);
        CircleLayoutAdapter<String> adapter = new CircleLayoutAdapter<String>() {
            @Override
            public View getView(int position, ViewGroup parent) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_circle, parent, false);
                TextView tv = (TextView) view.findViewById(R.id.tv);
                tv.setText(getItem(position));
                return view;
            }
        } ;
        ArrayList<String> tests = new ArrayList<String>();
        for(int i=0; i<3; i++){
            tests.add(String.valueOf(i));
        }
        adapter.setDatas(tests);
        cl_test.setAdapter(adapter)
                .setStartDegreeOffset(50);
    }
}
