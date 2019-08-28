package com.example.user.mvvmcollabreusableapp.fragment;

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
import com.example.user.mvvmcollabreusableapp.databinding.FragMypostsLayoutBinding;
import com.example.user.mvvmcollabreusableapp.network.model.CollaborationData;
import com.example.user.mvvmcollabreusableapp.viewmodel.MyPostsViewModel;

import java.util.ArrayList;
import java.util.List;

public class MyPostsFragment extends Fragment {
    private FragMypostsLayoutBinding binding;
    private MyPostsViewModel myPostsViewModel;
    private String userId;

    @BindingAdapter({"handler", "wallData"})
    public static void setAdapter(RecyclerView myPostsRecyclerView, MyPostsFragment fragment, List<CollaborationData> collaborationData) {
        LinearLayoutManager llm = new LinearLayoutManager(fragment.getContext());
        llm.scrollToPosition(0);
        llm.setSmoothScrollbarEnabled(true);
        myPostsRecyclerView.setLayoutManager(llm);
        myPostsRecyclerView.addItemDecoration(new DividerItemDecoration(fragment.getContext(), DividerItemDecoration.VERTICAL));
        myPostsRecyclerView.setHasFixedSize(true);
        if (collaborationData != null && !collaborationData.isEmpty()) {
            myPostsRecyclerView.setAdapter(new CollaborationListAdapter(new ArrayList<>(collaborationData), MyPostsFragment.class.getName()));
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
        binding = DataBindingUtil.inflate(inflater, R.layout.frag_myposts_layout, container, false);
        myPostsViewModel = ViewModelProviders.of(this).get(MyPostsViewModel.class);
        binding.setHandler(this);
        binding.setMyPostsModel(myPostsViewModel);
        binding.setLifecycleOwner(this);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        updateMyPosts(myPostsViewModel);
    }

    private void updateMyPosts(MyPostsViewModel myPostsViewModel) {
        myPostsViewModel.getMyPostsData().observe(this, new Observer<List<CollaborationData>>() {
            @Override
            public void onChanged(@Nullable List<CollaborationData> collaborationData) {
                binding.setWallData(collaborationData);
                binding.setIsLoading(true);
            }
        });
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            assert getFragmentManager() != null;
            getFragmentManager().beginTransaction().detach(this).attach(this).commit();
        }
    }
}