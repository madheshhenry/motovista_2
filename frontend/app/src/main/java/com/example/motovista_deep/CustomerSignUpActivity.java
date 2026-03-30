package com.example.motovista_deep;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.text.TextUtils;
import com.example.motovista_deep.helpers.SharedPrefManager;
import androidx.appcompat.app.AppCompatActivity;

import com.example.motovista_deep.api.ApiService;
import com.example.motovista_deep.api.RetrofitClient;
import com.example.motovista_deep.models.RegisterRequest;
import com.example.motovista_deep.models.RegisterResponse;
import com.example.motovista_deep.models.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomerSignUpActivity extends AppCompatActivity {

    private EditText etFullName, etEmail, etPhone, etPassword, etConfirmPassword;
    private CheckBox cbTerms;
    private Button btnSignUp;
    private ImageView btnBack, btnTogglePassword, btnToggleConfirmPassword;
    private TextView tvLoginLink;

    private boolean isPasswordVisible = false;
    private boolean isConfirmPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_signup);

        initializeViews();
        setupClickListeners();
        setupLoginLink();
    }

    private void initializeViews() {
        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        cbTerms = findViewById(R.id.cbTerms);
        btnSignUp = findViewById(R.id.btnSignUp);
        btnBack = findViewById(R.id.btnBack);
        btnTogglePassword = findViewById(R.id.btnTogglePassword);
        btnToggleConfirmPassword = findViewById(R.id.btnToggleConfirmPassword);
        tvLoginLink = findViewById(R.id.tvLoginLink);
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btnTogglePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePasswordVisibility();
            }
        });

        btnToggleConfirmPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleConfirmPasswordVisibility();
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptSignUp();
            }
        });

        View termsContainer = findViewById(R.id.termsContainer);
        termsContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cbTerms.setChecked(!cbTerms.isChecked());
            }
        });
    }

    private void togglePasswordVisibility() {
        if (isPasswordVisible) {
            etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            btnTogglePassword.setImageResource(R.drawable.ic_visibility_off);
        } else {
            etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            btnTogglePassword.setImageResource(R.drawable.ic_visibility);
        }
        isPasswordVisible = !isPasswordVisible;
        etPassword.setSelection(etPassword.getText().length());
    }

    private void toggleConfirmPasswordVisibility() {
        if (isConfirmPasswordVisible) {
            etConfirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            btnToggleConfirmPassword.setImageResource(R.drawable.ic_visibility_off);
        } else {
            etConfirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            btnToggleConfirmPassword.setImageResource(R.drawable.ic_visibility);
        }
        isConfirmPasswordVisible = !isConfirmPasswordVisible;
        etConfirmPassword.setSelection(etConfirmPassword.getText().length());
    }

    private void setupLoginLink() {
        String text = "Already have an account? Log in";
        SpannableString spannableString = new SpannableString(text);

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                finish();
            }
        };

        int startIndex = text.indexOf("Log in");
        int endIndex = startIndex + "Log in".length();
        spannableString.setSpan(clickableSpan, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        tvLoginLink.setText(spannableString);
        tvLoginLink.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void attemptSignUp() {
        String fullName = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();
        boolean agreedToTerms = cbTerms.isChecked();

        if (TextUtils.isEmpty(fullName)) {
            etFullName.setError("Full name is required");
            etFullName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Email is required");
            etEmail.requestFocus();
            return;
        }

        if (!isValidEmail(email)) {
            etEmail.setError("Enter a valid email address");
            etEmail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(phone)) {
            etPhone.setError("Phone number is required");
            etPhone.requestFocus();
            return;
        }

        if (phone.length() < 10) {
            etPhone.setError("Enter a valid phone number");
            etPhone.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Password is required");
            etPassword.requestFocus();
            return;
        }

        if (password.length() < 8) {
            etPassword.setError("Password must be at least 8 characters");
            etPassword.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            etConfirmPassword.setError("Please confirm your password");
            etConfirmPassword.requestFocus();
            return;
        }

        if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError("Passwords do not match");
            etConfirmPassword.requestFocus();
            return;
        }

        if (!agreedToTerms) {
            Toast.makeText(this, "Please agree to Terms & Conditions", Toast.LENGTH_SHORT).show();
            return;
        }

        performSignUp(fullName, email, phone, password);
    }

    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void performSignUp(String fullName, String email, String phone, String password) {
        btnSignUp.setText("Creating account...");
        btnSignUp.setEnabled(false);

        RegisterRequest request = new RegisterRequest(fullName, email, phone, password);
        ApiService api = RetrofitClient.getApiService();
        Call<RegisterResponse> call = api.register(request);

        call.enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                btnSignUp.setText("Sign Up");
                btnSignUp.setEnabled(true);

                if (response.isSuccessful() && response.body() != null) {
                    RegisterResponse res = response.body();

                    // Check if registration was successful
                    if (res.isSuccess()) {
                        
                        // DO NOT Auto Login - Flow: Register -> Verify -> Login
                        
                        String message = "Sign up successful!";
                        if (res.getData().isRequires_verification()) {
                            message += " Please check your email for verification link.";
                        }
                        
                        Toast.makeText(CustomerSignUpActivity.this, message, Toast.LENGTH_LONG).show();

                        // Navigate back to Login Screen
                        // Assuming this activity was started from Login, finish() brings us back.
                        // Or we can explicitly start LoginActivity.
                        Intent intent = new Intent(CustomerSignUpActivity.this, CustomerLoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();

                    } else {
                        // Show error message from PHP backend
                        Toast.makeText(CustomerSignUpActivity.this,
                                res.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                } else {
                    // Handle server errors
                    String errorMsg = "Signup failed";
                    if (response.code() == 400) {
                        errorMsg = "Invalid data. Please check your inputs.";
                    } else if (response.code() == 409) {
                        errorMsg = "Email already registered";
                    } else if (response.code() == 500) {
                        errorMsg = "Server error. Please try again later.";
                    }
                    Toast.makeText(CustomerSignUpActivity.this,
                            errorMsg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RegisterResponse> call, Throwable t) {
                btnSignUp.setText("Sign Up");
                btnSignUp.setEnabled(true);
                Toast.makeText(CustomerSignUpActivity.this,
                        "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}