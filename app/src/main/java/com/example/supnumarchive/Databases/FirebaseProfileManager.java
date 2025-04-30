package com.example.supnumarchive.Databases;

import android.net.Uri;

import com.example.supnumarchive.Models.UserProfile;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class FirebaseProfileManager {
    private final FirebaseFirestore db;
    private final FirebaseAuth auth;
    private final FirebaseStorage storage;

    public FirebaseProfileManager() {
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
    }

    // Récupérer le profil utilisateur
    public Task<DocumentSnapshot> getUserProfile() {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            return null;
        }

        return db.collection("users").document(currentUser.getUid()).get();
    }

    // Mettre à jour le profil utilisateur
    public Task<Void> updateUserProfile(UserProfile userProfile) {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            return null;
        }

        Map<String, Object> profileData = new HashMap<>();
        profileData.put("name", userProfile.getName());
        profileData.put("email", userProfile.getEmail());
        profileData.put("language", userProfile.getLanguage());
        profileData.put("location", userProfile.getLocation());
        profileData.put("subscriptionType", userProfile.getSubscriptionType());

        return db.collection("users").document(currentUser.getUid()).update(profileData);
    }

    // Télécharger une image de profil
    public UploadTask uploadProfileImage(byte[] imageData) {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            return null;
        }

        StorageReference profileImageRef = storage.getReference()
                .child("profile_images")
                .child(currentUser.getUid() + ".jpg");

        return profileImageRef.putBytes(imageData);
    }

    // Obtenir l'URL de l'image de profil
    public Task<Uri> getProfileImageUrl() {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            return null;
        }

        StorageReference profileImageRef = storage.getReference()
                .child("profile_images")
                .child(currentUser.getUid() + ".jpg");

        return profileImageRef.getDownloadUrl();
    }

    // Mettre à jour la préférence de langue
    public Task<Void> updateLanguagePreference(String language) {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            return null;
        }

        return db.collection("users").document(currentUser.getUid())
                .update("language", language);
    }

    // Mettre à jour la localisation
    public Task<Void> updateLocation(String location) {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            return null;
        }

        return db.collection("users").document(currentUser.getUid())
                .update("location", location);
    }

    // Mettre à jour l'abonnement
    public Task<Void> updateSubscription(String subscriptionType) {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            return null;
        }

        return db.collection("users").document(currentUser.getUid())
                .update("subscriptionType", subscriptionType);
    }
}