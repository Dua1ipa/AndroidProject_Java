package com.example.project.PopUp;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.project.R;

import java.util.Locale;

public class EmailPopup extends AppCompatActivity {
    private static final String TAG = "EmailPopup";

    private CountDownTimer timer;
    private boolean verifyEmail = true;
    private static final String KEY = "777";

    TextView timerText;
    EditText codeNum;
    AppCompatButton btn_check;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.email_popup);

        codeNum = findViewById(R.id.codeNum);
        timerText = findViewById(R.id.timerText);

        countTime();

        btn_check = findViewById(R.id.btn_check);
        btn_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String code = codeNum.getText().toString();
                getIntentData(code);
            }
        });

    }

    // 인증 시간 함수 //
    private void countTime(){
        timer = new CountDownTimer(180000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long min = millisUntilFinished / 1000 / 60;
                long sec = (millisUntilFinished / 1000) % 60;
                String timeFormat = String.format(Locale.getDefault(), "유효시간 %02d분 %02d초 ", min, sec);
                timerText.setText(timeFormat);
            }

            @Override
            public void onFinish() {
                verifyEmail = false;
                showAlert("인증 시간이 종료됨");
            }
        }.start();
    }

    // 3분 지나면 다이얼로그 //
    private void showAlert(String msg){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("확인", (dialog, which) -> {
                    dialog.dismiss();
                    finish();
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    //
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(timer != null)
            timer.cancel();
    }

    // 인텐트로 전달 받은 값 처리 함수 //
    private void getIntentData(String condeNum){
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String code = codeNum.getText().toString();

        // 이메일 인증할 때 인텐트를 통해 키값 전달
        if(intent.hasExtra(KEY)){
            String data = bundle.getString(KEY);
            if(code.equals(data)){
                showToast("이메일 인증에 성공했습니다.");
                Intent resultIntent = new Intent();
                resultIntent.putExtra("result", "success");
                setResult(RESULT_OK, resultIntent);
                finish();
            }
            else
                showToast("인증번호가 일치하지 않습니다.");
        }
    }

    private void showToast(String msg){Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();}
}