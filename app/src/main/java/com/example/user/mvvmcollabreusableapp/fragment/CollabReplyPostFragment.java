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
import android.databinding.DataBindingUtil;
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
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.user.mvvmcollabreusableapp.R;
import com.example.user.mvvmcollabreusableapp.databinding.LayoutReplypostBinding;
import com.example.user.mvvmcollabreusableapp.network.model.NamesList;
import com.example.user.mvvmcollabreusableapp.network.model.User;
import com.example.user.mvvmcollabreusableapp.network.service.CollaborationAPIService;
import com.example.user.mvvmcollabreusableapp.ui.CollaborationActivity;
import com.example.user.mvvmcollabreusableapp.utils.DataUtils;
import com.example.user.mvvmcollabreusableapp.utils.NetworkUtils;
import com.example.user.mvvmcollabreusableapp.utils.Utils;
import com.pchmn.materialchips.ChipView;
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

public class CollabReplyPostFragment extends BottomSheetDialogFragment implements View.OnClickListener {
    private boolean isTagVisible;
    private int CAMERA_IMAGE_REQUEST = 100;
    private int ATTACH_FILE_REQUEST = 101;
    private Uri uri;
    private BottomSheetBehavior behavior;
    private ArrayList<Uri> uris;
    private int fileCount = 0;
    private ProgressDialog progressDialog;
    private CollaborationAPIService collaborationAPIService;
    private LayoutReplypostBinding binding;
    private CollaborationActivity context;
    private BottomSheetBehavior.BottomSheetCallback mBottomSheetBehaviorCallback = new BottomSheetBehavior.BottomSheetCallback() {
        @Override
        public void onStateChanged(@NonNull View view, int newState) {
            if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                dismiss();
                Intent intent = Objects.requireNonNull(getActivity()).getIntent();
                getActivity().finish();
                startActivity(intent);
            }
        }

        @Override
        public void onSlide(@NonNull View view, float v) {

        }
    };

    public static CollabReplyPostFragment newInstance(List<User> usersList, String parent) {
        Bundle args = new Bundle();
        args.putParcelableArrayList("users", new ArrayList<>(usersList));
        args.putString("parent", parent);

        CollabReplyPostFragment fragment = new CollabReplyPostFragment();
        fragment.setArguments(args);
        return fragment;
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
                e.printStackTrace();
            }
        }
        collaborationAPIService = NetworkUtils.provideCollaborationAPIService(getActivity(), "https://collab.");
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

        binding = DataBindingUtil.inflate(inflater, R.layout.layout_replypost, container, false);
        binding.setHandler(this);
        binding.setLifecycleOwner(this);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        progressDialog = Utils.createProgressDialog(getActivity());
        uris = new ArrayList<>();
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

    private void launchFileChooser() {
        Intent fileIntent = new Intent();
        fileIntent.setAction(Intent.ACTION_GET_CONTENT);
        fileIntent.setType("*/*");
        fileIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(Intent.createChooser(fileIntent, "Select file"), ATTACH_FILE_REQUEST);
    }

    public void enterTags() {
        if (isTagVisible) {
            binding.chipsinputTag.setVisibility(View.GONE);
            isTagVisible = false;
        } else {
            binding.chipsinputTag.setVisibility(View.VISIBLE);
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
        if (TextUtils.isEmpty(binding.edCreatepost.getText().toString().trim())) {
            binding.edCreatepost.requestFocus();
            binding.edCreatepost.setError("Text content cannot be empty");
            count++;
        }

        if (count > 0)
            return false;
        else
            return true;
    }

    private void createCollaborationRequest() {
        if (progressDialog != null)
            progressDialog.show();

        List<MultipartBody.Part> files = new ArrayList<>();
        Map<String, RequestBody> map = new HashMap<>();

        map.put("title", createPartFromString(binding.title.getText().toString()));
        map.put("text_content", createPartFromString(binding.edCreatepost.getText().toString()));
        map.put("project", createPartFromString("5bdbe97149fd0846e3099264"));
        map.put("post_type", createPartFromString("reply"));
        assert getArguments() != null;
        map.put("parent", createPartFromString(getArguments().getString("parent")));

        if (binding.chipsinputTag.getSelectedChipList().size() > 0) {
            map.put("tags", createPartFromString(getTagIds(binding.chipsinputTag.getSelectedChipList())));
            map.put("tag_length", createPartFromString(String.valueOf(binding.chipsinputTag.getSelectedChipList().size())));
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
        collaborationAPIService.collaborationParamsPost(DataUtils.getToken(getActivity()), map, files)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getReplyPostObserver());
    }

    private SingleObserver<? super Object> getReplyPostObserver() {
        return new DisposableSingleObserver<Object>() {
            @Override
            public void onSuccess(Object o) {
                if (progressDialog.isShowing())
                    progressDialog.dismiss();
                Utils.showToast(getActivity(), "Reply created successfully!");
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
                Utils.showToast(getActivity(), "Unable to process request!");
            }
        };
    }

    private String getTagIds(List<? extends ChipInterface> selectedChipList) {
        StringBuilder tags = new StringBuilder();
        int count = 0;
        for (ChipInterface chipInterface : selectedChipList) {
            tags.append(chipInterface.getId());
            if (++count < selectedChipList.size())
                tags.append(",");
        }
        return tags.toString();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            ChipView chipView = new ChipView(getActivity());
            chipView.setTag(fileCount);
            chipView.setDeletable(true);
            chipView.setHasAvatarIcon(true);
            chipView.setOnDeleteClicked(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    for (int i = 0; i < binding.containerAttachments.getChildCount(); i++) {
                        int tag = (int) binding.containerAttachments.getChildAt(i).getTag();
                        if (tag == ((int) chipView.getTag()))
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
            Utils.showToast(getActivity(), "Take Picture Failed or canceled");
        }

        if (requestCode == ATTACH_FILE_REQUEST && resultCode == Activity.RESULT_OK) {

            if (data != null && data.getClipData() != null) {
                for (int i = 0; i < data.getClipData().getItemCount(); i++) {
                    String filePath = Utils.getFilePath(data.getClipData().getItemAt(i).getUri(), getActivity());
                    addChip(filePath);
                }
            } else if (data != null && data.getData() != null) {
                String path = Utils.getFilePath(data.getData(), getActivity());
                addChip(path);
            }
        } else if (requestCode == ATTACH_FILE_REQUEST && resultCode == Activity.RESULT_CANCELED) {
            Utils.showToast(getActivity(), "Attach file Failed or canceled");
        }
    }

    private void addChip(String filePath) {
        Uri uri = Uri.fromFile(new File(filePath));
        uris.add(uri);
        ChipView chipView = new ChipView(getActivity());
        chipView.setTag(fileCount);
        chipView.setDeletable(true);
        chipView.setHasAvatarIcon(true);

        chipView.setOnDeleteClicked(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (int i = 0; i < binding.containerAttachments.getChildCount(); i++) {
                    int tag = (int) binding.containerAttachments.getChildAt(i).getTag();
                    if (tag == ((int) chipView.getTag()))
                        uris.remove(i);
                }
                binding.containerAttachments.removeView(chipView);
                if (binding.containerAttachments.getChildCount() == 0)
                    binding.chipsinputContainer.setVisibility(View.GONE);
            }
        });
        chipView.setLabel(new File(uri.toString()).getName());
        binding.chipsinputContainer.setVisibility(View.VISIBLE);
        binding.containerAttachments.addView(chipView);
        fileCount++;
    }

    private File getOutputMediaFile() {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Attachments");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs())
                mediaStorageDir.mkdirs();
        }

        @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        return new File(mediaStorageDir.getPath() + File.separator +
                "IMG_" + timeStamp + ".png");
    }

    @NonNull
    private MultipartBody.Part prepareFilePart(Uri fileUri) {
        File file = new File(Objects.requireNonNull(Utils.getFilePath(fileUri, getActivity())));
        RequestBody requestFile = RequestBody.create(MultipartBody.FORM, file);
        return MultipartBody.Part.createFormData("any", file.getName(), requestFile);
    }

    @NonNull
    private RequestBody createPartFromString(String descriptionString) {
        return RequestBody.create(MultipartBody.FORM, descriptionString);
    }
}