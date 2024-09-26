package com.example.project.Intro;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.project.Login.LoginActivity;
import com.example.project.MainActivity;
import com.example.project.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {
    private static final String TAG = "SplashActivity";

    private FirebaseAuth auth;

    SharedPreferences sharedPreferences;
    String[] REQUIRED_PERMISSIONS = {Manifest.permission.INTERNET};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        sharedPreferences = getSharedPreferences("loginInfo", MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("autoLogin", false);

        new Handler().postDelayed(()->{
            if(currentUser != null && isLoggedIn){
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }else{
                Intent intent = new Intent(SplashActivity.this, StartActivity.class);
                startActivity(intent);
                finish();
            }
        }, 1000);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length == REQUIRED_PERMISSIONS.length){
            boolean check = true;
            for(int result : grantResults){
                if(result != PackageManager.PERMISSION_GRANTED){
                    check = false;
                    break;
                }
            }
        }else{
            showToast("권한이 거부되었습니다. 설정에서 권한을 허용해주세요.");
            finish();
        }
    }

    private void showToast(String msg){Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();}
}