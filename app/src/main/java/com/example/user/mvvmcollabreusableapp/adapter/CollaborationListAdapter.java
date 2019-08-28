package com.example.user.mvvmcollabreusableapp.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.example.user.mvvmcollabreusableapp.network.model.CollaborationData;
import com.example.user.mvvmcollabreusableapp.network.model.FollowersData;
import com.example.user.mvvmcollabreusableapp.widgets.CommentDataView;

import java.util.ArrayList;
import java.util.List;

public class CollaborationListAdapter extends RecyclerView.Adapter<CollaborationListAdapter.CollaborationViewHolder> {
    private List<CollaborationData> collaborationData;
    private List<FollowersData> followersData;

    public CollaborationListAdapter(ArrayList<CollaborationData> collaborationData, String name) {
        this.collaborationData = collaborationData;
    }

    public CollaborationListAdapter(List<FollowersData> followersData, String name) {
        this.followersData = followersData;
    }

    @NonNull
    @Override
    public CollaborationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CommentDataView itemView = new CommentDataView(parent.getContext());
        itemView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        return new CollaborationViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CollaborationViewHolder holder, int position) {
        if (collaborationData != null && !collaborationData.isEmpty()) {
            holder.getCustomView().setData(this.collaborationData.get(position));
        } else if (followersData != null && !followersData.isEmpty()) {
            holder.getCustomView().setData(this.followersData.get(position), position);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        if (collaborationData != null && !collaborationData.isEmpty())
            return this.collaborationData.size();
        else if (followersData != null && !followersData.isEmpty())
            return this.followersData.size();

        return 0;
    }

    class CollaborationViewHolder extends RecyclerView.ViewHolder {

        private CommentDataView customView;

        public CollaborationViewHolder(View v) {
            super(v);
            customView = (CommentDataView) v;
        }

        public CommentDataView getCustomView() {
            return customView;
        }
    }
}