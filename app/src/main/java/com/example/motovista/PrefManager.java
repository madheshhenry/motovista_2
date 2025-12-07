package com.example.motovista;
import android.content.Context;
import android.content.SharedPreferences;

public class PrefManager {
    private static final String PREF_NAME = "my_app_prefs";
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    public PrefManager(Context ctx) {
        prefs = ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public void saveToken(String token) { editor.putString("token", token).apply(); }
    public String getToken() { return prefs.getString("token", ""); }

    public void saveAdminName(String name) { editor.putString("admin_name", name).apply(); }
    public String getAdminName() { return prefs.getString("admin_name", ""); }
}
