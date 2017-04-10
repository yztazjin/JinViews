package ttyy.com.jinviews;

import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Author: hjq
 * Date  : 2017/03/30 20:05
 * Name  : CircleLayoutAdapter
 * Intro : Edit By hjq
 * Version : 1.0
 */
public abstract class CircleLayoutAdapter<T> {

    protected List<T> datas;

    NotifyDataSetChanger mNotifyDataSetChanger;

    public int getCount(){
        return datas == null ? 0 : datas.size();
    }

    public void setDatas(List<T> datas){
        this.datas = datas;
    }

    public T getItem(int pos){
        if(datas == null){
            return null;
        }

        if(datas.size() <= pos){
            return null;
        }

        return datas.get(pos);
    }

    public abstract View getView(int position, ViewGroup parent);

    public final void notifyDataSetChanged(){
        if(mNotifyDataSetChanger != null){
            mNotifyDataSetChanger.notifyDataSetChanged();
        }
    }

    interface NotifyDataSetChanger{
        void notifyDataSetChanged();
    }
}
