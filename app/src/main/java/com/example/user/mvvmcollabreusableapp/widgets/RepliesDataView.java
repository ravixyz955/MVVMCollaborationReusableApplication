package com.example.user.mvvmcollabreusableapp.widgets;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.example.user.mvvmcollabreusableapp.R;
import com.example.user.mvvmcollabreusableapp.databinding.RepliesItemBinding;
import com.example.user.mvvmcollabreusableapp.fragment.CollabFilesDialogFragment;
import com.example.user.mvvmcollabreusableapp.fragment.CollabTagsDialogFragment;
import com.example.user.mvvmcollabreusableapp.network.model.CollaborationData;
import com.example.user.mvvmcollabreusableapp.network.model.FollowersData;
import com.example.user.mvvmcollabreusableapp.ui.CollaborationActivity;
import com.pchmn.materialchips.ChipView;

import java.io.File;

public class RepliesDataView extends LinearLayout {

    private RepliesItemBinding binding;
    private Context mContext;


    public RepliesDataView(Context context) {
        super(context);
        this.mContext = context;
        init();
    }

    public RepliesDataView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public RepliesDataView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @BindingAdapter({"collabTagData", "followersTagData"})
    public static void setTagData(ChipView chipView, CollaborationData collaborationData, FollowersData followersData) {
        if ((collaborationData != null && collaborationData.getTags() != null && !collaborationData.getTags().isEmpty())) {
            if (collaborationData.getTags().size() > 1) {
                String tags = 1 + "+ more tags";
                chipView.setLabel(tags);
            } else
                chipView.setLabel(collaborationData.getTags().get(0).getFullName());
        }
        if (followersData != null && followersData.getTags() != null && !followersData.getTags().isEmpty()) {
            if (followersData.getTags().size() > 1) {
                String tags = 1 + "+ more tags";
                chipView.setLabel(tags);
            } else
                chipView.setLabel(followersData.getTags().get(0).getFullName());
        }
        chipView.setOnChipClicked(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (collaborationData != null && collaborationData.getTags().size() > 1) {
                    CollabTagsDialogFragment collabTagsDialogFragment = CollabTagsDialogFragment.newInstance(collaborationData);
                    collabTagsDialogFragment.show(((CollaborationActivity) chipView.getContext()).getSupportFragmentManager(), "Tags");
                } else if (followersData != null && followersData.getTags().size() > 1) {
                    CollabTagsDialogFragment collabTagsDialogFragment = CollabTagsDialogFragment.newInstance(followersData);
                    collabTagsDialogFragment.show(((CollaborationActivity) chipView.getContext()).getSupportFragmentManager(), "Tags");
                }
            }
        });
    }

    @BindingAdapter({"collabFileData", "followersFileData"})
    public static void setFileData(ChipView chipView, CollaborationData collaborationData, FollowersData followersData) {
        if (collaborationData != null && collaborationData.getAttachments() != null) {
            if (!collaborationData.getAttachments().isEmpty()) {
                String attachmentName = collaborationData.getAttachments().get(0);

                if (collaborationData.getAttachments().size() > 1) {
                    String attachments = 1 + "+ more attachments";
                    chipView.setLabel(attachments);
                } else
                    chipView.setLabel(new File(attachmentName).getName());
            } else
                chipView.setLabel("No attachments");
        }

        if (followersData != null && followersData.getAttachments() != null) {
            if (!followersData.getAttachments().isEmpty()) {
                String attachmentName = followersData.getAttachments().get(0);

                if (followersData.getAttachments().size() > 1) {
                    String attachments = 1 + "+ more attachments";
                    chipView.setLabel(attachments);
                } else
                    chipView.setLabel(new File(attachmentName).getName());
            } else
                chipView.setLabel("No attachments");
        }
        chipView.setOnChipClicked(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (collaborationData != null && collaborationData.getAttachments().size() > 1) {
                    CollabFilesDialogFragment collabFilesDialogFragment = CollabFilesDialogFragment.newInstance(collaborationData);
                    collabFilesDialogFragment.show(((CollaborationActivity) chipView.getContext()).getSupportFragmentManager(), "Files");
                } else if (followersData != null && followersData.getAttachments().size() > 1) {
                    CollabFilesDialogFragment collabTagsDialogFragment = CollabFilesDialogFragment.newInstance(followersData);
                    collabTagsDialogFragment.show(((CollaborationActivity) chipView.getContext()).getSupportFragmentManager(), "Files");
                }
            }
        });

    }

    private void init() {
        binding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.replies_item, this, true);
    }

    public void setData(CollaborationData collaborationData) {
        if (collaborationData != null) {
            binding.setCollabData(collaborationData);
        }
    }

    public void setData(FollowersData followersData) {
        binding.setFollowersData(followersData);
    }
}