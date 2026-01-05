package com.example.motovista_deep.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.motovista_deep.models.User;
import com.google.gson.Gson;

public class SharedPrefManager {
    private static final String PREF_NAME = "motovista_prefs";
    private static SharedPrefManager instance;
    private final SharedPreferences prefs;
    private final Gson gson = new Gson();

    private SharedPrefManager(Context ctx) {
        prefs = ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized SharedPrefManager getInstance(Context ctx) {
        if (instance == null) instance = new SharedPrefManager(ctx.getApplicationContext());
        return instance;
    }

    // Save Customer Session
    public void saveCustomerLogin(User user, String token) {
        saveLogin(user, token, "customer");
    }

    // Save Admin Session
    public void saveAdminLogin(User user, String token) {
        saveLogin(user, token, "admin");
    }

    private void saveLogin(User user, String token, String role) {
        prefs.edit()
                .putString("user", gson.toJson(user))
                .putString("token", token)
                .putString("role", role)
                .putBoolean("is_logged_in", true)
                .apply();
    }

    public User getUser() {
        String json = prefs.getString("user", null);
        return json == null ? null : gson.fromJson(json, User.class);
    }

    // ✅ FIXED: Check if user is "Old" (Completed)
    public boolean isProfileCompleted() {
        User user = getUser();
        return (user != null && user.isIs_profile_completed());
    }

    public String getUserName() {
        User user = getUser();
        return (user != null) ? user.getFull_name() : "";
    }

    public String getUserEmail() {
        User user = getUser();
        return (user != null) ? user.getEmail() : "";
    }

    public String getUserPhone() {
        User user = getUser();
        return (user != null) ? user.getPhone() : "";
    }

    // ✅ ADDED: Explicit Setters to fix "Symbol Not Found" error
    public void setUserName(String name) {
        User user = getUser();
        if (user != null) {
            user.setFull_name(name);
            saveUserToPrefs(user);
        }
    }

    public void setUserEmail(String email) {
        User user = getUser();
        if (user != null) {
            user.setEmail(email);
            saveUserToPrefs(user);
        }
    }

    public void setUserPhone(String phone) {
        User user = getUser();
        if (user != null) {
            user.setPhone(phone);
            saveUserToPrefs(user);
        }
    }

    // ✅ Logic to mark user as "Old" locally after setup
    public void setProfileCompleted(boolean status) {
        User user = getUser();
        if (user != null) {
            user.setIs_profile_completed(status);
            saveUserToPrefs(user);
        }
    }

    // Helper to update the whole user object
    public void updateUser(User updatedUser) {
        if (updatedUser != null) {
            saveUserToPrefs(updatedUser);
        }
    }

    // Private internal save method
    private void saveUserToPrefs(User user) {
        prefs.edit().putString("user", gson.toJson(user)).apply();
    }

    public String getToken() {
        return prefs.getString("token", null);
    }

    public String getRole() {
        return prefs.getString("role", "");
    }

    public boolean isLoggedIn() {
        return prefs.getBoolean("is_logged_in", false);
    }

    public void clear() {
        prefs.edit().clear().apply();
    }

    public User getCustomer() {
        return getUser();
    }
}