package com.example.securemessagingapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.securemessagingapp.R;
import com.example.securemessagingapp.databinding.ActivitySignInBinding;
import com.google.firebase.Firebase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class SignInActivity extends AppCompatActivity {
    private ActivitySignInBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListners();
    }
    private void setListners(){
        binding.textCreateNewAccount.setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(),SignUpActivity.class)));
//        binding.buttonSignIn.setOnClickListener(v-> addDataToFirebase());
    }

//    private void addDataToFirebase(){
//        FirebaseFirestore database = FirebaseFirestore.getInstance();
//        HashMap<String, Object> data = new HashMap<>();
//        data.put("first_name","Lucifer");
//        data.put("last_name","Mornigstar");
//        database.collection("users")
//                .add(data)
//                .addOnSuccessListener(documentReference -> {
//                    Toast.makeText(getApplicationContext(),"Data Inserted",Toast.LENGTH_SHORT).show();
//                })
//                .addOnFailureListener(Exception ->{
//                    Toast.makeText(getApplicationContext(),Exception.getMessage(),Toast.LENGTH_SHORT).show();
//                });
//    }
}