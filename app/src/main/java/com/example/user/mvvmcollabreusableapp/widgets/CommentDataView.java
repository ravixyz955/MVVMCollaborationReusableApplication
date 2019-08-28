package com.example.user.mvvmcollabreusableapp.widgets;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.user.mvvmcollabreusableapp.R;
import com.example.user.mvvmcollabreusableapp.adapter.RepliesListAdapter;
import com.example.user.mvvmcollabreusableapp.constants.Constants;
import com.example.user.mvvmcollabreusableapp.databinding.CommentsItemBinding;
import com.example.user.mvvmcollabreusableapp.fragment.CollabFilesDialogFragment;
import com.example.user.mvvmcollabreusableapp.fragment.CollabReplyPostFragment;
import com.example.user.mvvmcollabreusableapp.fragment.CollabTagsDialogFragment;
import com.example.user.mvvmcollabreusableapp.network.model.CollaborationData;
import com.example.user.mvvmcollabreusableapp.network.model.FollowersData;
import com.example.user.mvvmcollabreusableapp.network.model.User;
import com.example.user.mvvmcollabreusableapp.ui.CollaborationActivity;
import com.example.user.mvvmcollabreusableapp.viewmodel.CommentDataModel;
import com.pchmn.materialchips.ChipView;

import java.io.File;
import java.util.List;
import java.util.Objects;

public class CommentDataView extends LinearLayout {

    private CommentsItemBinding binding;
    private Context mContext;
    private ClickHandler clickHandler;
    private CommentDataModel commentDataModel;


    public CommentDataView(Context context) {
        super(context);
        this.mContext = context;
        commentDataModel = ViewModelProviders.of(((CollaborationActivity) context)).get(CommentDataModel.class);
        init();
    }

    public CommentDataView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CommentDataView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
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

    @BindingAdapter({"collabFollowing", "followersFollowing"})
    public static void setFollowing(ImageView img, CollaborationData collaborationData, FollowersData followersData) {
        if (collaborationData != null && !collaborationData.getFollowers().isEmpty()) {
            setFollowTag(img);
        } else {
            setUnfollowTag(img);
        }

        if (followersData != null && !followersData.getFollowers().isEmpty()) {
            setFollowTag(img);
        } else {
            setUnfollowTag(img);
        }
    }

    private static void setUnfollowTag(ImageView img) {
        img.setTag(Constants.UNFOLLOW);
        img.setImageDrawable(img.getContext().getResources().getDrawable(R.drawable.ic_unfollow));
    }

    private static void setFollowTag(ImageView img) {
        img.setTag(Constants.FOLLOW);
        img.setImageDrawable(img.getContext().getResources().getDrawable(R.drawable.ic_follow));
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
        binding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.comments_item, this, true);
        clickHandler = new ClickHandler();
        binding.setHandler(clickHandler);
    }

    public void setData(CollaborationData collaborationData) {
        if (collaborationData != null) {
            binding.setCollabData(collaborationData);
            binding.setHandler(clickHandler);
        }
    }

    public void setData(FollowersData followersData, int commentPosition) {
        binding.setFollowersData(followersData);
        binding.setHandler(clickHandler);
    }

    private void makeFollow(String id) {
        commentDataModel.getFollow(id).observe(((CollaborationActivity) mContext), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String followers) {
                if (Objects.requireNonNull(followers).contains("success")) {
                    if (binding.followers.getTag().toString().equalsIgnoreCase(Constants.FOLLOW)) {
                        binding.followers.setTag(Constants.UNFOLLOW);
                        binding.followers.setImageDrawable(getResources().getDrawable(R.drawable.ic_unfollow));
                    } else {
                        binding.followers.setTag(Constants.FOLLOW);
                        binding.followers.setImageDrawable(getResources().getDrawable(R.drawable.ic_follow));
                    }
                }
            }
        });
    }

    public class ClickHandler {
        public void onClickListener(RecyclerView repliesRecycler, CollaborationData collaborationData, FollowersData followersData) {
            int visibility = repliesRecycler.getVisibility();
            if (collaborationData != null) {
                if (visibility == GONE) {
                    repliesRecycler.setVisibility(VISIBLE);
                    binding.arrowIcon.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
                    List<CollaborationData> replies = collaborationData.getReplies();
                    repliesRecycler.setLayoutManager(new LinearLayoutManager(repliesRecycler.getContext()));
                    repliesRecycler.setAdapter(new RepliesListAdapter(mContext, replies));
                } else if (visibility == VISIBLE) {
                    repliesRecycler.setVisibility(GONE);
                    binding.arrowIcon.setImageResource(R.drawable.ic_keyboard_arrow_right_black_24dp);
                }
            }

            if (followersData != null) {
                if (visibility == GONE) {
                    repliesRecycler.setVisibility(VISIBLE);
                    binding.arrowIcon.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
                    List<FollowersData> replies = followersData.getReplies();
                    repliesRecycler.setLayoutManager(new LinearLayoutManager(repliesRecycler.getContext()));
                    repliesRecycler.setAdapter(new RepliesListAdapter(mContext, replies, FollowersData.class.getName()));
                } else if (visibility == VISIBLE) {
                    repliesRecycler.setVisibility(GONE);
                    binding.arrowIcon.setImageResource(R.drawable.ic_keyboard_arrow_right_black_24dp);
                }
            }
        }

        public void onReplyListener(CollaborationData collaborationData, FollowersData followersData) {
            String parent = null;
            List<User> replyUsersList = ((CollaborationActivity) binding.commentReplyTxt.getContext()).getUsersList();
            if (collaborationData != null)
                parent = collaborationData.getId();
            if (followersData != null)
                parent = followersData.getId();
            if (parent != null) {
                CollabReplyPostFragment collabReplyPostFragment = CollabReplyPostFragment.newInstance(replyUsersList, parent);
                collabReplyPostFragment.show(((CollaborationActivity) binding.commentReplyTxt.getContext()).getSupportFragmentManager(), "Reply");
            }
        }

        public void likesListener(String id) {
            commentDataModel.getLikes(id).observe(((CollaborationActivity) mContext), new Observer<Integer>() {
                @Override
                public void onChanged(@Nullable Integer likes) {
                    if (likes != null)
                        binding.likes.setText(likes);
                }
            });
        }

        public void unlikesListener(String id) {
            commentDataModel.getUnikes(id).observe(((CollaborationActivity) mContext), new Observer<Integer>() {
                @Override
                public void onChanged(@Nullable Integer unlikes) {
                    if (unlikes != null)
                        binding.dislikes.setText(unlikes);
                }
            });
        }

        public void followersListener(ImageView img, String id) {
            if (img.getTag().toString()
                    .equalsIgnoreCase(Constants.FOLLOW)) {
                makeUnfollow(id);
            } else {
                makeFollow(id);
            }
        }

        private void makeUnfollow(String id) {
            commentDataModel.getUnfollow(id).observe(((CollaborationActivity) mContext), new Observer<String>() {
                @Override
                public void onChanged(@Nullable String followers) {
                    if (Objects.requireNonNull(followers).contains("success")) {
                        if (binding.followers.getTag().toString().equalsIgnoreCase(Constants.FOLLOW)) {
                            binding.followers.setTag(Constants.UNFOLLOW);
                            binding.followers.setImageDrawable(getResources().getDrawable(R.drawable.ic_unfollow));
                        } else {
                            binding.followers.setTag(Constants.FOLLOW);
                            binding.followers.setImageDrawable(getResources().getDrawable(R.drawable.ic_follow));
                        }
                    }
                }
            });
        }
    }
}