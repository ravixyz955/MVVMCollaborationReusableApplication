package com.example.user.mvvmcollabreusableapp.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.example.user.mvvmcollabreusableapp.network.model.FollowersData;
import com.example.user.mvvmcollabreusableapp.repository.CollabRepository;

import java.util.List;

public class FollowersViewModel extends AndroidViewModel {
    //    private LiveData<List<FollowersData>> followersPost;
    private MutableLiveData<List<FollowersData>> followersPost;


    public FollowersViewModel(@NonNull Application application) {
        super(application);
    }

    /*public LiveData<List<FollowersData>> getFollowersData() {
        CollabRepository collabRepository = CollabRepository.getInstance();
        followersPost = collabRepository.getFollowersData(getApplication());
        return followersPost;
    }*/

    public LiveData<List<FollowersData>> getFollowersData() {
        if (followersPost == null) {
            followersPost = new MutableLiveData<>();
            CollabRepository collabRepository = CollabRepository.getInstance();
            followersPost.setValue(collabRepository.getFollowersData());
        }
        return followersPost;
    }
}
