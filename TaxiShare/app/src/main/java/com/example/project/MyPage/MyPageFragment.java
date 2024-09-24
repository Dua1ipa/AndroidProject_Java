package com.example.project.MyPage;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.project.Data.imgData;

import com.example.project.Login.LoginActivity;
import com.example.project.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyPageFragment extends Fragment {
    private static final String TAG = "MyPageFragment";

    // 파이어 베이스
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private FirebaseUser user;
    private FirebaseAuth auth;
    private FirebaseStorage firebaseStorage;
    private Uri imgUri;

    TextView textNick, textEmail;
    CircleImageView imageView;
    AppCompatButton btn_editProfile, btn_logout;

    SharedPreferences sharedPreferences;

    private ProgressBar progressBar;

    private ActivityResultLauncher<Intent> resultLauncher;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_my_page, container, false);

        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        textNick = viewGroup.findViewById(R.id.textNick);
        textEmail = viewGroup.findViewById(R.id.textEmail);
        progressBar = viewGroup.findViewById(R.id.progressBar);

        showUserInfo();

        imgUri = null;  //선택된 이미지 uri
        resultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                    if(result.getResultCode() == RESULT_CANCELED) return;
                    imgUri = result.getData().getData();
                    Glide.with(this).load(imgUri).into(imageView);  //선택한 이미지 보여주기
                    deleteImg();  //전 이미지 삭제하기
                    upload();     //현재 이미지 업로드하기
                }
        );

        // 프로필 이미지 눌렀을 때
        imageView = viewGroup.findViewById(R.id.profileImg);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openImgChooser();
            }
        });

        btn_editProfile = viewGroup.findViewById(R.id.btn_editProfile);
        btn_editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        // 로그아웃
        btn_logout = viewGroup.findViewById(R.id.btn_logout);
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sharedPreferences = getActivity().getSharedPreferences("loginInfo", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();  //SharedPreferences에서 로그인 정보 삭제
                editor.apply();
                
                auth = FirebaseAuth.getInstance();
                auth.signOut();
                
                Intent intent = new Intent(getActivity(), LoginActivity.class);  //로그아웃 후 로그인 화면으로 이동
                showToast("로그아웃 성공");
                startActivity(intent);
            }
        });

        return viewGroup;
    }

    // 사용자 정보 출력하는 함수 //
    private void showUserInfo(){
        user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();

        if(user == null) return;
        else{
            progressBar.setVisibility(View.VISIBLE);  //프로그래스바 보이기
            storageReference = FirebaseStorage.getInstance().getReference().child("userProfImg/");
            storageReference.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
                @Override
                public void onSuccess(ListResult listResult) {
                    for(StorageReference item : listResult.getItems()){
                        String imgName = item.getName();
                        if(imgName.startsWith(uid) && imgName.endsWith(".png")){
                            item.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Glide.with(getActivity()).load(uri).into(imageView);
                                    progressBar.setVisibility(View.GONE);  //프로그래스바 숨기기
                                }
                            });
                        }
                    }
                }
            });

            databaseReference = FirebaseDatabase.getInstance().getReference().child("usersInfo").child(uid);
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    // 사용자 정보 가져오기
                    String nickName = snapshot.child("NickName").getValue(String.class);    //닉네임
                    String email = snapshot.child("ID").getValue(String.class);             //이메일

                    textNick.setText(nickName);
                    textEmail.setText(email);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });
        }
    }

    // 앨범 함수 //
    private void openImgChooser(){
        Intent intent = new Intent(MediaStore.ACTION_PICK_IMAGES);
        resultLauncher.launch(intent);
    }

    // 이미지 업로드 전 이전 이미지 삭제 함수 //
    private void deleteImg(){
        user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        Log.d(TAG, "uid : " + uid);

        if(user == null) return;
        else {
            storageReference = FirebaseStorage.getInstance().getReference().child("userProfImg/");
            storageReference.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
                @Override
                public void onSuccess(ListResult listResult) {
                    for (StorageReference item : listResult.getItems()) {
                        String imgName = item.getName();
                        if (imgName.startsWith(uid) && imgName.endsWith(".png")) {
                            item.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    StorageReference deleteRef = FirebaseStorage.getInstance().getReferenceFromUrl(uri.toString());
                                    deleteRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Log.d(TAG, "이미지 삭제 성공");
                                        }
                                    });
                                }
                            });
                        }
                    }
                }
            });
        }
    }

    // 이미지 업로드 함수 //
    private void upload(){
        if(imgUri == null)  return;  //사진 선택 안 했으면 반환

        firebaseStorage = FirebaseStorage.getInstance();  //파이어베어스 관리 저장소 객체 생성

        String uid = user.getUid();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");  //저장할 파일 이름이 중복되지 않도록 사용자 uid로 설정
        String fileName = uid + "_" + simpleDateFormat.format(new Date()) + "_" +".png";      //

        StorageReference imgRef = firebaseStorage.getReference("userProfImg/"+fileName);  //저장할 위치
        imgRef.putFile(imgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d(TAG, "이미지 변경 성공");
            }
        }).addOnFailureListener(new OnFailureListener() { 
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "이미지 변경 실패");
            }
        });
    }

    private void showToast(String msg){Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();}

}