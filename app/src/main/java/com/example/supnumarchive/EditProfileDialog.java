package com.example.supnumarchive;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.example.supnumarchive.Databases.FirebaseProfileManager;
import com.example.supnumarchive.Models.UserProfile;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class EditProfileDialog extends Dialog {
    private static final int PICK_IMAGE_REQUEST = 1001;

    private ImageView profileImage;
    private TextInputEditText nameEditText, emailEditText;
    private Button changePhotoBtn, cancelButton, saveButton;
    private FirebaseProfileManager profileManager;
    private OnProfileUpdatedListener listener;

    public interface OnProfileUpdatedListener {
        void onProfileUpdated();
    }

    public EditProfileDialog(@NonNull Context context, OnProfileUpdatedListener listener) {
        super(context);
        this.listener = listener;
        profileManager = new FirebaseProfileManager();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_edit_profile_dialog);

        if (getWindow() != null) {
            getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        }

        profileImage = findViewById(R.id.profile_image);
        nameEditText = findViewById(R.id.name_edit_text);
        emailEditText = findViewById(R.id.email_edit_text);
        changePhotoBtn = findViewById(R.id.change_photo_btn);
        cancelButton = findViewById(R.id.cancel_button);
        saveButton = findViewById(R.id.save_button);

        loadCurrentProfile();

        changePhotoBtn.setOnClickListener(v -> openImagePicker());
        cancelButton.setOnClickListener(v -> dismiss());
        saveButton.setOnClickListener(v -> updateProfile());
    }

    private void loadCurrentProfile() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            nameEditText.setText(currentUser.getDisplayName());
            emailEditText.setText(currentUser.getEmail());

            profileManager.getProfileImageUrl().addOnSuccessListener(uri -> {
                Glide.with(getContext())
                        .load(uri)
                        .circleCrop()
                        .placeholder(R.drawable.ic_profile_placeholder)
                        .into(profileImage);
            }).addOnFailureListener(e -> {
                Toast.makeText(getContext(), "Failed to load profile image", Toast.LENGTH_SHORT).show();
            });

            profileManager.getUserProfile().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    UserProfile userProfile = documentSnapshot.toObject(UserProfile.class);
                    if (userProfile != null && userProfile.getName() != null) {
                        nameEditText.setText(userProfile.getName());
                    }
                }
            });
        }
    }

    private void updateProfile() {
        String name = nameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            nameEditText.setError("Name is required");
            return;
        }

        if (TextUtils.isEmpty(email)) {
            emailEditText.setError("Email is required");
            return;
        }

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            return;
        }

        UserProfile userProfile = new UserProfile(currentUser.getUid(), name, email);

        profileManager.updateUserProfile(userProfile)
                .addOnSuccessListener(aVoid -> {
                    currentUser.updateEmail(email)
                            .addOnSuccessListener(aVoid1 -> {
                                Toast.makeText(getContext(), "Profile updated", Toast.LENGTH_SHORT).show();
                                if (listener != null) {
                                    listener.onProfileUpdated();
                                }
                                dismiss();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(getContext(), "Profile updated but failed to update email: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                if (listener != null) {
                                    listener.onProfileUpdated();
                                }
                                dismiss();
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Update failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        ((ProfileActivity) getContext()).startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    public void handleImageResult(Uri imageUri) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), imageUri);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
            byte[] imageData = baos.toByteArray();

            profileManager.uploadProfileImage(imageData)
                    .addOnSuccessListener(taskSnapshot -> {
                        Glide.with(getContext())
                                .load(taskSnapshot.getUploadSessionUri())
                                .circleCrop()
                                .into(profileImage);
                        Toast.makeText(getContext(), "Profile image updated", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Failed to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Failed to process image", Toast.LENGTH_SHORT).show();
        }
    }
}