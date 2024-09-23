package com.example.project.Login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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

import com.bumptech.glide.Glide;
import com.example.project.Data.UserInfo;
import com.example.project.Email.GenRandNum;
import com.example.project.Email.SendCode;
import com.example.project.MainActivity;
import com.example.project.PopUp.EmailPopup;
import com.example.project.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.google.firebase.storage.UploadTask;
import com.kakao.sdk.user.model.User;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.mail.internet.AddressException;

import de.hdodenhof.circleimageview.CircleImageView;

public class SignUpActivity extends AppCompatActivity {
    private static final String TAG = "SignUpActivity";

    FirebaseAuth myAuth;
    FirebaseDatabase database;
    FirebaseStorage firebaseStorage;
    FirebaseUser user;
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
    private ActivityResultLauncher<Intent> resultLauncher;

    CircleImageView circleImg;
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
        usersRef = FirebaseDatabase.getInstance().getReference("usersInfo");

        firebaseStorage = FirebaseStorage.getInstance();
        storageRef = firebaseStorage.getReference();
        StorageReference imgRef = storageRef.child("default_user.png");

        progressDialog = new ProgressDialog(this);

        ID = findViewById(R.id.ID);
        PW = findViewById(R.id.PW);
        re_PW = findViewById(R.id.re_PW);
        nickName = findViewById(R.id.nickName);
        circleImg = findViewById(R.id.circleImg);
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

        imgUri = null;  //선택된 이미지 uri
        resultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if(result.getResultCode() == RESULT_CANCELED) return;
                imgUri = result.getData().getData();

                Glide.with(this).load(imgUri).into(circleImg);
            }
        );

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

        // 이미지 버튼
        circleImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openImgChooser();
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
                                    user = myAuth.getCurrentUser();  //사용자 정보 가져오기
                                    if(imgUri != null){
                                        imgRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                Glide.with(SignUpActivity.this).load(uri).into(circleImg);
                                            }
                                        });
                                    }

                                    Map<String, Object> userMap = new HashMap<>();
                                    userMap.put("uid", user.getUid());
                                    userMap.put("NickName", nick);
                                    userMap.put("ID", id);

                                    usersRef.child(user.getUid()).setValue(userMap);
                                    upload();

                                    progressDialog.dismiss();
                                    Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                                    startActivity(intent);
                                    showToast("회원가입 성공");
                                }else{  //사용자가 생성되지 않으면
                                    Exception exception = task.getException();
                                    if(exception instanceof FirebaseAuthUserCollisionException)
                                        showToast("이미 존재하는 아이디입니다");
                                    else
                                        showToast("회원가입 실패");
                                    progressDialog.dismiss();
                                }
                            }
                        });
                    }else{  //이메일 인증이 완료되지 않은 경우
                        confirmEmail.setText("이메일 인증을 완료해주세요");
                        confirmEmail.setTextColor(Color.RED);
                    }
                }
            }
        });
    }

    // 앨범 함수 //
    private void openImgChooser(){
        Intent intent = new Intent(MediaStore.ACTION_PICK_IMAGES);
        resultLauncher.launch(intent);
    }

    // 이미지 업로드 함수 //
    private void upload(){
        if(imgUri == null)  return;  //사진 선택 안 했으면 반환

        firebaseStorage = FirebaseStorage.getInstance();  //파이어베어스 관리 저장소 객체 생성

        String uid = user.getUid();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");  //저장할 파일 이름이 중복되지 않도록 사용자 uid로 설정
        String fileName = uid + "_" + simpleDateFormat.format(new Date()) + "_" +".png";  //

        StorageReference imgRef = firebaseStorage.getReference("userProfImg/"+fileName);  //저장할 이름
        imgRef.putFile(imgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d(TAG, "이미지 업로드 성공");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "이미지 업로드 실패");
            }
        });
    }

    private void showToast(String msg){Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();}

}