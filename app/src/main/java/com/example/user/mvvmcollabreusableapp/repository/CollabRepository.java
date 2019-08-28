package com.example.user.mvvmcollabreusableapp.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;

import com.example.user.mvvmcollabreusableapp.MockData;
import com.example.user.mvvmcollabreusableapp.constants.Constants;
import com.example.user.mvvmcollabreusableapp.network.model.AllUsers;
import com.example.user.mvvmcollabreusableapp.network.model.CollaborationData;
import com.example.user.mvvmcollabreusableapp.network.model.FollowersData;
import com.example.user.mvvmcollabreusableapp.network.service.CollaborationAPIService;
import com.example.user.mvvmcollabreusableapp.network.service.ProjectAPIService;
import com.example.user.mvvmcollabreusableapp.utils.NetworkUtils;
import com.example.user.mvvmcollabreusableapp.utils.Utils;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class CollabRepository {
    private static CollabRepository collabRepository;
    private ProjectAPIService projectAPIService;
    private CollaborationAPIService collaborationAPIService;
    private MutableLiveData<List<AllUsers.Users>> liveallUsers;
    private MutableLiveData<List<CollaborationData>> liveMyWallPosts;
    private MutableLiveData<List<CollaborationData>> liveMyPosts;
    private MutableLiveData<List<FollowersData>> liveFollowersPosts;
    private MutableLiveData<Integer> liveLikes;
    private MutableLiveData<Integer> liveunLikes;
    private MutableLiveData<String> liveUnfollow;
    private MutableLiveData<String> liveFollow;

    private CollabRepository() {
    }

    public static CollabRepository getInstance() {
        if (collabRepository == null) {
            collabRepository = new CollabRepository();
        }
        return collabRepository;
    }


    public LiveData<List<AllUsers.Users>> getUsersList(Context context) {
        projectAPIService = NetworkUtils.provideProjectAPIService(context, "https://auth.");
//        users = MockData.buildUsersuData();

        projectAPIService.getUsers(Constants.TOKEN)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getUsersDisposableObserver(context));

        return liveallUsers;
    }

    public LiveData<List<CollaborationData>> getMyWallComments(Context context) {
        collaborationAPIService = NetworkUtils.provideCollaborationAPIService(context, "https://collab.");

        collaborationAPIService.myWallCollaboration(Constants.TOKEN, "")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getMyWallObsever(context));
//        List<CollaborationData> collaborationData = MockData.buildCommentsData();
//        if (collaborationData != null && !collaborationData.isEmpty()) {
//            Collections.reverse(collaborationData);
//        }
        return liveMyWallPosts;
    }

    public LiveData<List<CollaborationData>> getMyPostsComments(Context context) {
        collaborationAPIService = NetworkUtils.provideCollaborationAPIService(context, "https://collab.");

        collaborationAPIService.myPostsCollaboration(Constants.TOKEN, "")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getMyPostsObsever(context));
        return liveMyWallPosts;
    }

    public LiveData<List<FollowersData>> getFollowersData(Context context) {
//        List<FollowersData> followersData = MockData.buildFollowersData();
        collaborationAPIService = NetworkUtils.provideCollaborationAPIService(context, "https://collab.");
        collaborationAPIService.followersCollaboration(Constants.TOKEN, "")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getFollowersObsever(context));

        return liveFollowersPosts;
    }

    public LiveData<Integer> getLikes(Context context, String id) {
        collaborationAPIService = NetworkUtils.provideCollaborationAPIService(context, "https://collab.");
        collaborationAPIService.getLike(Constants.TOKEN, id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getLikesObserver(context));
        return liveLikes;
    }

    public LiveData<Integer> getDislikes(Context context, String id) {
        collaborationAPIService = NetworkUtils.provideCollaborationAPIService(context, "https://collab.");
        collaborationAPIService.getDislike(Constants.TOKEN, id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getDilikesObserver(context));
        return liveunLikes;
    }

    public LiveData<String> getUnfollow(Context context, String id) {
        collaborationAPIService = NetworkUtils.provideCollaborationAPIService(context, "https://collab.");
        collaborationAPIService.getUnfollow(Constants.TOKEN, id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getUnfollowObserver(context));
        return liveUnfollow;
    }

    public LiveData<String> getFollow(Context context, String id) {
        collaborationAPIService = NetworkUtils.provideCollaborationAPIService(context, "https://collab.");
        collaborationAPIService.getUnfollow(Constants.TOKEN, id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getFollowObserver(context));
        return liveFollow;
    }

    private SingleObserver<? super Object> getFollowObserver(Context context) {
        liveFollow = new MutableLiveData<>();
        return new DisposableSingleObserver<Object>() {
            @Override
            public void onSuccess(Object o) {
                liveFollow.setValue("success");
            }

            @Override
            public void onError(Throwable e) {
                liveFollow.setValue(null);
            }
        };
    }

    public List<CollaborationData> getComments() {
        List<CollaborationData> collaborationData = MockData.buildCommentsData();
        if (collaborationData != null && !collaborationData.isEmpty()) {
            Collections.reverse(collaborationData);
        }
        return collaborationData;
    }

    public List<FollowersData> getFollowersData() {
        List<FollowersData> followersData = MockData.buildFollowersData();
        if (followersData != null && !followersData.isEmpty()) {
            Collections.reverse(followersData);
        }
        return followersData;
    }

    private SingleObserver<? super Object> getUnfollowObserver(Context context) {
        liveUnfollow = new MutableLiveData<>();
        return new DisposableSingleObserver<Object>() {
            @Override
            public void onSuccess(Object o) {
                liveUnfollow.setValue("success");
            }

            @Override
            public void onError(Throwable e) {
                liveUnfollow.setValue(null);
            }
        };
    }


    private SingleObserver<? super Object> getLikesObserver(Context context) {
        liveLikes = new MutableLiveData<>();
        return new DisposableSingleObserver<Object>() {
            @Override
            public void onSuccess(Object likes) {
                try {
                    int likes1 = new JSONObject(new Gson().toJson(likes))
                            .getInt("likes");
                    liveLikes.setValue(likes1);
                } catch (JSONException e) {
                    liveLikes.setValue(null);
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable e) {
                liveLikes.setValue(null);
                Utils.showToast(context, "Like failed");
            }
        };
    }

    private SingleObserver<? super Object> getDilikesObserver(Context context) {
        liveunLikes = new MutableLiveData<>();
        return new DisposableSingleObserver<Object>() {
            @Override
            public void onSuccess(Object likes) {
                try {
                    int unlikes = new JSONObject(new Gson().toJson(likes))
                            .getInt("unlikes");
                    liveunLikes.setValue(unlikes);
                } catch (JSONException e) {
                    liveunLikes.setValue(null);
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable e) {
                liveunLikes.setValue(null);
                Utils.showToast(context, "unlike failed");
            }
        };
    }

    private SingleObserver<? super AllUsers> getUsersDisposableObserver(Context context) {
        liveallUsers = new MutableLiveData<>();
        return new DisposableSingleObserver<AllUsers>() {

            @Override
            public void onSuccess(AllUsers allUsers) {
                ArrayList<AllUsers.Users> collabUsers = allUsers.getUsers();
                liveallUsers.setValue(collabUsers);
            }

            @Override
            public void onError(Throwable e) {
                liveallUsers.setValue(null);
                Utils.showToast(context, "Unable to process request!");
            }
        };
    }

    private SingleObserver<? super List<CollaborationData>> getMyWallObsever(Context context) {
        liveMyWallPosts = new MutableLiveData<>();
        return new DisposableSingleObserver<List<CollaborationData>>() {
            @Override
            public void onSuccess(List<CollaborationData> collaborationData) {
                Collections.reverse(collaborationData);
                liveMyWallPosts.setValue(collaborationData);
            }

            @Override
            public void onError(Throwable e) {
                liveMyWallPosts.setValue(null);
            }
        };
    }

    private SingleObserver<? super List<CollaborationData>> getMyPostsObsever(Context context) {
        liveMyPosts = new MutableLiveData<>();
        return new DisposableSingleObserver<List<CollaborationData>>() {
            @Override
            public void onSuccess(List<CollaborationData> collaborationData) {
                Collections.reverse(collaborationData);
                liveMyPosts.setValue(collaborationData);
            }

            @Override
            public void onError(Throwable e) {
                liveMyPosts.setValue(null);
            }
        };
    }

    private SingleObserver<? super List<FollowersData>> getFollowersObsever(Context context) {
        liveFollowersPosts = new MutableLiveData<>();
        return new DisposableSingleObserver<List<FollowersData>>() {
            @Override
            public void onSuccess(List<FollowersData> followersData) {
                Collections.reverse(followersData);
                liveFollowersPosts.setValue(followersData);
            }

            @Override
            public void onError(Throwable e) {
                liveFollowersPosts.setValue(null);
            }
        };
    }
}