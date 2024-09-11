package com.example.project.MyPage;

import static android.app.Activity.RESULT_OK;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.project.Data.imgData;

import com.example.project.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyPageFragment extends Fragment {
    private static final String TAG = "MyPageFragment";

    // 파이어 베이스
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private FirebaseUser user;
    private Uri uri;

    CircleImageView imageView;

    private ProgressBar progressBar;
    ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_my_page, container, false);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("데이터 불려오는 중입니다");
//        progressDialog.show();

        //프로그래스바 숨기기
//        progressBar.setVisibility(View.INVISIBLE);

        imageView = viewGroup.findViewById(R.id.profileImg);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        // 사진 가져오기
        ActivityResultLauncher<Intent> activityResult = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if( result.getResultCode() == RESULT_OK && result.getData() != null){
                            uri = result.getData().getData();
                            imageView.setImageURI(uri);
                        }
                    }
                });

        return viewGroup;
    }

    private void uploadImg(Uri uri){

        // 이미지 담기
        imgData imgData = new imgData(uri.toString());

        // 데이터 넣기

        //프로그래스바 숨김
        progressBar.setVisibility(View.INVISIBLE);

        showToast("이미지 변경 성공");
    }

    private void showUserInfo(){
        user  = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null){}
        else{
            String userId = user.getUid();
        }
    }

    private void showToast(String msg){Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();}

}