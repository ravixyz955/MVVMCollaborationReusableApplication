package com.example.user.mvvmcollabreusableapp.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.example.user.mvvmcollabreusableapp.network.model.CollaborationData;
import com.example.user.mvvmcollabreusableapp.repository.CollabRepository;

import java.util.List;

public class MyWallViewModel extends AndroidViewModel {
    private LiveData<List<CollaborationData>> myWallPostsLiveData;
    private MutableLiveData<List<CollaborationData>> myWallPosts;

    public MyWallViewModel(@NonNull Application application) {
        super(application);
    }

    /*public LiveData<List<CollaborationData>> getMyWallData() {
        CollabRepository collabRepository = CollabRepository.getInstance();
        myWallPostsLiveData = collabRepository.getMyWallComments(getApplication());
        return myWallPostsLiveData;
    }*/

    public LiveData<List<CollaborationData>> getMyWallData() {
        if (myWallPosts == null) {
            myWallPosts = new MutableLiveData<>();
            CollabRepository collabRepository = CollabRepository.getInstance();
            myWallPosts.setValue(collabRepository.getComments());
        }
        return myWallPosts;
    }
}