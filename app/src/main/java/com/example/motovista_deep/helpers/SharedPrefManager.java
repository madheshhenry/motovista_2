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

    // ✅ SAVE CUSTOMER LOGIN
    public void saveCustomerLogin(User user, String token) {
        prefs.edit()
                .putString("user", gson.toJson(user))
                .putString("token", token)
                .putString("role", "customer")
                .putBoolean("is_logged_in", true)
                .apply();
    }

    // ✅ SAVE ADMIN LOGIN
    public void saveAdminLogin(String username, int adminId, String token) {
        AdminUser admin = new AdminUser(adminId, username);

        prefs.edit()
                .putString("admin", gson.toJson(admin))
                .putString("token", token)
                .putString("role", "admin")
                .putBoolean("is_logged_in", true)
                .apply();
    }

    // ✅ GET CUSTOMER DATA
    public User getCustomer() {
        String json = prefs.getString("user", null);
        return json == null ? null : gson.fromJson(json, User.class);
    }

    // ✅ GET ADMIN DATA
    public AdminUser getAdmin() {
        String json = prefs.getString("admin", null);
        return json == null ? null : gson.fromJson(json, AdminUser.class);
    }

    public String getToken() {
        return prefs.getString("token", null);
    }

    public boolean isLoggedIn() {
        return prefs.getBoolean("is_logged_in", false);
    }

    public String getRole() {
        return prefs.getString("role", null);
    }

    public void clear() {
        prefs.edit().clear().apply();
    }

    // INTERNAL CLASS FOR ADMIN DATA
    public static class AdminUser {
        public int id;
        public String username;

        public AdminUser(int id, String username) {
            this.id = id;
            this.username = username;
        }
    }
}
