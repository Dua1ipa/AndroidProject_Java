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
import com.example.project.R;

public class SplashActivity extends AppCompatActivity {
    private static final String TAG = "SplashActivity";

    String[] REQUIRED_PERMISSIONS = {Manifest.permission.INTERNET};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, StartActivity.class);
                startActivity(intent);
                finish();
//                nextActivity();
            }
        },1000);

    }

    // login 상태  체크 //
    private void nextActivity(){
//        // SharedPreferences에서 login 정보 있는 지 확인한다
//        SharedPreferences sharedPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
//
//        // 있을 경우 isLoggedIn = true
//        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);
//        if (isLoggedIn) {  //로그인 기록 있으면 메인 화면으로 이동
//            startActivity(new Intent(Splash.this, MainActivity.class));
//            finish();
//        } else {  //로그인 기록 없으면 로그인화면으로 이동
//            startActivity(new Intent(Splash.this, LoginActivity.class));
//            finish();
//        }
        startActivity(new Intent(SplashActivity.this, StartActivity.class));
        finish();
    }

    private class splashHandler implements Runnable{
        @Override
        public void run() {
            startActivity(new Intent(getApplication(), LoginActivity.class));
        }
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
            Toast.makeText(SplashActivity.this, "권한이 거부되었습니다. 설정에서 권한을 허용해주세요.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}