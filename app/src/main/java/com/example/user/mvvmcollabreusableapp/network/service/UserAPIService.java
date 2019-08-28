package com.example.user.mvvmcollabreusableapp.network.service;

import com.example.user.mvvmcollabreusableapp.network.model.ActivateUserRequest;
import com.example.user.mvvmcollabreusableapp.network.model.AuthenticateUserRequest;
import com.example.user.mvvmcollabreusableapp.network.model.ProjectPlan;
import com.example.user.mvvmcollabreusableapp.network.model.RegisterUserRequest;
import com.example.user.mvvmcollabreusableapp.network.model.User;

import io.reactivex.Single;
import retrofit2.Call;

public interface UserAPIService {

    Single<User> registerUser(RegisterUserRequest request);

    Single<User> authenticate(AuthenticateUserRequest request);

    Single<Void> activateUser(ActivateUserRequest request);

    Call<ProjectPlan> getProjectPlan(String token, String projectID);
}