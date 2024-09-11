package com.example.project.Login;

import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.project.R;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class SignUpActivity extends AppCompatActivity {
    private static final String TAG = "SignUpActivity";

    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private FirebaseUser user;
    private StorageReference storageRef;
    private DatabaseReference databaseRef;
    private Uri imgUri;

    // 이메일 검증
    private boolean verifyEmail = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        auth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        storageRef = FirebaseStorage.getInstance().getReference();
        databaseRef = FirebaseDatabase.getInstance().getReference();

    }

    protected void init(){}

    private void signUp(){}

    private void showToast(String msg){Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();}

}