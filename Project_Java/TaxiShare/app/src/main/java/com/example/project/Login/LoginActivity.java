package com.example.project.Login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.project.MainActivity;
import com.example.project.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseUser firebaseUser;
    private FirebaseAuth myAuth;

    ProgressDialog progressDialog;

    // 버튼
    AppCompatButton btn_login;
    TextView text_find, text_signUp;
    CheckBox checkBox;

    EditText ID,PW;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        myAuth = FirebaseAuth.getInstance();
        firebaseUser = myAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Users");

//        String userID = firebaseUser.getUid();
//        Log.d(TAG, "userID : " + userID);

//        databaseReference.child("Male").setValue("Kim");

        ID = findViewById(R.id.ID);
        PW = findViewById(R.id.PW);
        checkBox = findViewById(R.id.checkBox);

        progressDialog = new ProgressDialog(this);

        // 로그인 버튼
        btn_login = findViewById(R.id.btn_login);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String id = ID.getText().toString();
                String pw = PW.getText().toString();

                if (TextUtils.isEmpty(id)) {
                    ID.setError("아이디를 입력하세요!");
                    ID.requestFocus();
                }
                else if (TextUtils.isEmpty(pw)) {
                    PW.setError("비밀번호를 입력하세요!");
                    PW.requestFocus();
                }
                else{
                    String email = id + "@cku.ac.kr";
                    progressDialog.setMessage("로그인 중");
                    progressDialog.show();
                    myAuth.signInWithEmailAndPassword(email, pw).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                if(checkBox.isChecked()){
                                    SharedPreferences sharedPreferences = getSharedPreferences("loginInfo", MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putBoolean("autoLogin", true);
                                    editor.putString("id", email);
                                    editor.putString("pw", pw);
                                    editor.apply();
                                }
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                showToast("로그인 성공");
                                progressDialog.dismiss();
                                finish();
                            }else{
                                showToast("로그인 정보가 일치하지 않습니다.");
                                progressDialog.dismiss();
                                ID.setText("");
                                PW.setText("");
                                ID.requestFocus();
                            }
                        }
                    });
                }
            }
        });

        // 아이디 & 비밀번호 찾기
        text_find = findViewById(R.id.text_find);
        text_find.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, FindActivity.class);
                startActivity(intent);
            }
        });

        // 회원가입
        text_signUp = findViewById(R.id.text_signUp);
        text_signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });

    }

    private void showToast(String msg){Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();}
}