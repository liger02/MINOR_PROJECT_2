package com.example.securemessagingapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.securemessagingapp.R;
import com.example.securemessagingapp.databinding.ActivitySignInBinding;
import com.example.securemessagingapp.databinding.ActivitySignUpBinding;

public class SignUpActivity extends AppCompatActivity {

    private ActivitySignUpBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListners();
    }
    private void setListners(){
        binding.textSignIn.setOnClickListener(v -> onBackPressed());
    }
}