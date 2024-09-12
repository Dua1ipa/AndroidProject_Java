package com.example.project.Login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.project.Data.UserInfo;
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
import com.kakao.sdk.user.model.User;

import java.util.Objects;

import javax.mail.internet.AddressException;

public class SignUpActivity extends AppCompatActivity {
    private static final String TAG = "SignUpActivity";

    private FirebaseAuth myAuth;
    private FirebaseDatabase database;
    private StorageReference storageRef;
    private DatabaseReference usersRef;
    private Uri imgUri;

    private ProgressDialog progressDialog;

    private boolean verifyEmail = false;  //이메일
    private static final int PICK_IMAGE_REQUEST = 999;
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
        database = FirebaseDatabase.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference("userProfImg");
        usersRef = FirebaseDatabase.getInstance().getReference("usersInfo");

        progressDialog = new ProgressDialog(this);

        ID = findViewById(R.id.ID);
        PW = findViewById(R.id.PW);
        re_PW = findViewById(R.id.re_PW);
        nickName = findViewById(R.id.nickName);
        img_mail = findViewById(R.id.img_mail);

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

        // 이메일 인증 버튼
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

        // 회원가입 버튼
        btn_signUp = findViewById(R.id.btn_signUp);
        btn_signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id = ID.getText().toString();
                String pw = PW.getText().toString();
                String re_pw = re_PW.getText().toString();
                String nick = nickName.getText().toString();
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
                }else{
                    if(verifyEmail == true){  //이메일 인증이 완료된 경우
                        String email = id + "@cku.ac.kr";
                        progressDialog.setMessage("회원가입 중");
                        progressDialog.show();
                        // 사용자 생성
                        myAuth.createUserWithEmailAndPassword(email, pw).addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){  //사용자가 생성되면
                                    FirebaseUser user = myAuth.getCurrentUser();  //사용자 정보 가져오기
                                    showToast("회원가입 성공");
                                }else{  //사용자가 생성되지 않으면

                                }
                            }
                        });
                    }else{  //이메일 인증이 완료되지 않은 경우

                    }
                }
            }
        });
    }

    // 앨범 함수 //
    private void openImgChooser(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    // 사진 선택한 경우 처리함수 //
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == PICK_IMAGE_REQUEST && requestCode == RESULT_OK && data != null && data.getData() != null){
            imgUri = data.getData();
        }
    }

    private String getFileExtension(Uri uri){
        return Objects.requireNonNull(getContentResolver().getType(uri).split("/"))[1];
    }

    protected void init(){}

    private void signUp(){}

    private void showToast(String msg){Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();}

}