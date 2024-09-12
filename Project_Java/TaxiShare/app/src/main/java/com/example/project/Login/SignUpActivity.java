package com.example.project.Login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.project.Email.GenRandNum;
import com.example.project.Email.SendCode;
import com.example.project.MainActivity;
import com.example.project.PopUp.EmailPopup;
import com.example.project.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import javax.mail.internet.AddressException;

public class SignUpActivity extends AppCompatActivity {
    private static final String TAG = "SignUpActivity";

    private FirebaseAuth myAuth;
    private FirebaseDatabase database;
    private FirebaseUser user;
    private StorageReference storageRef;
    private DatabaseReference databaseRef;
    private Uri imgUri;

    private ProgressDialog progressDialog;

    private boolean verifyEmail = false;  //이메일
    private static final String KEY = "777";
    private String pwPattern = "(?=.*[a-zA-Z])(?=.*[0-9])[a-zA-Z0-9]+";
    private String idPattern =  "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    private ActivityResultLauncher<Intent> launcher;

    ImageView img_mail;
    EditText ID, PW, re_PW, nickName;
    TextView confirmEmail;
    AppCompatButton btn_signUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        myAuth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        storageRef = FirebaseStorage.getInstance().getReference();
        databaseRef = FirebaseDatabase.getInstance().getReference();

        progressDialog = new ProgressDialog(this);

        ID = findViewById(R.id.ID);
        PW = findViewById(R.id.PW);
        re_PW = findViewById(R.id.re_PW);
        nickName = findViewById(R.id.nickName);
        img_mail = findViewById(R.id.img_mail);

        String id = ID.getText().toString();
        String pw = PW.getText().toString();
        String re_pw = re_PW.getText().toString();
        String nick = nickName.getText().toString();

        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if(result.getResultCode() == RESULT_OK){
                Intent intent = result.getData();
                if(intent != null){
                    String resultValue = intent.getStringExtra("result");
                    if(resultValue.equals("success")){
                        verifyEmail = true;
                        confirmEmail.setText("이메일 인증 완료");
                        confirmEmail.setTextColor(Color.GREEN);
                        img_mail.setImageResource(R.drawable.img_check);

                        ID.setEnabled(false);  //이메일 입력창 입력 비활성화
                    }
                }
            }
        });

        confirmEmail = findViewById(R.id.confirmEmail);
        confirmEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id = ID.getText().toString();
                String emailCode = new GenRandNum().genRandNum();
                if(id.isEmpty()){
                    ID.setError("아이디를 입력하세요");
                    ID.requestFocus();
                }else{
                    String email = id + "@cku.ac.kr";
                    SendCode sendCode = new SendCode(email, emailCode);
                    if(sendCode.sendEmail()) {
                        showToast("인증번호를 메일로 전송했습니다");
                        Intent intent = new Intent(SignUpActivity.this, EmailPopup.class);
                        intent.putExtra(KEY, emailCode);
                        launcher.launch(intent);
                    }
                }
            }
        });

        btn_signUp = findViewById(R.id.btn_signUp);
        btn_signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                }else if (!pw.matches(pwPattern)) {
                    re_PW.setError("비밀번호는 영문과 숫자를 함께 사용하세요.");
                } else if (TextUtils.isEmpty(nick)){
                    nickName.setError("닉네임을 입력하세요");
                    nickName.requestFocus();
                }else if (!pw.equals(re_pw)) {
                    re_PW.setError("비밀번호를 동일하게 입력하세요");
                    PW.setText("");
                    re_PW.setText("");
                    PW.requestFocus();
                }else if (nick.length() <= 5){
                    nickName.setError("닉네임은 5글 이상 입력하세요");
                }else if (nick.length() >= 10){
                    nickName.setError("닉네임은 10글자 이하로 입력하세요");
                }else{  // 이메일 인증이 완료된 경우
                    if(verifyEmail == true){
                        String email = id + "@cku.ac.kr";
                        progressDialog.setMessage("회원등록 중");
                        progressDialog.show();
                        myAuth.createUserWithEmailAndPassword(email, pw).addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    user = myAuth.getCurrentUser();
                                    showToast("회원가입 성공");
                                    finish();
                                    Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                                    startActivity(intent);
                                    progressDialog.dismiss();
                                }else {  // 회원가입 버튼 클릭 시 이메일 중복 검사
                                    Exception exception = task.getException();
                                    if(exception instanceof FirebaseAuthUserCollisionException)
                                        showToast("이메일이 중복됩니다");
                                    else
                                        showToast("회원가입 실패");
                                }
                            }
                        });
                    }else{  // 이메일 인증이 완료되지 않은 경우

                    }
                }
            }
        });
    }

    protected void init(){}

    private void signUp(){}

    private void showToast(String msg){Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();}

}