package com.example.motovista_deep.helpers;

import android.content.Context;
import android.content.SharedPreferences;

public class OrderSessionManager {
    private static final String PREF_NAME = "OrderSessionPref";
    private static final String KEY_REQUEST_ID = "current_request_id";
    private static final String KEY_CURRENT_STEP = "current_step";

    public enum Step {
        NONE,
        PAYMENT_CONFIRMED,
        DOCUMENTS,
        COMPLETED
    }

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context context;

    public OrderSessionManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public void startSession(int requestId) {
        editor.putInt(KEY_REQUEST_ID, requestId);
        editor.putString(KEY_CURRENT_STEP, Step.PAYMENT_CONFIRMED.name());
        editor.apply();
    }

    public void setStep(Step step) {
        editor.putString(KEY_CURRENT_STEP, step.name());
        editor.apply();
    }

    public void clearSession() {
        editor.clear();
        editor.apply();
    }

    public int getRequestId() {
        return pref.getInt(KEY_REQUEST_ID, -1);
    }

    public Step getCurrentStep() {
        String stepName = pref.getString(KEY_CURRENT_STEP, Step.NONE.name());
        return Step.valueOf(stepName);
    }

    public boolean isSessionActive() {
        return getRequestId() != -1 && getCurrentStep() != Step.NONE;
    }
}
