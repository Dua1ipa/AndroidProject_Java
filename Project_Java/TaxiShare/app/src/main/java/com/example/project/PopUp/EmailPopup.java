package com.example.project.PopUp;

import android.os.Bundle;
import android.os.CountDownTimer;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.project.R;

public class EmailPopup extends AppCompatActivity {
    private static final String TAG = "EmailPopup";

    private CountDownTimer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_email_popup);
    }

    // 인증 시간 함수 //
    private void countTime(){
        timer = new CountDownTimer(180000, 1000) {
            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {

            }
        }.start();
    }

    // 3분 지나면 다이얼로그 //
    private void showAlert(){

    }

}