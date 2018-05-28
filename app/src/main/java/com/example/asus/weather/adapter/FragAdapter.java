package com.example.asus.weather.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.ActionBar;

import com.example.asus.weather.fragment.WeatherFragment;

import java.util.ArrayList;

/**
 * viewpager适配器
 * Created by ASUS on 2018/5/19.
 */

public class FragAdapter extends FragmentStatePagerAdapter {

    private ArrayList<Fragment> mFragments;
    private FragmentManager mf;

    public FragAdapter(FragmentManager fm,ArrayList<Fragment> fragments) {
        super(fm);
        mFragments = fragments;
        mf = fm;
    }

    @Override
    public Fragment getItem(int arg0) {
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

    public void addItem(String data){
        Fragment fragment = WeatherFragment.newFragment(data);
        ArrayList<Fragment> list = new ArrayList<>();
        list.addAll(this.mFragments);
        this.mFragments.removeAll(this.mFragments);
        this.mFragments.add(fragment);
        this.mFragments.addAll(list);
        notifyDataSetChanged();
    }

}
