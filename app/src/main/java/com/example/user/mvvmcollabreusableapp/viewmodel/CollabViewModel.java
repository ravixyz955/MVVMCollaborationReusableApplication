package com.example.user.mvvmcollabreusableapp.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.example.user.mvvmcollabreusableapp.network.model.AllUsers;
import com.example.user.mvvmcollabreusableapp.repository.CollabRepository;

import java.util.List;

public class CollabViewModel extends AndroidViewModel {
    private LiveData<List<AllUsers.Users>> usersMutableLiveData;
//    private MutableLiveData<List<AllUsers.Users>> usersMutableLiveData;

    public CollabViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<List<AllUsers.Users>> getUsers() {
        CollabRepository collabRepository = CollabRepository.getInstance();
        usersMutableLiveData = collabRepository.getUsersList(getApplication());
        return usersMutableLiveData;
    }

    /*public LiveData<List<AllUsers.Users>> getUsers() {
        if (usersMutableLiveData == null) {
            CollabRepository collabRepository = CollabRepository.getInstance();
            usersMutableLiveData = new MutableLiveData<>();
            usersMutableLiveData.setValue(collabRepository.getUsersList());
        }
        return usersMutableLiveData;
    }*/
}
