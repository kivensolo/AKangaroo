package com.zeke.kangaroo.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public final class CommonFragmentAdapter extends FragmentPagerAdapter {
    private ArrayList<Fragment> fragments;

    public CommonFragmentAdapter(FragmentManager fm) {
        super(fm);
        this.fragments = new ArrayList<Fragment>(0);
    }

    public CommonFragmentAdapter(FragmentManager fm, List<Fragment> fragmentList) {
        super(fm);
        this.fragments = new ArrayList<Fragment>(fragmentList);
    }

    public void addFragment(Fragment fragment){
        this.fragments.add(fragment);
        notifyDataSetChanged();
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public long getItemId(int position) {
        int hashCode = fragments.get(position).hashCode();
        return hashCode;
    }

    @Override
    public int getItemPosition(Object object) {
        return super.getItemPosition(object);
    }
}
