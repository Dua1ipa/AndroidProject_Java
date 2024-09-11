package com.example.project.Login;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

    // 버튼
    Button btn_login;
    TextView text_find, text_signUp;

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

        // 로그인 버튼
        btn_login = findViewById(R.id.btn_login);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String id = ID.getText().toString();
                String pw = PW.getText().toString();

                if (TextUtils.isEmpty(id))
                    showToast("아이디를 입력하세요!");
                else if (TextUtils.isEmpty(pw))
                    showToast("비밀번호를 입력하세요!");
                else{
                    myAuth.signInWithEmailAndPassword(id, pw).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                showToast("로그인 성공");
                            }else{
                                showToast("로그인 정보가 일치하지 않습니다.");
                                ID.setText("");
                                PW.setText("");
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

    }

    private void showToast(String msg){Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();}
}