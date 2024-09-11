package com.example.project.Login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.project.R;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.example.project.Intro.StartActivity;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class FindActivity extends AppCompatActivity {
    private static final String TAG = "FindActivity";

    private FirebaseAuth mAuth;
    private FirebaseUser user;

    Context ct;
    ImageButton btn_check, btn_back, btn_check2;
    EditText id;

    SweetAlertDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find);

        mAuth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);

        id = findViewById(R.id.id);

        // 뒤로가기 버튼
        btn_back = findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), StartActivity.class));
                FindActivity.this.finish();
            }
        });

        // 확인 버튼
        btn_check = findViewById(R.id.btn_check);
        btn_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 이메일이 공백이면
                if(id.getText().toString().isEmpty()){
                    SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(ct, SweetAlertDialog.ERROR_TYPE);
                    sweetAlertDialog.setTitleText("오류");
                    sweetAlertDialog.setContentText("이메일을 입력하세요"); 
                    sweetAlertDialog.show();
                    return;
                }

            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
    }

}