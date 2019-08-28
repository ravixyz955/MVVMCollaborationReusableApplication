package com.example.user.mvvmcollabreusableapp.network.service;

import com.example.user.mvvmcollabreusableapp.network.model.CollaborationData;
import com.example.user.mvvmcollabreusableapp.network.model.FollowersData;

import java.util.List;
import java.util.Map;

import io.reactivex.Single;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public interface CollaborationAPIService {

    Single<List<CollaborationData>> myWallCollaboration(String token, String projectId);

    Single<List<CollaborationData>> myPostsCollaboration(String token, String userId);

    Single<List<FollowersData>> followersCollaboration(String token, String userId);

    Single<Object> getFollow(String token, String postId);

    Single<Object> getUnfollow(String token, String postId);

    Single<Object> getLike(String token, String postId);

    Single<Object> getDislike(String token, String postId);

    Single<Object> collaborationParamsPost(String token, Map<String, RequestBody> partMap, List<MultipartBody.Part> files);
}