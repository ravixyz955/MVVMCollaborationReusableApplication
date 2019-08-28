package com.example.user.mvvmcollabreusableapp.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;

import com.example.user.mvvmcollabreusableapp.R;
import com.example.user.mvvmcollabreusableapp.adapter.UsersSuggestionsAdapter;
import com.example.user.mvvmcollabreusableapp.databinding.LayoutSearchUsersBinding;
import com.example.user.mvvmcollabreusableapp.network.model.User;
import com.example.user.mvvmcollabreusableapp.utils.Utils;
import com.pchmn.materialchips.ChipView;

import java.util.ArrayList;
import java.util.List;

public class CollaborationTagUserFragment extends BottomSheetDialogFragment {
    private ArrayList<User> users;
    private UsersSuggestionsAdapter suggestionsAdapter;
    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.length() > 0)
                searchUsers(s.toString());
            else
                suggestionsAdapter.setUsers(users);
        }
    };
    private ArrayList<ChipView> chipViews;
    private BottomSheetBehavior behavior;
    private TagUserInterface tagUserInterface;
    private LayoutSearchUsersBinding binding;
    UsersSuggestionsAdapter.OnItemClickListener onItemClickListener = new UsersSuggestionsAdapter.OnItemClickListener() {
        @Override
        public void onItemClickListener(User user) {
            ChipView chipView = new ChipView(getActivity());
            chipView.setLabel(user.getFullName());
            chipView.setTag(user.getId());
            chipView.setHasAvatarIcon(true);
            chipView.setDeletable(true);
            chipView.setOnDeleteClicked(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    binding.llTaggedUsers.removeView(chipView);
                    if (chipViews.contains(chipView.getId()))
                        chipViews.remove(chipView);

                }
            });
            binding.tvEmptyUsers.setVisibility(View.GONE);
            chipViews.add(chipView);
            binding.llTaggedUsers.addView(chipView);
        }
    };

    public static CollaborationTagUserFragment newInstance(ArrayList<User> users) {
        Bundle args = new Bundle();
        args.putParcelableArrayList("users", users);

        CollaborationTagUserFragment fragment = new CollaborationTagUserFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @BindingAdapter({"handler", "suggestionsAdapter"})
    public static void setAdapter(RecyclerView rv, CollaborationTagUserFragment frag,
                                  UsersSuggestionsAdapter usersSuggestionsAdapter) {
        LinearLayoutManager llm = new LinearLayoutManager(frag.getContext());
        rv.setLayoutManager(llm);
        rv.setAdapter(usersSuggestionsAdapter);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Resources resources = getResources();

        if (resources.getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            assert getView() != null;
            View parent = (View) getView().getParent();
            parent.setBackground(getResources().getDrawable(R.drawable.bottom_sheet_corner));
            CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) parent.getLayoutParams();
            layoutParams.height = Utils.getScreenHeight();
            layoutParams.setMargins(resources.getDimensionPixelSize(R.dimen.bottom_sheet_margin_left), 0, resources.getDimensionPixelSize(R.dimen.bottom_sheet_margin_right), 0);
            parent.setLayoutParams(layoutParams);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.BottomSheetDialog);
        return super.onCreateDialog(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                BottomSheetDialog d = (BottomSheetDialog) dialog;
                View bottomSheetInternal = d.findViewById(android.support.design.R.id.design_bottom_sheet);
                assert bottomSheetInternal != null;
                behavior = BottomSheetBehavior.from(bottomSheetInternal);
                behavior.setPeekHeight(Utils.getScreenHeight());
            }
        });
        binding = DataBindingUtil.inflate(inflater, R.layout.layout_search_users, container, false);
        binding.setLifecycleOwner(this);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        AutoCompleteTextView userSearchTerm = view.findViewById(R.id.user_search_term);
        chipViews = new ArrayList<>();

        assert getArguments() != null;
        users = getArguments().getParcelableArrayList("users");
        suggestionsAdapter = new UsersSuggestionsAdapter(getContext(), users);
        binding.setUsers(users);
        binding.setHandler(this);
        binding.setSuggestionsAdapter(suggestionsAdapter);

        suggestionsAdapter.setOnItemClickListener(onItemClickListener);

        userSearchTerm.addTextChangedListener(textWatcher);

    }

    private void searchUsers(String searchText) {
        suggestionsAdapter.setUsers(filterResults(searchText));

    }

    private List<User> filterResults(String searchText) {
        ArrayList<User> usersList = new ArrayList<>();

        for (User user : users) {
            if (user.getFullName().contains(searchText))
                usersList.add(user);
        }
        return usersList;
    }

    public void fabClick() {
        if (!chipViews.isEmpty()) {
            tagUserInterface.sendUsers(chipViews);
        }
        getDialog().dismiss();
    }

    public void setTagUserFragment(CollabCreatePostFragment collabCreatePostFragment) {
        this.tagUserInterface = collabCreatePostFragment;
    }

    public interface TagUserInterface {
        void sendUsers(List<ChipView> chipViews);
    }
}
