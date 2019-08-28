package com.example.user.mvvmcollabreusableapp.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.user.mvvmcollabreusableapp.fragment.FollowersFragment;
import com.example.user.mvvmcollabreusableapp.fragment.MyPostsFragment;
import com.example.user.mvvmcollabreusableapp.fragment.MyWallFragment;

public class CollaborationAdapter extends FragmentStatePagerAdapter {
    private String[] tabTitles = new String[]{"My Wall", "My Posts", "Followers"};

    public CollaborationAdapter(Context context, FragmentManager fm) {
        super(fm);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new MyWallFragment();
            case 1:
                return new MyPostsFragment();
            case 2:
                return new FollowersFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return tabTitles.length;
    }
}