package com.example.user.mvvmcollabreusableapp.fragment;

import android.annotation.SuppressLint;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.example.user.mvvmcollabreusableapp.R;
import com.example.user.mvvmcollabreusableapp.adapter.CollaborationListAdapter;
import com.example.user.mvvmcollabreusableapp.databinding.FragFollowersLayoutBinding;
import com.example.user.mvvmcollabreusableapp.network.model.FollowersData;
import com.example.user.mvvmcollabreusableapp.viewmodel.FollowersViewModel;

import java.util.ArrayList;
import java.util.List;

public class FollowersFragment extends Fragment {
    private String userId;
    private FragFollowersLayoutBinding binding;
    private FollowersViewModel followersViewModel;

    @BindingAdapter({"handler", "followersData"})
    public static void setAdapter(RecyclerView followersRecyclerView, FollowersFragment fragment, List<FollowersData> followersData) {
        LinearLayoutManager llm = new LinearLayoutManager(fragment.getContext());
        llm.scrollToPosition(0);
        llm.setSmoothScrollbarEnabled(true);
        followersRecyclerView.setLayoutManager(llm);
        followersRecyclerView.addItemDecoration(new DividerItemDecoration(fragment.getContext(), DividerItemDecoration.VERTICAL));
        followersRecyclerView.setHasFixedSize(true);
        if (followersData != null && !followersData.isEmpty()) {
            followersRecyclerView.setAdapter(new CollaborationListAdapter(new ArrayList<>(followersData), FollowersFragment.class.getName()));
        }
    }

    @BindingAdapter("isLoading")
    public static void setIsLoading(FrameLayout frameLayout, boolean visibile) {
        frameLayout.setVisibility(visibile ? View.GONE : View.VISIBLE);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        userId = new Gson().fromJson(DataUtils.getUser(mContext), User.class).getId();
        binding = DataBindingUtil.inflate(inflater, R.layout.frag_followers_layout, container, false);
        followersViewModel = ViewModelProviders.of(this).get(FollowersViewModel.class);
        binding.setHandler(this);
        binding.setFollowersModel(followersViewModel);
        binding.setLifecycleOwner(this);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        updateFollowers(followersViewModel);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            assert getFragmentManager() != null;
            getFragmentManager().beginTransaction().detach(this).attach(this).commit();
        }
    }

    @SuppressLint("LogNotTimber")
    private void updateFollowers(FollowersViewModel followersViewModel) {
        followersViewModel.getFollowersData().observe(this, new Observer<List<FollowersData>>() {
            @Override
            public void onChanged(@Nullable List<FollowersData> followersData) {
                if (followersData != null && !followersData.isEmpty()) {
                    binding.setFollowersData(followersData);
                    binding.setIsLoading(true);
                } else {
                    binding.nofollowers.setVisibility(View.VISIBLE);
                }
            }
        });
    }
}