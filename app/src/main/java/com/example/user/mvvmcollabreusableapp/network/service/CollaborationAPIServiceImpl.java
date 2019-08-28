package com.example.user.mvvmcollabreusableapp.network.service;

import com.example.user.mvvmcollabreusableapp.network.RemoteServerAPI;
import com.example.user.mvvmcollabreusableapp.network.model.CollaborationData;
import com.example.user.mvvmcollabreusableapp.network.model.FollowersData;

import java.util.List;
import java.util.Map;

import io.reactivex.Single;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class CollaborationAPIServiceImpl implements CollaborationAPIService {

    private final RemoteServerAPI remoteServerAPI;

    public CollaborationAPIServiceImpl(RemoteServerAPI remoteServerAPI) {
        this.remoteServerAPI = remoteServerAPI;
    }

    @Override
    public Single<List<CollaborationData>> myWallCollaboration(String token, String projectId) {
        return remoteServerAPI.myWallCollaboration(token, projectId);
    }

    @Override
    public Single<List<CollaborationData>> myPostsCollaboration(String token, String userId) {
        return remoteServerAPI.myPostsCollaboration(token, userId);
    }

    @Override
    public Single<List<FollowersData>> followersCollaboration(String token, String userId) {
        return remoteServerAPI.followersCollaboration(token, userId);
    }

    @Override
    public Single<Object> getFollow(String token, String postId) {
        return remoteServerAPI.getFollow(token, postId);
    }

    @Override
    public Single<Object> getUnfollow(String token, String postId) {
        return remoteServerAPI.getUnfollow(token, postId);
    }

    @Override
    public Single<Object> getLike(String token, String postId) {
        return remoteServerAPI.getLike(token, postId);
    }

    @Override
    public Single<Object> getDislike(String token, String postId) {
        return remoteServerAPI.getDislike(token, postId);
    }

    @Override
    public Single<Object> collaborationParamsPost(String token, Map<String, RequestBody> partMap, List<MultipartBody.Part> files) {
        return remoteServerAPI.collaborationParamsPost(token, partMap, files);
    }
}