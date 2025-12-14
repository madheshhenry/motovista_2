package com.example.motovista_deep;
import com.example.motovista_deep.api.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.example.motovista_deep.models.User;

import androidx.appcompat.app.AppCompatActivity;
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
import com.example.motovista_deep.models.RegisterResponse;
import com.example.motovista_deep.models.RegisterRequest;
import com.example.motovista_deep.helpers.SharedPrefManager;
import com.example.motovista_deep.api.ApiService;



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

        // Initialize views
        initializeViews();

        // Setup click listeners
        setupClickListeners();

        // Setup login link with clickable span
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
        // Back button
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // Toggle password visibility
        btnTogglePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePasswordVisibility();
            }
        });

        // Toggle confirm password visibility
        btnToggleConfirmPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleConfirmPasswordVisibility();
            }
        });

        // Sign Up button
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptSignUp();
            }
        });

        // Terms checkbox - make whole text clickable
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
            // Hide password
            etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            btnTogglePassword.setImageResource(R.drawable.ic_visibility_off);
            etPassword.setSelection(etPassword.getText().length());
        } else {
            // Show password
            etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            btnTogglePassword.setImageResource(R.drawable.ic_visibility);
            etPassword.setSelection(etPassword.getText().length());
        }
        isPasswordVisible = !isPasswordVisible;
    }

    private void toggleConfirmPasswordVisibility() {
        if (isConfirmPasswordVisible) {
            // Hide password
            etConfirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            btnToggleConfirmPassword.setImageResource(R.drawable.ic_visibility_off);
            etConfirmPassword.setSelection(etConfirmPassword.getText().length());
        } else {
            // Show password
            etConfirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            btnToggleConfirmPassword.setImageResource(R.drawable.ic_visibility);
            etConfirmPassword.setSelection(etConfirmPassword.getText().length());
        }
        isConfirmPasswordVisible = !isConfirmPasswordVisible;
    }

    private void setupLoginLink() {
        String text = "Already have an account? Log in";
        SpannableString spannableString = new SpannableString(text);

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                // Navigate back to login screen
                finish();
            }
        };

        // Make "Log in" clickable
        int startIndex = text.indexOf("Log in");
        int endIndex = startIndex + "Log in".length();
        spannableString.setSpan(clickableSpan, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        tvLoginLink.setText(spannableString);
        tvLoginLink.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void attemptSignUp() {
        // Get input values
        String fullName = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();
        boolean agreedToTerms = cbTerms.isChecked();

        // Validate inputs
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

        // All validations passed - Proceed with sign up
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

                    if ("success".equals(res.getStatus())) {

                        User customer = res.getData().getCustomer();
                        String token = res.getData().getToken();

                        SharedPrefManager.getInstance(CustomerSignUpActivity.this)
                                .saveCustomerLogin(customer, token);

                        Toast.makeText(CustomerSignUpActivity.this,
                                "Sign up successful!", Toast.LENGTH_SHORT).show();

                        startActivity(new Intent(CustomerSignUpActivity.this,
                                CustomerProfileActivity.class));
                        finish();

                    } else {
                        Toast.makeText(CustomerSignUpActivity.this,
                                res.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(CustomerSignUpActivity.this,
                            "Signup failed (Server Error).", Toast.LENGTH_SHORT).show();
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