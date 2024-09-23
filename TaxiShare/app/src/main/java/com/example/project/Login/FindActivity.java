package com.example.project.Login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.project.R;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.database.DatabaseReference;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class FindActivity extends AppCompatActivity {
    private static final String TAG = "FindActivity";

    // 파이어베이스
    private DatabaseReference databaseReference;
    private FirebaseAuth auth;
    private FirebaseUser user;

    Context ct;
    ImageButton btn_check, btn_back, btn_check2;
    EditText editID;

    SweetAlertDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find);

        user = FirebaseAuth.getInstance().getCurrentUser();

        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);

        editID = findViewById(R.id.editID);

        // 뒤로가기 버튼
        btn_back = findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));  //로그인 화면으로 이동
                FindActivity.this.finish();
            }
        });

        // 확인 버튼
        btn_check = findViewById(R.id.btn_check);
        btn_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(editID.getText().toString().isEmpty()){  //이메일이 공백이면 오류 발생
                    SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(ct, SweetAlertDialog.ERROR_TYPE);
                    sweetAlertDialog.setTitleText("오류");
                    sweetAlertDialog.setContentText("이메일을 입력하세요"); 
                    sweetAlertDialog.show();
                }
                else{
                    String email = editID.getText().toString() + "@cku.ac.kr";
                    auth = FirebaseAuth.getInstance();
                    auth.sendPasswordResetEmail(email)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){  //비밀번호 재설정 이메일이 전송되면
                                        showToast("비밀번호 재설정 이메일이 전송되었습니다");
                                        startActivity(new Intent(getApplicationContext(), LoginActivity.class));  //로그인 화면으로 이동
                                        FindActivity.this.finish();
                                    }else{  //이메일 전송이 실패하면
                                        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(ct, SweetAlertDialog.ERROR_TYPE);
                                        sweetAlertDialog.setTitleText("아이디 오류");
                                        sweetAlertDialog.setContentText("해당 아이디가 없습니다");
                                        sweetAlertDialog.show();
                                    }
                                }
                            });
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

    private void showToast(String msg){Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();}

}