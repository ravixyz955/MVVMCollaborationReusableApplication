package com.example.user.mvvmcollabreusableapp.ui;

import android.Manifest;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.pm.PackageManager;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.user.mvvmcollabreusableapp.R;
import com.example.user.mvvmcollabreusableapp.adapter.CollaborationAdapter;
import com.example.user.mvvmcollabreusableapp.databinding.ActivityCollaborationBinding;
import com.example.user.mvvmcollabreusableapp.fragment.CollabCreatePostFragment;
import com.example.user.mvvmcollabreusableapp.network.model.AllUsers;
import com.example.user.mvvmcollabreusableapp.network.model.User;
import com.example.user.mvvmcollabreusableapp.viewmodel.CollabViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CollaborationActivity extends AppCompatActivity {
    private static final int PERMISSION_ALL = 1;
    private List<AllUsers.Users> users;
    private CollabViewModel collabViewModel;
    private ActivityCollaborationBinding binding;

    @BindingAdapter({"handler"})
    public static void setHandler(final ViewPager viewPager, final CollaborationActivity activity) {
        final CollaborationAdapter adapter = new CollaborationAdapter(viewPager.getContext(), activity.getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(3);
    }

    @BindingAdapter({"pager"})
    public static void setPager(final TabLayout view, final ViewPager pagerView) {
        view.setupWithViewPager(pagerView, true);
    }

    @BindingAdapter("isLoading")
    public static void setVisibleGone(FrameLayout frameLayout, boolean visibile) {
        frameLayout.setVisibility(visibile ? View.GONE : View.VISIBLE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_collaboration);
        collabViewModel = ViewModelProviders.of(this).get(CollabViewModel.class);
        binding.setCollabviewModel(collabViewModel);
        binding.setHandler(this);
        binding.setManager(getSupportFragmentManager());
        binding.setLifecycleOwner(this);
        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);

        String[] permissions = new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE
        };

        if (!hasPermissions(this, permissions)) {
            ActivityCompat.requestPermissions(this, permissions, PERMISSION_ALL);
        } else {
            setUsersList();
        }
    }

    public void fabClick() {
        CollabCreatePostFragment collabCreatePostFragment = CollabCreatePostFragment.newInstance(getUsersList());
        collabCreatePostFragment.show(getSupportFragmentManager(), "createpost");

    }

    private void setUsersList() {
        collabViewModel.getUsers().observe(this, new Observer<List<AllUsers.Users>>() {
            @Override
            public void onChanged(@Nullable List<AllUsers.Users> users) {
                binding.setIsLoading(true);
                if (users != null && !users.isEmpty()) {
                    setUsers(users);
                }
            }
        });
    }

    public List<AllUsers.Users> getUsers() {
        return users;
    }

    public void setUsers(List<AllUsers.Users> users) {
        this.users = users;
    }

    private boolean hasPermissions(Context context, String[] permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == PERMISSION_ALL) {
            if (grantResults.length > 0) {
                setUsersList();
            } else {
                Toast.makeText(CollaborationActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public List<User> getUsersList() {
        if (getUsers() != null) {
            List<AllUsers.Users> collabUsers = getUsers();
            ArrayList<User> usersList;
            if (!collabUsers.isEmpty()) {
                usersList = new ArrayList<>();
                for (int i = 0; i < collabUsers.size(); i++) {
                    User user = new User();
                /*    if (!DataUtils.getEmail(this).equalsIgnoreCase(collabUsers.get(i).getEmail()) &&
                            collabUsers.get(i).getEmail() != null) {*/
                    if (collabUsers.get(i).getEmail() != null) {
                        Log.d("SDERF", "getUsersList: ");
                        user.setId(collabUsers.get(i).get_id());
                        user.setFullName(collabUsers.get(i).getEmail().replaceAll("@.*", ""));
                        usersList.add(user);
                    }
                }
                return usersList;
            }
        }
        return Collections.emptyList();
    }
}