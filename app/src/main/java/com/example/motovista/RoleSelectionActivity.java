package com.example.motovista;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RoleSelectionActivity extends AppCompatActivity {

    LinearLayout customerCard, adminCard;
    Button btnContinue;
    String selectedRole = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_role_selection);

        // FIND VIEWS
        customerCard = findViewById(R.id.customerCard);
        adminCard = findViewById(R.id.adminCard);
        btnContinue = findViewById(R.id.btnContinue);

        // LOAD CARD SELECTION ANIMATION
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.card_select_scale);

        // CUSTOMER SELECT
        customerCard.setOnClickListener(v -> {
            selectedRole = "customer";

            customerCard.startAnimation(anim); // animation
            customerCard.setBackgroundResource(R.drawable.role_card_selected);
            adminCard.setBackgroundResource(R.drawable.role_card_normal);

            btnContinue.setVisibility(Button.VISIBLE);
        });

        // ADMIN SELECT
        adminCard.setOnClickListener(v -> {
            selectedRole = "admin";

            adminCard.startAnimation(anim); // animation
            adminCard.setBackgroundResource(R.drawable.role_card_selected);
            customerCard.setBackgroundResource(R.drawable.role_card_normal);

            btnContinue.setVisibility(Button.VISIBLE);
        });

        // CONTINUE BUTTON ACTION
        btnContinue.setOnClickListener(v -> {
            if (selectedRole.equals("customer")) {
                startActivity(new Intent(RoleSelectionActivity.this, CustomerLoginActivity.class));
            } else if (selectedRole.equals("admin")) {
                startActivity(new Intent(RoleSelectionActivity.this, AdminLoginActivity.class));
            }
        });btnContinue.setOnClickListener(v -> {

            if (selectedRole.equals("customer")) {
                Intent i = new Intent(RoleSelectionActivity.this, LoginActivity.class);
                startActivity(i);
            }
            else if (selectedRole.equals("admin")) {
                Intent i = new Intent(RoleSelectionActivity.this, AdminLoginActivity.class);
                startActivity(i);
            }
            else {
                Toast.makeText(this, "Please select a role", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
