package com.example.securemessagingapp.activities;

import static com.example.securemessagingapp.activities.RSA.generateKeyPair;
import static com.example.securemessagingapp.activities.RSA.keyToString;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.securemessagingapp.databinding.ActivitySignUpBinding;
import com.example.securemessagingapp.utilities.Constants;
import com.example.securemessagingapp.utilities.PreferenceManager;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.security.KeyPair;
import java.util.HashMap;

public class SignUpActivity extends AppCompatActivity {

    private ActivitySignUpBinding binding;
    private PreferenceManager preferenceManager;
    private String encodedImage;
    private String privateKey,publicKey;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        setListners();
    }
    private void setListners(){

        binding.textSignIn.setOnClickListener(v -> onBackPressed());
        binding.buttonSignUp.setOnClickListener(v->{
            if(isValidSignUpDetails()){
                signUp();
            }
        });
        binding.layoutImage.setOnClickListener(v->{
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pickImage.launch(intent);
        });
    }

    private void showToast(String message){
        Toast.makeText(getApplicationContext(), message , Toast.LENGTH_SHORT).show();
    }
    private void signUp()
    {
        loading(true);
        try {

            KeyPair keyPair = generateKeyPair();
            System.out.println(keyPair.getPrivate());
            System.out.println(keyPair.getPublic());
            publicKey = keyToString(keyPair.getPublic());
            privateKey = keyToString(keyPair.getPrivate());
            System.out.println(publicKey);
            System.out.println(privateKey);
        }catch (Exception e){
            e.printStackTrace();
        }
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        HashMap<String,Object>user = new HashMap<>();
        user.put(Constants.KEY_NAME, binding.inputName.getText().toString());
        user.put(Constants.KEY_EMAIL,binding.inputEmail.getText().toString());
        user.put(Constants.KEY_PASSWORD,binding.inputPassword.getText().toString());
        user.put(Constants.KEY_IMAGE,encodedImage);
        user.put(Constants.PRIVATE_KEY,privateKey);
        user.put(Constants.PUBLIC_KEY,publicKey);

        database.collection(Constants.KEY_COLLECTION_USERS)
                .add(user)
                .addOnSuccessListener(documentReference -> {
                    loading(false);
                    preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN,true);
                    preferenceManager.putString(Constants.KEY_USER_ID,documentReference.getId());
                    preferenceManager.putString(Constants.KEY_NAME,binding.inputName.getText().toString());
                    preferenceManager.putString(Constants.KEY_IMAGE,encodedImage);
                    preferenceManager.putString(Constants.PRIVATE_KEY,privateKey);
                    preferenceManager.putString(Constants.PUBLIC_KEY,publicKey);
                    Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                })
                .addOnFailureListener(exception ->{
                    loading(false);
                    showToast(exception.getMessage());
                });
    }
    private String encodeImage(Bitmap bitmap){
        int previewWidth = 150;
        int previewHeight = bitmap.getHeight()*previewWidth/bitmap.getWidth();
        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap, previewWidth, previewHeight , false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        previewBitmap.compress(Bitmap.CompressFormat.JPEG,50,byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes,Base64.DEFAULT);
    }
    private final ActivityResultLauncher<Intent>pickImage=registerForActivityResult(
      new ActivityResultContracts.StartActivityForResult(),
      result ->{
          if(result.getResultCode()==RESULT_OK){
              if(result.getData()!=null)
              {
                  Uri imageUri = result.getData().getData();
                  try{
                      InputStream inputStream = getContentResolver().openInputStream(imageUri);
                      Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                      binding.imageProfile.setImageBitmap(bitmap);
                      binding.textAddImage.setVisibility(View.GONE);
                      encodedImage = encodeImage(bitmap);
                  }
                  catch (FileNotFoundException e){
                      e.printStackTrace();
                  }
              }
          }
      }
    );
    private Boolean isValidSignUpDetails(){
        if(encodedImage==null)
        {
            showToast("Select profile image");
            return false;
        }
        else if(binding.inputName.getText().toString().trim().isEmpty()){
            showToast("Enter name");
            return false;
        }
        else if(binding.inputName.getText().toString().matches(".*\\d.*")){
            showToast("Name contains number(s)");
            return false;
        }
        else if(binding.inputEmail.getText().toString().trim().isEmpty()){
            showToast("Enter Email");
            return false;
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(binding.inputEmail.getText().toString()).matches()){
            showToast("Enter valid Email");
            return false;
        }
        else if(binding.inputPassword.getText().toString().trim().isEmpty() || binding.inputPassword.getText().toString().length()<8 )
        {
            showToast("Password must be of minimum 8 characters");
            return false;
        }
        else if(binding.inputConfirmPassword.getText().toString().trim().isEmpty()){
            showToast("Confirm your password");
            return false;
        }
        else if(!binding.inputPassword.getText().toString().equals(binding.inputConfirmPassword.getText().toString())){
            showToast("Password & confirm password must be same");
            return false;
        }
        else{
            return true;
        }
    }

    private void loading(Boolean isloading)
    {
        if(isloading){
            binding.buttonSignUp.setVisibility(View.INVISIBLE);
            binding.progressBar.setVisibility(View.VISIBLE);
        }
        else {
            binding.progressBar.setVisibility(View.INVISIBLE);
            binding.buttonSignUp.setVisibility(View.VISIBLE);
        }
    }


}