package com.example.user.mvvmcollabreusableapp.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.databinding.InverseBindingAdapter;
import android.databinding.InverseBindingListener;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.user.mvvmcollabreusableapp.R;
import com.example.user.mvvmcollabreusableapp.adapter.UsersSuggestionsAdapter;
import com.example.user.mvvmcollabreusableapp.constants.Constants;
import com.example.user.mvvmcollabreusableapp.databinding.LayoutCreatepostBinding;
import com.example.user.mvvmcollabreusableapp.network.model.NamesList;
import com.example.user.mvvmcollabreusableapp.network.model.User;
import com.example.user.mvvmcollabreusableapp.network.model.VisibilityModel;
import com.example.user.mvvmcollabreusableapp.network.service.CollaborationAPIService;
import com.example.user.mvvmcollabreusableapp.ui.CollaborationActivity;
import com.example.user.mvvmcollabreusableapp.utils.DataUtils;
import com.example.user.mvvmcollabreusableapp.utils.NetworkUtils;
import com.example.user.mvvmcollabreusableapp.utils.Utils;
import com.pchmn.materialchips.ChipView;
import com.pchmn.materialchips.ChipsInput;
import com.pchmn.materialchips.model.ChipInterface;

import java.io.File;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import static com.example.user.mvvmcollabreusableapp.constants.Constants.ATTACH_FILE_REQUEST;
import static com.example.user.mvvmcollabreusableapp.constants.Constants.CAMERA_IMAGE_REQUEST;

public class CollabCreatePostFragment extends BottomSheetDialogFragment implements CollaborationTagUserFragment.TagUserInterface, View.OnClickListener {
    private boolean isTagVisible;
    private Uri uri;
    private BottomSheetBehavior behavior;
    private ArrayList<Uri> uris;
    private int fileCount = 0;
    private ProgressDialog progressDialog;
    private CollaborationAPIService collaborationAPIService;
    private CollaborationActivity context;
    private UsersSuggestionsAdapter suggestionsAdapter;
    private AutoCompleteTextView userSearchTerm;
    private ArrayList<User> users;
    private LayoutCreatepostBinding binding;
    private BottomSheetBehavior.BottomSheetCallback mBottomSheetBehaviorCallback = new BottomSheetBehavior.BottomSheetCallback() {
        @Override
        public void onStateChanged(@NonNull View view, int newState) {
            if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                dismiss();
                Intent intent = Objects.requireNonNull(((Activity) context)).getIntent();
                context.finish();
                startActivityForResult(intent, 101);
            }
        }

        @Override
        public void onSlide(@NonNull View view, float v) {
            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }
    };

    public static CollabCreatePostFragment newInstance(List<User> usersList) {
        Bundle args = new Bundle();
        args.putParcelableArrayList(Constants.USERS, (new ArrayList<>(usersList)));

        CollabCreatePostFragment fragment = new CollabCreatePostFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @BindingAdapter(value = {"chipsinputVisibility", "visibilityLine", "selectedValue", "selectedValueAttrChanged"}, requireAll = false)
    public static void bindSpinnerData(Spinner pSpinner, ChipsInput chipView, View view, String newSelectedValue, final InverseBindingListener newTextAttrChanged) {
        pSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                newTextAttrChanged.onChange();
                if (adapterView.getId() == R.id.spinner_users_visibility) {
                    if (pSpinner.getSelectedItemPosition() == 3) {
                        chipView.setVisibility(View.VISIBLE);
                        view.setVisibility(View.VISIBLE);
                    } else {
                        chipView.setVisibility(View.GONE);
                        view.setVisibility(View.GONE);
                    }
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        if (newSelectedValue != null) {
            int pos = ((ArrayAdapter<String>) pSpinner.getAdapter()).getPosition(newSelectedValue);
            pSpinner.setSelection(pos, true);
        }
    }

    @InverseBindingAdapter(attribute = "selectedValue", event = "selectedValueAttrChanged")
    public static String captureSelectedValue(Spinner pAppCompatSpinner) {
        return (String) pAppCompatSpinner.getSelectedItem();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = (CollaborationActivity) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= 24) {
            try {
                Method m = StrictMode.class.getMethod(getString(R.string.disablefileuriexposure));
                m.invoke(null);
            } catch (Exception e) {
                Log.e("FileUriExposure", e.getMessage());
            }
        }
        collaborationAPIService = NetworkUtils.provideCollaborationAPIService(context, "https://collab.");
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
                behavior.setPeekHeight(Utils.getScreenHeight() / 2);
            }
        });
        VisibilityModel visibilityModel = new VisibilityModel();
        binding = DataBindingUtil.inflate(inflater, R.layout.layout_createpost, container, false);
        binding.setHandler(this);
        binding.setVisibility(visibilityModel);
        binding.setLifecycleOwner(this);
        return binding.getRoot();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        progressDialog = Utils.createProgressDialog(context);
        uris = new ArrayList<>();

        binding.containerAttachments.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);

        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.layout_search_users, null);
        RecyclerView resultsRecycler = dialogView.findViewById(R.id.users_search_results);
        userSearchTerm = dialogView.findViewById(R.id.user_search_term);
        assert getArguments() != null;
        users = getArguments().getParcelableArrayList(Constants.USERS);
        LinearLayoutManager llm = new LinearLayoutManager(context);
        resultsRecycler.setLayoutManager(llm);
        suggestionsAdapter = new UsersSuggestionsAdapter(context, users);
        resultsRecycler.setAdapter(suggestionsAdapter);

        binding.chipsinputVisibility.setFilterableList(getList());

        userSearchTerm.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //nothing to be done
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //nothing to be done
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0)
                    searchUsers(s.toString());
                else
                    suggestionsAdapter.setUsers(users);
            }
        });

        suggestionsAdapter.setOnItemClickListener(new UsersSuggestionsAdapter.OnItemClickListener() {
            @Override
            public void onItemClickListener(User user) {
                ChipView chipView = new ChipView(context);
                chipView.setLabel(user.getFullName());
                chipView.setHasAvatarIcon(true);
                chipView.setDeletable(true);
                binding.containerUserTags.addView(chipView);
                userSearchTerm.setText("");
            }
        });
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

    private List<? extends ChipInterface> getList() {
        ArrayList<NamesList> namesLists = new ArrayList<>();
        Bundle arguments = getArguments();
        assert arguments != null;
        ArrayList<User> usersList = arguments.getParcelableArrayList("users");
        assert usersList != null;
        if (!usersList.isEmpty()) {
            for (User user : usersList) {
                namesLists.add(new NamesList(user.getId(), null, user.getFullName(), null));
            }
        }
        return namesLists;
    }

    @Override
    public void onClick(View view) {
    }

    public void closePost() {
        if (behavior != null) {
            behavior.setHideable(true);
            behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        }
    }

    public void sendPost() {
        if (validatePostFields())
            createCollaborationRequest();
    }

    public void addAttachments() {
        launchFileChooser();
    }

    public void addTags() {
        CollaborationTagUserFragment collaborationTagUserFragment = CollaborationTagUserFragment.newInstance(users);
        collaborationTagUserFragment.setTagUserFragment(this);
        collaborationTagUserFragment.show(context.getSupportFragmentManager(), "Tag Users");
    }

    public void enterTags() {
        if (isTagVisible) {
            binding.chipsinputUserTags.setVisibility(View.GONE);
            isTagVisible = false;
        } else {
            binding.chipsinputUserTags.setVisibility(View.VISIBLE);
            isTagVisible = true;
        }
    }

    public void launchCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        uri = Uri.fromFile(getOutputMediaFile());
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(takePictureIntent, CAMERA_IMAGE_REQUEST);
    }

    private boolean validatePostFields() {
        int count = 0;
        if (binding.spinnerUsersVisibility.getSelectedItemPosition() == 0) {
            count++;
            binding.spinnerUsersVisibility.requestFocus();
            TextView errorText = (TextView) binding.spinnerUsersVisibility.getSelectedView();
            errorText.setError("");
            errorText.setTextColor(getResources().getColor(R.color.dark_red));
            errorText.setText(R.string.visibility);
        } else if (binding.spinnerUsersVisibility.getSelectedItem().toString().contains("Selected users")) {
            if (binding.chipsinputVisibility.getSelectedChipList().isEmpty()) {
                count++;
                Utils.showToast(context, "Visibility users cannot be empty!");
            } else if (TextUtils.isEmpty(binding.edCreatepost.getText().toString().trim())) {
                binding.edCreatepost.requestFocus();
                binding.edCreatepost.setError("Text content cannot be empty");
                count++;
            }
        } else if (TextUtils.isEmpty(binding.edCreatepost.getText().toString().trim())) {
            binding.edCreatepost.requestFocus();
            binding.edCreatepost.setError("Text content cannot be empty");
            count++;
        }

        return count <= 0;
    }

    private void createCollaborationRequest() {
        if (progressDialog != null)
            progressDialog.show();

        List<MultipartBody.Part> files = new ArrayList<>();
        Map<String, RequestBody> map = new HashMap<>();

        map.put("title", createPartFromString(binding.title.getText().toString()));
        map.put("text_content", createPartFromString(binding.edCreatepost.getText().toString()));
        map.put("project", createPartFromString("5bdbe97149fd0846e3099264"));
        map.put("post_type", createPartFromString("post"));

        if (binding.containerUserTags.getChildCount() > 0) {
            map.put("tags", createPartFromString(getTagUserIds(binding.containerUserTags)));
            map.put("tag_length", createPartFromString(String.valueOf(binding.containerUserTags.getChildCount())));
        }

        if (binding.spinnerUsersVisibility.getSelectedItemPosition() == 3) {
            if (binding.chipsinputVisibility.getSelectedChipList().isEmpty()) {
                map.put("visibility", createPartFromString("selected-users"));
                map.put("selectedUsers", createPartFromString(getVisibilityTagIds(binding.chipsinputVisibility.getSelectedChipList())));
            }
        } else {
            String visibility = null;
            if (binding.spinnerUsersVisibility.getSelectedItemPosition() == 1)
                visibility = "all-org-users";
            else if (binding.spinnerUsersVisibility.getSelectedItemPosition() == 2)
                visibility = "all-proj-users";
            map.put("visibility", createPartFromString(visibility));
        }

        if (!uris.isEmpty()) {
            for (Uri fileUri : uris) {
                files.add(prepareFilePart(fileUri));
            }
        }
        collaborationPostRequest(map, files);
    }

    @SuppressLint("LogNotTimber")
    private void collaborationPostRequest(Map<String, RequestBody> map, List<MultipartBody.Part> files) {
        collaborationAPIService.collaborationParamsPost(DataUtils.getToken(context), map, files)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getPostRequestsObserver());
    }

    private SingleObserver<? super Object> getPostRequestsObserver() {
        return new DisposableSingleObserver<Object>() {
            @Override
            public void onSuccess(Object o) {
                if (progressDialog.isShowing())
                    progressDialog.dismiss();
                Utils.showToast(context, "Post created successfully!");
                if (behavior != null) {
                    behavior.setBottomSheetCallback(mBottomSheetBehaviorCallback);
                    behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }

            @Override
            public void onError(Throwable e) {
                if (progressDialog.isShowing())
                    progressDialog.dismiss();
                Log.d("Failure:", "onFailure: " + e.getMessage());
                Utils.showToast(context, "Unable to process request!");
            }
        };
    }

    private String getVisibilityTagIds(List<? extends ChipInterface> selectedChipList) {
        StringBuilder tags = new StringBuilder();
        int count = 0;
        for (ChipInterface chipInterface : selectedChipList) {
            tags.append(chipInterface.getId());
            if (++count < selectedChipList.size())
                tags.append(",");
        }
        return tags.toString();
    }

    private String getTagUserIds(LinearLayout taggerUsersContainer) {
        StringBuilder tags = new StringBuilder();
        int count = 0;
        for (int i = 0; i < taggerUsersContainer.getChildCount(); i++) {
            String id = ((ChipView) taggerUsersContainer.getChildAt(i)).getTag().toString();
            tags.append(id);
            if (++count < taggerUsersContainer.getChildCount())
                tags.append(",");
        }
        return tags.toString();
    }

    private void launchFileChooser() {
        Intent fileIntent = new Intent();
        fileIntent.setAction(Intent.ACTION_GET_CONTENT);
        fileIntent.setType("*/*");
        fileIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(Intent.createChooser(fileIntent, "Select file"), ATTACH_FILE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            ChipView chipView = new ChipView(context);
            chipView.setTag(fileCount);
            chipView.setDeletable(true);
            chipView.setHasAvatarIcon(true);
            chipView.setOnDeleteClicked(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    for (int i = 0; i < binding.containerAttachments.getChildCount(); i++) {
                        int attachmentTag = (int) binding.containerAttachments.getChildAt(i).getTag();
                        if (attachmentTag == ((int) chipView.getTag()))
                            uris.remove(i);
                    }
                    binding.containerAttachments.removeView(chipView);
                    if (binding.containerAttachments.getChildCount() == 0)
                        binding.chipsinputContainer.setVisibility(View.GONE);
                }
            });
            chipView.setLabel(new File(uri.toString()).getName());
            uris.add(uri);
            binding.chipsinputContainer.setVisibility(View.VISIBLE);
            binding.containerAttachments.addView(chipView);
            fileCount++;
        } else if (requestCode == CAMERA_IMAGE_REQUEST && resultCode == Activity.RESULT_CANCELED) {
            Utils.showToast(context, "Take Picture Failed or canceled");
        }

        if (requestCode == ATTACH_FILE_REQUEST && resultCode == Activity.RESULT_OK) {

            if (data != null && data.getClipData() != null) {
                for (int i = 0; i < data.getClipData().getItemCount(); i++) {
                    String filePath = Utils.getFilePath(data.getClipData().getItemAt(i).getUri(), context);
                    addChip(filePath);
                }
            } else if (data != null && data.getData() != null) {
                String path = Utils.getFilePath(data.getData(), context);
                addChip(path);
            }
        } else if (requestCode == ATTACH_FILE_REQUEST && resultCode == Activity.RESULT_CANCELED) {
            Utils.showToast(context, "Attach file Failed or canceled");
        }
    }

    private void addChip(String filePath) {
        Uri fileUri = Uri.fromFile(new File(filePath));
        uris.add(fileUri);
        ChipView chipView = new ChipView(context);
        chipView.setTag(fileCount);
        chipView.setDeletable(true);
        chipView.setHasAvatarIcon(true);

        chipView.setOnDeleteClicked(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (int i = 0; i < binding.containerAttachments.getChildCount(); i++) {
                    int chipViewTag = (int) binding.containerAttachments.getChildAt(i).getTag();
                    if (chipViewTag == ((int) chipView.getTag()))
                        uris.remove(i);
                }
                binding.containerAttachments.removeView(chipView);
                if (binding.containerAttachments.getChildCount() == 0)
                    binding.chipsinputContainer.setVisibility(View.GONE);
            }
        });
        chipView.setLabel(new File(fileUri.toString()).getName());
        binding.chipsinputContainer.setVisibility(View.VISIBLE);
        binding.containerAttachments.addView(chipView);
        fileCount++;
    }

    private File getOutputMediaFile() {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Attachments");
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            mediaStorageDir.mkdirs();
        }

        @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        return new File(mediaStorageDir.getPath() + File.separator +
                "IMG_" + timeStamp + ".png");
    }

    @NonNull
    private MultipartBody.Part prepareFilePart(Uri fileUri) {
        File file = new File(Objects.requireNonNull(Utils.getFilePath(fileUri, context)));
        RequestBody requestFile = RequestBody.create(MultipartBody.FORM, file);
        return MultipartBody.Part.createFormData("any", file.getName(), requestFile);
    }

    @NonNull
    private RequestBody createPartFromString(String descriptionString) {
        return RequestBody.create(MultipartBody.FORM, descriptionString);
    }

    @Override
    public void sendUsers(List<ChipView> chipViews) {
        if (!chipViews.isEmpty()) {
            for (ChipView chipView : chipViews) {
                if (chipView != null) {
                    ViewGroup parent = (ViewGroup) chipView.getParent();
                    if (parent != null) {
                        parent.removeView(chipView);
                    }
                }
                assert chipView != null;
                chipView.setDeletable(false);
                binding.containerUserTags.addView(chipView);
            }
        }
    }
}