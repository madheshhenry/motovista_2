package com.example.motovista_deep;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.example.motovista_deep.api.RetrofitClient;
import com.example.motovista_deep.helpers.SharedPrefManager;
import com.example.motovista_deep.models.LoginResponse;
import com.example.motovista_deep.models.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RoleSelectionActivity extends AppCompatActivity {

    private CardView cardCustomer, cardAdmin;
    private ImageView ivCustomerSelected, ivAdminSelected;
    private Button btnContinue;
    private String selectedRole = "";
    private ConstraintLayout rootLayout;

    // Google Sign-In
    private static final int RC_SIGN_IN = 9001;
    private GoogleSignInClient mGoogleSignInClient;
    private LinearLayout googleSection;
    private MaterialButton btnGoogleContinue;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // STEP 1: MAKE FULL SCREEN - EDGE TO EDGE
        makeFullScreen();

        setContentView(R.layout.activity_role_selection);

        // Configure Google Sign-In
        // IMPORTANT: Web Client ID is required for backend verification.
        // Replace "YOUR_WEB_CLIENT_ID.apps.googleusercontent.com" with the real one from Firebase Console.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken("11624205114-4qtbdbv7oo5k81kmjc5tn3ig7du6iu7r.apps.googleusercontent.com")
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // STEP 2: INITIALIZE VIEWS
        initViews();

        // STEP 3: ADJUST FOR SYSTEM BARS (Notch, Navigation Bar)
        adjustForSystemBars();

        // STEP 4: SETUP CLICK LISTENERS
        setupClickListeners();
    }

    private void makeFullScreen() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            WindowManager.LayoutParams params = getWindow().getAttributes();
            params.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            getWindow().setAttributes(params);
        }

        hideNavigationBar();
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        getWindow().setNavigationBarColor(Color.TRANSPARENT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowInsetsControllerCompat controller = WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
            controller.hide(WindowInsetsCompat.Type.systemBars());
            controller.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
        }
    }

    private void hideNavigationBar() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideNavigationBar();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideNavigationBar();
        }
    }

    private void initViews() {
        cardCustomer = findViewById(R.id.card_customer);
        cardAdmin = findViewById(R.id.card_admin);
        ivCustomerSelected = findViewById(R.id.iv_customer_selected);
        ivAdminSelected = findViewById(R.id.iv_admin_selected);
        btnContinue = findViewById(R.id.btn_continue);
        rootLayout = findViewById(R.id.root_layout);
        
        // Google Section
        googleSection = findViewById(R.id.google_login_section);
        btnGoogleContinue = findViewById(R.id.btn_google_continue);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Signing in with Google...");
        progressDialog.setCancelable(false);

        // Set button initially disabled with lower opacity
        btnContinue.setEnabled(false);
        btnContinue.setAlpha(0.5f);
    }

    private void adjustForSystemBars() {
        rootLayout.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        rootLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        applySystemBarPadding();
                    }
                }
        );
    }

    private void applySystemBarPadding() {
        int statusBarHeight = 0;
        int navigationBarHeight = 0;

        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) statusBarHeight = getResources().getDimensionPixelSize(resourceId);

        resourceId = getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) navigationBarHeight = getResources().getDimensionPixelSize(resourceId);

        rootLayout.setPadding(rootLayout.getPaddingLeft(), statusBarHeight, rootLayout.getPaddingRight(), navigationBarHeight);
    }

    private void setupClickListeners() {
        cardCustomer.setOnClickListener(v -> selectRole("customer"));
        cardAdmin.setOnClickListener(v -> selectRole("admin"));

        btnContinue.setOnClickListener(v -> {
            if (!selectedRole.isEmpty()) {
                navigateToNextScreen();
            }
        });

        btnGoogleContinue.setOnClickListener(v -> signInWithGoogle());
    }

    private void signInWithGoogle() {
        // 🚀 Force Account Selection (Clear previous cache)
        mGoogleSignInClient.signOut().addOnCompleteListener(this, task -> {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleGoogleSignInResult(task);
        }
    }

    private void handleGoogleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            String idToken = account.getIdToken();
            if (idToken != null) {
                performGoogleLogin(idToken);
            } else {
                Toast.makeText(this, "Failed to get Google ID token.", Toast.LENGTH_LONG).show();
            }
        } catch (ApiException e) {
            Log.e("GOOGLE_AUTH", "signInResult:failed code=" + e.getStatusCode());
            Toast.makeText(this, "Google Sign-In failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void performGoogleLogin(String idToken) {
        progressDialog.show();
        Map<String, String> body = new HashMap<>();
        body.put("id_token", idToken);

        RetrofitClient.getApiService().googleLogin(body).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                progressDialog.dismiss();
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();
                    
                    if (loginResponse.isSuccess()) {
                        LoginResponse.LoginData data = loginResponse.getData();
                        
                        // 🚀 Step 1: Handle OTP Verification requirement
                        if (data != null && data.isRequires_verification()) {
                            Intent intent = new Intent(RoleSelectionActivity.this, VerifyOtpActivity.class);
                            intent.putExtra("email", data.getEmail());
                            intent.putExtra("flow", "google_login");
                            startActivity(intent);
                            return;
                        }

                        // 🚀 Step 2: Normal Login Flow (after verification)
                        if (data != null && data.getCustomer() != null && data.getToken() != null) {
                            User user = data.getCustomer();
                            String token = data.getToken();

                            SharedPrefManager.getInstance(RoleSelectionActivity.this).saveCustomerLogin(user, token);
                            Toast.makeText(RoleSelectionActivity.this, "Welcome, " + user.getFull_name(), Toast.LENGTH_SHORT).show();

                            Intent intent;
                            if (!user.isIs_profile_completed()) {
                                intent = new Intent(RoleSelectionActivity.this, CustomerProfileActivity.class);
                            } else {
                                intent = new Intent(RoleSelectionActivity.this, CustomerHomeActivity.class);
                            }
                            startActivity(intent);
                            finish();
                        }
                    } else {
                        Toast.makeText(RoleSelectionActivity.this, loginResponse.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    String errorMsg = "Google Login Failed: Server Error";
                    try {
                        if (response.errorBody() != null) {
                            errorMsg = "Server Error: " + response.errorBody().string();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(RoleSelectionActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(RoleSelectionActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void selectRole(String role) {
        resetSelections();
        selectedRole = role;

        if (role.equals("customer")) {
            cardCustomer.setCardBackgroundColor(getResources().getColor(R.color.primary_light));
            cardCustomer.setCardElevation(12f);
            ivCustomerSelected.setVisibility(View.VISIBLE);
            googleSection.setVisibility(View.VISIBLE);

            if (SharedPrefManager.getInstance(this).isLoggedIn() && "customer".equals(SharedPrefManager.getInstance(this).getRole())) {
                btnContinue.setEnabled(true);
                btnContinue.setAlpha(1f);
                btnContinue.setText("Continue as Customer");
            }
        } else if (role.equals("admin")) {
            cardAdmin.setCardBackgroundColor(getResources().getColor(R.color.primary_light));
            cardAdmin.setCardElevation(12f);
            ivAdminSelected.setVisibility(View.VISIBLE);
            googleSection.setVisibility(View.GONE);

            if (SharedPrefManager.getInstance(this).isLoggedIn() && "admin".equals(SharedPrefManager.getInstance(this).getRole())) {
                btnContinue.setEnabled(true);
                btnContinue.setAlpha(1f);
                btnContinue.setText("Continue as Admin");
            }
        }

        if (!btnContinue.isEnabled()) {
            btnContinue.setEnabled(true);
            btnContinue.setAlpha(1f);
        }
    }

    private void resetSelections() {
        cardCustomer.setCardBackgroundColor(getResources().getColor(android.R.color.white));
        cardCustomer.setCardElevation(6f);
        ivCustomerSelected.setVisibility(View.GONE);

        cardAdmin.setCardBackgroundColor(getResources().getColor(android.R.color.white));
        cardAdmin.setCardElevation(6f);
        ivAdminSelected.setVisibility(View.GONE);
    }

    private void navigateToNextScreen() {
        Intent intent;
        SharedPrefManager prefManager = SharedPrefManager.getInstance(this);

        if (selectedRole.equals("customer")) {
            if (prefManager.isLoggedIn() && "customer".equals(prefManager.getRole())) {
                if (prefManager.isProfileCompleted()) {
                    intent = new Intent(this, CustomerHomeActivity.class);
                } else {
                    intent = new Intent(this, CustomerProfileActivity.class);
                }
            } else {
                intent = new Intent(this, CustomerLoginActivity.class);
            }
        } else {
            if (prefManager.isLoggedIn() && "admin".equals(prefManager.getRole())) {
                intent = new Intent(this, AdminDashboardActivity.class);
            } else {
                intent = new Intent(this, AdminLoginActivity.class);
            }
        }

        intent.putExtra("SELECTED_ROLE", selectedRole);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }
}