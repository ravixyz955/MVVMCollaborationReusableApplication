package com.example.user.mvvmcollabreusableapp.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.example.user.mvvmcollabreusableapp.network.model.CollaborationData;
import com.example.user.mvvmcollabreusableapp.repository.CollabRepository;

import java.util.List;

public class MyPostsViewModel extends AndroidViewModel {
    private LiveData<List<CollaborationData>> myPostsLiveData;
    private MutableLiveData<List<CollaborationData>> myWallPosts;

    public MyPostsViewModel(@NonNull Application application) {
        super(application);
    }

    /*public LiveData<List<CollaborationData>> getMyPostsData() {
        CollabRepository collabRepository = CollabRepository.getInstance();
        myPostsLiveData = collabRepository.getMyPostsComments(getApplication());
        return myPostsLiveData;
    }*/

    public LiveData<List<CollaborationData>> getMyPostsData() {
        if (myWallPosts == null) {
            myWallPosts = new MutableLiveData<>();
            CollabRepository collabRepository = CollabRepository.getInstance();
            myWallPosts.setValue(collabRepository.getComments());
        }
        return myWallPosts;
    }
}
