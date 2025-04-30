package com.example.supnumarchive.Models;
public class UserProfile {
    private String userId;
    private String name;
    private String email;
    private String profileImageUrl;
    private String language;
    private String location;
    private String subscriptionType;

    // Constructeur vide requis pour Firestore
    public UserProfile() {}

    public UserProfile(String userId, String name, String email) {
        this.userId = userId;
        this.name = name;
        this.email = email;
    }

    // Getters et Setters
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getProfileImageUrl() { return profileImageUrl; }
    public void setProfileImageUrl(String profileImageUrl) { this.profileImageUrl = profileImageUrl; }

    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getSubscriptionType() { return subscriptionType; }
    public void setSubscriptionType(String subscriptionType) { this.subscriptionType = subscriptionType; }
}