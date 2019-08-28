package com.example.user.mvvmcollabreusableapp.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.example.user.mvvmcollabreusableapp.network.model.CollaborationData;
import com.example.user.mvvmcollabreusableapp.network.model.FollowersData;
import com.example.user.mvvmcollabreusableapp.widgets.RepliesDataView;

import java.util.List;

public class RepliesListAdapter extends RecyclerView.Adapter<RepliesListAdapter.RepliesViewHolder> {
    private Context mContext;
    private List<CollaborationData> mReplies;
    private List<FollowersData> mFollowersReplies;

    public RepliesListAdapter(Context mContext, List<CollaborationData> replies) {
        this.mContext = mContext;
        this.mReplies = replies;
    }

    public RepliesListAdapter(Context mContext, List<FollowersData> replies, String name) {
        this.mContext = mContext;
        this.mFollowersReplies = replies;
    }

    @NonNull
    @Override
    public RepliesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RepliesDataView itemView = new RepliesDataView(mContext);
        itemView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        return new RepliesViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RepliesViewHolder holder, int position) {
        if (mReplies != null) {
            holder.getCustomView().setData(this.mReplies.get(position));
        } else
            holder.getCustomView().setData(this.mFollowersReplies.get(position));
    }

    @Override
    public int getItemCount() {
        if (mReplies != null)
            return this.mReplies.size();
        else if (mFollowersReplies != null)
            return this.mFollowersReplies.size();
        return 0;
    }

    class RepliesViewHolder extends RecyclerView.ViewHolder {

        private RepliesDataView customView;

        public RepliesViewHolder(View v) {
            super(v);
            customView = (RepliesDataView) v;
        }

        public RepliesDataView getCustomView() {
            return customView;
        }
    }
}