package com.example.asus.weather.adapter;
import android.util.Log;
import android.view.ViewGroup;

import com.example.asus.weather.fragment.WeatherFragment;

import java.util.ArrayList;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;

/**
 * viewpager适配器
 * Created by ASUS on 2018/5/19.
 */

public class FragAdapter extends FragmentStatePagerAdapter {

    private ArrayList<Fragment> mFragments;
    private FragmentManager fragmentManager;

    public FragAdapter(FragmentManager fm, ArrayList<Fragment> fragments) {
        super(fm);
        mFragments = fragments;
        fragmentManager = fm;
    }

    @Override
    public Fragment getItem(int arg0) {
        Log.d("Fragment", "getItem：" + arg0);
        return mFragments.get(arg0);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

    @Override
    public int getItemPosition(Object object) {
        return PagerAdapter.POSITION_NONE;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        if (!mFragments.contains(fragment))
            return null;
        return fragment;
    }

    /**
     * 添加
     * @param data
     */
    public void addItem(String data){
        Fragment fragment = WeatherFragment.newFragment(data);
        ArrayList<Fragment> list = new ArrayList<>();
        list.addAll(this.mFragments);
        this.mFragments.removeAll(this.mFragments);
        this.mFragments.add(fragment);
        this.mFragments.addAll(list);
        notifyDataSetChanged();
    }

    /**
     * 删除
     * @param data
     */
    public void deleteItem(String data){
        for(int i = 0; i < mFragments.size(); i++){
            if(mFragments.get(i).getArguments().getString("key").compareTo(data) == 0){
                mFragments.remove(mFragments.get(i));
            }
        }
        notifyDataSetChanged();
    }
}
