package com.example.user.mvvmcollabreusableapp.network.service;

import com.example.user.mvvmcollabreusableapp.network.model.AllUsers;
import com.example.user.mvvmcollabreusableapp.network.model.Project;

import java.util.List;

import io.reactivex.Single;
import retrofit2.Call;

public interface ProjectAPIService {

    Call<List<Project>> getProjects(String token, String parent);

    Call<Project> getProject(String token, String projectId);

    Single<AllUsers> getUsers(String token);
}
