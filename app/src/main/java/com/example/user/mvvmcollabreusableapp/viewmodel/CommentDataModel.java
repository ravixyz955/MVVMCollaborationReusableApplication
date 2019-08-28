package com.example.user.mvvmcollabreusableapp.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.example.user.mvvmcollabreusableapp.repository.CollabRepository;

public class CommentDataModel extends AndroidViewModel {

    public CommentDataModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<Integer> getLikes(String id) {
        CollabRepository collabRepository = CollabRepository.getInstance();
        return collabRepository.getLikes(getApplication(), id);
    }

    public LiveData<Integer> getUnikes(String id) {
        CollabRepository collabRepository = CollabRepository.getInstance();
        return collabRepository.getDislikes(getApplication(), id);
    }

    public LiveData<String> getFollow(String id) {
        CollabRepository collabRepository = CollabRepository.getInstance();
        return collabRepository.getFollow(getApplication(), id);
    }

    public LiveData<String> getUnfollow(String id) {
        CollabRepository collabRepository = CollabRepository.getInstance();
        return collabRepository.getUnfollow(getApplication(), id);
    }
}
