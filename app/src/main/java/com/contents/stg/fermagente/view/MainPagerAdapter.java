package com.contents.stg.fermagente.view;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class MainPagerAdapter extends FragmentPagerAdapter {

    Context context;

    private MainPagerAdapter(FragmentManager manager) {
        super(manager);
    }

    public MainPagerAdapter(Context context, FragmentManager manager) {
        this(manager);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 1)
            return FeedFragment.newInstance();
        return DemoFragment.newInstance("FRAGMENT " + position);
    }

    @Override
    public int getCount() { return 3; }
}
