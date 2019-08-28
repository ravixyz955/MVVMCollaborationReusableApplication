package com.example.user.mvvmcollabreusableapp.network.service;

import com.squareup.okhttp.RequestBody;

import retrofit2.Call;

public interface ReplyAPIService {
    Call<Object> postReplies(String token, String comment_id, RequestBody requestBody);
}
