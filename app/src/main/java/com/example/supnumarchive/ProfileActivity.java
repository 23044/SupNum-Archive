package com.example.supnumarchive;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.supnumarchive.Databases.FirebaseProfileManager;
import com.example.supnumarchive.Models.UserProfile;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ProfileActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;

    private ImageView profileImage;
    private TextView nameTextView, emailTextView;
    private Button editProfileBtn, clearCacheBtn, clearHistoryBtn;
    private FirebaseProfileManager profileManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profileManager = new FirebaseProfileManager();

        profileImage = findViewById(R.id.profile_image);
        nameTextView = findViewById(R.id.name_text_view);
        emailTextView = findViewById(R.id.email_text_view);
        editProfileBtn = findViewById(R.id.edit_profile_btn);
        clearCacheBtn = findViewById(R.id.clear_cache_btn);
        clearHistoryBtn = findViewById(R.id.clear_history_btn);

        loadProfileData();

        editProfileBtn.setOnClickListener(v -> startActivity(new Intent(ProfileActivity.this, EditProfileDialog.class)));
        profileImage.setOnClickListener(v -> openImagePicker());
        clearCacheBtn.setOnClickListener(v -> clearCache());
        clearHistoryBtn.setOnClickListener(v -> clearHistory());

        setupMenuItems();
    }

    private void loadProfileData() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            nameTextView.setText(currentUser.getDisplayName());
            emailTextView.setText(currentUser.getEmail());

            profileManager.getProfileImageUrl().addOnSuccessListener(uri -> {
                Glide.with(this)
                        .load(uri)
                        .circleCrop()
                        .placeholder(R.drawable.ic_profile_placeholder)
                        .into(profileImage);
            });

            profileManager.getUserProfile().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    UserProfile userProfile = documentSnapshot.toObject(UserProfile.class);
                    if (userProfile != null && userProfile.getName() != null) {
                        nameTextView.setText(userProfile.getName());
                    }
                }
            });
        }
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
                byte[] imageData = baos.toByteArray();

                profileManager.uploadProfileImage(imageData)
                        .addOnSuccessListener(taskSnapshot -> {
                            loadProfileData();
                            Toast.makeText(this, "Photo de profil mise à jour", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(this, "Échec du téléchargement: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void clearCache() {
        Toast.makeText(this, "Cache vidé", Toast.LENGTH_SHORT).show();
    }

    private void clearHistory() {
        Toast.makeText(this, "Historique vidé", Toast.LENGTH_SHORT).show();
    }

    private void setupMenuItems() {
        findViewById(R.id.favorites_card).setOnClickListener(v -> openFavoritesActivity());
        findViewById(R.id.downloads_card).setOnClickListener(v -> openDownloadsActivity());
        findViewById(R.id.languages_card).setOnClickListener(v -> openLanguageSelection());
        findViewById(R.id.location_card).setOnClickListener(v -> openLocationSettings());
        findViewById(R.id.subscription_card).setOnClickListener(v -> openSubscriptionManagement());
        findViewById(R.id.display_card).setOnClickListener(v -> openDisplaySettings());
    }

    private void openFavoritesActivity() {}
    private void openDownloadsActivity() {}
    private void openLanguageSelection() {}
    private void openLocationSettings() {}
    private void openSubscriptionManagement() {}
    private void openDisplaySettings() {}

    @Override
    protected void onResume() {
        super.onResume();
        loadProfileData();
    }
}