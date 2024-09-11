package com.example.project.Login;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

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

    private ProgressDialog progressDialog;

    // 이메일 검증
    private boolean verifyEmail = false;

    EditText ID, PW, re_PW, nickName;
    AppCompatButton btn_signUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        auth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        storageRef = FirebaseStorage.getInstance().getReference();
        databaseRef = FirebaseDatabase.getInstance().getReference();

        progressDialog = new ProgressDialog(this);

        ID = findViewById(R.id.ID);
        String id = ID.getText().toString();
        PW = findViewById(R.id.PW);
        String pw = PW.getText().toString();
        re_PW = findViewById(R.id.re_PW);
        String re_pw = re_PW.getText().toString();
        nickName = findViewById(R.id.nickName);
        String nick = nickName.getText().toString();

        btn_signUp = findViewById(R.id.btn_signUp);
        if(TextUtils.isEmpty(id)) {
            ID.setError("아이디를 입력하세요");
            ID.requestFocus();
        }
        else if(TextUtils.isEmpty(pw)) {
            PW.setError("비밀번호를 입력하세요");
            PW.requestFocus();
        }else if (pw.length() < 8){
            PW.setError("비밀번호를 8글자 이상 입력하세요");
        } else if(TextUtils.isEmpty(re_pw)) {
            re_PW.setError("비밀번호를 다시 입력하세요");
            re_PW.requestFocus();
        }else if (TextUtils.isEmpty(nick)){
            nickName.setError("닉네임을 입력하세요");
            nickName.requestFocus();
        }else if (pw != re_pw) {
            re_PW.setError("비밀번호를 동일하게 입력하세요");
        }

    }

    protected void init(){}

    private void signUp(){}

    private void showToast(String msg){Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();}

}