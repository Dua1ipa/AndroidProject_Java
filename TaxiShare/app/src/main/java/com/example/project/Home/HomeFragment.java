package com.example.project.Home;

import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomViewTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.project.Chatting.ChattingFragment;
import com.example.project.Data.SwipeToDeleteCallback;
import com.example.project.Data.TaxiRoom;

import com.example.project.Data.TaxiRoomsAdapter;
import com.example.project.Join.CurrentFragment;
import com.example.project.Join.JoinedFragment;
import com.example.project.MakeRoom.MakeRoomFragment;
import com.example.project.MyPage.MyPageFragment;
import com.example.project.R;
import com.example.project.Search.SearchFragment;
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

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeFragment extends Fragment {
    private static final String TAG = "HomeFragment";

    Toolbar toolbar;
    CircleImageView toolbar_image;
    ImageView toolbar_alert, toolbar_search;

    // 파이어베이스
    private FirebaseUser user;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private String uid;

    TextView text_count, text_1,text_2,text_3,text_4,text_5,text_6,text_7;
    Button btn_add,btn_1,btn_all,btn_2,btn_3,btn_4,btn_5,btn_6,btn_7;

    private RecyclerView recyclerView;
    private ArrayList<TaxiRoom> roomsList;
    private ArrayList<TaxiRoom> dateList;
    private TaxiRoomsAdapter taxiRoomsAdapter;

    MakeRoomFragment makeRoomFragment;

    private ExecutorService executorService;
    private Handler handler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_home, container, false);

        dateList = new ArrayList<>();
        handler = new Handler();
        executorService = Executors.newSingleThreadExecutor();

        // 툴바
        toolbar = viewGroup.findViewById(R.id.toolbar);
        toolbar_image = viewGroup.findViewById(R.id.toolbar_image);
        toolbar_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().beginTransaction().replace(R.id.container, new MyPageFragment()).commit();
            }
        });
        toolbar_alert = viewGroup.findViewById(R.id.toolbar_alert);
        toolbar_alert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().beginTransaction().replace(R.id.container, new AlertFragment()).commit();
            }
        });
        toolbar_search = viewGroup.findViewById(R.id.toolbar_search);
        toolbar_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().beginTransaction().replace(R.id.container, new SearchFragment()).commit();
            }
        });

        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        StorageReference imgRef = storageReference.child("userProfImg/");
        imgRef.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                for(StorageReference item : listResult.getItems()){
                    String imgName = item.getName();
                    if(imgName.startsWith(uid) && imgName.endsWith(".png")){
                        item.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                setToolbarIcon(uri.toString());
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                showToast("이미지를 불러올 수 없습니다.");
                                Log.e(TAG, "이미지를 불러올 수 없습니다.", e);
                            }
                        });
                    }
                }
            }
        });

        btn_all = viewGroup.findViewById(R.id.btn_all);
        btn_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recyclerView.setAdapter(taxiRoomsAdapter);
            }
        });

        text_1 = viewGroup.findViewById(R.id.text_1);
        text_1.setText(getDay(0));
        btn_1 = viewGroup.findViewById(R.id.btn_1);
        btn_1.setText(getTime(0));
        btn_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDateRoom(0);
            }
        });

        text_2 = viewGroup.findViewById(R.id.text_2);
        text_2.setText(getDay(1));
        btn_2 = viewGroup.findViewById(R.id.btn_2);
        btn_2.setText(getTime(1));
        btn_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDateRoom(1);
            }
        });

        text_3 = viewGroup.findViewById(R.id.text_3);
        text_3.setText(getDay(2));
        btn_3 = viewGroup.findViewById(R.id.btn_3);
        btn_3.setText(getTime(2));
        btn_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDateRoom(2);
            }
        });

        text_4 = viewGroup.findViewById(R.id.text_4);
        text_4.setText(getDay(3));
        btn_4 = viewGroup.findViewById(R.id.btn_4);
        btn_4.setText(getTime(3));
        btn_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDateRoom(3);
            }
        });

        text_5 = viewGroup.findViewById(R.id.text_5);
        text_5.setText(getDay(4));
        btn_5 = viewGroup.findViewById(R.id.btn_5);
        btn_5.setText(getTime(4));
        btn_5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDateRoom(4);
            }
        });

        text_6 = viewGroup.findViewById(R.id.text_6);
        text_6.setText(getDay(5));
        btn_6 = viewGroup.findViewById(R.id.btn_6);
        btn_6.setText(getTime(5));
        btn_6.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 getDateRoom(5);
             }
        });

        text_7 = viewGroup.findViewById(R.id.text_7);
        text_7.setText(getDay(6));
        btn_7 = viewGroup.findViewById(R.id.btn_7);
        btn_7.setText(getTime(6));
        btn_7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDateRoom(6);
            }
        });

        // 개설된 방 개수
        user = FirebaseAuth.getInstance().getCurrentUser();
        uid = user.getUid();
        text_count = viewGroup.findViewById(R.id.text_count);

        // 방 개설 버튼
        makeRoomFragment = new MakeRoomFragment();
        btn_add = viewGroup.findViewById(R.id.btn_add);
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().beginTransaction().replace(R.id.container, makeRoomFragment).commit();
            }
        });

        // 데이터 보여주기 (리싸이클러뷰)
        recyclerView = viewGroup.findViewById(R.id.recyclerView);
        roomsList = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));  //LayoutManager 설정 (수직 리스트)
        startAsyncCount();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("roomsInfo");  //Firebase 데이터베이스 참조
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {  //Firebase에서 데이터 가져오기
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){  //DB 순회 하면서 읽기
                    Log.d(TAG,dataSnapshot+" ");
//                    Log.d(TAG,uid+" ");
//                    Log.d(TAG,dataSnapshot.getKey()+" ");
                    if(dataSnapshot.getKey().equals(uid)) continue;

                    // dataSnapshot 내부의 특정 key 값에 접근해서 roomName 읽기
                    for (DataSnapshot innerSnapshot : dataSnapshot.getChildren()) {
                        TaxiRoom taxiRoom = innerSnapshot.getValue(TaxiRoom.class);  // TaxiRoom 객체로 변환
                        if (taxiRoom != null) {
                            roomsList.add(taxiRoom);
                        } else {
                            Log.d(TAG, "TaxiRoom is null"); 
                        }
                    }

                }
                taxiRoomsAdapter = new TaxiRoomsAdapter(roomsList, new TaxiRoomsAdapter.ItemClickListener() {
                    @Override
                    public void onItemClick(TaxiRoom item) {  //생성된 택시 방을 누르면
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
                        alertDialogBuilder.setTitle("택시 방 참여 안내");
                        alertDialogBuilder.setMessage("방에 참여 하시겠습니까?");
                        alertDialogBuilder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
//                                DatabaseReference roomRef = databaseReference.child("usersInfo").child(uid);
//                                roomRef.child("JoinedRooms").child(roomKey).setValue(taxiRoom)
//                                        .addOnSuccessListener(aVoid -> {
//                                            showToast("방에 참여 했습니다.");
//                                            getFragmentManager().beginTransaction().replace(R.id.container, new JoinedFragment()).commit();
//                                        })
//                                        .addOnFailureListener(e -> {
//                                            showToast("방 참여 실패");
//                                        });
                            }
                        });
                        alertDialogBuilder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                    }
                });  //Adapter 설정
                recyclerView.setAdapter(taxiRoomsAdapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        return viewGroup;
    }

    // 방 개수 비동기 처리 함수 //
    private void startAsyncCount(){
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                while(true){
                    try {
                        Thread.sleep(1000);  // 1초 대기
                    } catch (InterruptedException e) {e.printStackTrace();}
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            text_count.setText(roomsList.size()+"개");
                        }
                    });
                }

            }
        });
    }

    // 해당 날짜 방 검색 함수 //
    private void getDateRoom(int day){

        dateList.clear();
        for(TaxiRoom room : roomsList){
            Log.d(TAG,room.getDateOfDeparture());
            String today = getTime(day);
            if(room.getDateOfDeparture().contains(today)){
                dateList.add(room);
            }
        }
        recyclerView.setAdapter(new TaxiRoomsAdapter(dateList));
    }

    // 파이어베이스에서 데이터 가져오기 //
    public void getDataFromDB(){}

    // 시간 얻기 함수
    private String getTime(int day){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, day);

        SimpleDateFormat format = new SimpleDateFormat("dd일", Locale.KOREAN);

        return format.format(calendar.getTime());
    }

    // 날짜 얻기 함수
    private String getDay(int day){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, day);

        SimpleDateFormat format = new SimpleDateFormat("E", Locale.KOREAN);

        return format.format(calendar.getTime());
    }

    // 툴바 프로필 이지미 변경 함수 //
    private void setToolbarIcon(String imageUrl) {
        Glide.with(this)
                .load(imageUrl)
                .into(toolbar_image)
                .clearOnDetach();
    }

    private void showToast(String msg){Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();}

}