package com.example.project.Join;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.project.Chatting.ChattingFragment;
import com.example.project.Data.ChatData;
import com.example.project.Data.ChatMemberData;
import com.example.project.Data.SwipeToDeleteCallback;
import com.example.project.Data.TaxiRoom;
import com.example.project.Data.TaxiRoomsAdapter;
import com.example.project.Home.HomeFragment;
import com.example.project.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class JoinFragment extends Fragment {
    private static final String TAG = "JoinFragment";

    // 파이어베이스
    private FirebaseUser user;
    private DatabaseReference databaseReference;
    private String uid, nickName;
    private FirebaseDatabase database;

    private RecyclerView recyclerView;
    private ArrayList<TaxiRoom> roomsList;
    private ArrayList<ChatData> chatList;
    private TaxiRoomsAdapter taxiRoomsAdapter;

    ChatMemberData chatMemberData;

    AppCompatButton btn_joinedRoom, btn_createdRoom;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_join, container, false);

        recyclerView = viewGroup.findViewById(R.id.recyclerView);
        roomsList = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));  //LayoutManager 설정 (수직 리스트)

        user = FirebaseAuth.getInstance().getCurrentUser();
        uid = user.getUid();

        database = FirebaseDatabase.getInstance();    //파이어베이스 참조
        databaseReference = database.getReference();  //파이어베이스 -> 데이터베이스 참조

        btn_createdRoom = viewGroup.findViewById(R.id.btn_createdRoom);
        btn_joinedRoom = viewGroup.findViewById(R.id.btn_joinedRoom);

        showCreatedRooms();
        btn_joinedRoom.setTextColor(Color.WHITE);
        btn_createdRoom.setTextColor(Color.BLACK);

        btn_createdRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recyclerView.setAdapter(taxiRoomsAdapter);
                btn_joinedRoom.setTextColor(Color.WHITE);
                btn_createdRoom.setTextColor(Color.BLACK);
            }
        });

        btn_joinedRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn_joinedRoom.setTextColor(Color.BLACK);
                btn_createdRoom.setTextColor(Color.WHITE);
            }
        });

        return viewGroup;
    }

    private void showCreatedRooms(){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("usersInfo/"+uid+"/TaxiRooms");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {  //Firebase에서 데이터 가져오기
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){  //택시 방이 개설 되어 있으면
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){  //DB 순회 하면서 읽기
                        TaxiRoom taxiRoom = dataSnapshot.getValue(TaxiRoom.class);
                        roomsList.add(taxiRoom);
                    }
                    taxiRoomsAdapter = new TaxiRoomsAdapter(roomsList, chatList, new TaxiRoomsAdapter.ItemClickListener() {
                        @Override
                        public void onItemClick(TaxiRoom item) {  //생성된 택시 방을 누르면
                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
                            alertDialogBuilder.setTitle("채팅 참여 안내");
                            alertDialogBuilder.setMessage("채팅에 참여 하시겠습니까?");
                            alertDialogBuilder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Bundle bundle = new Bundle();
                                    DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("usersInfo").child(uid);
                                    usersRef.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            // 사용자 정보 가져오기
                                            nickName = snapshot.child("NickName").getValue(String.class);    //닉네임
                                            String roomKey = snapshot.child("TaxiRooms").child(item.getRoomKey()).getKey();
                                            createdChatRoom(roomKey, uid, nickName);

                                            bundle.putString("roomKey", item.getRoomKey());
                                            bundle.putString("nickName", nickName);

                                            ChattingFragment chattingFragment = new ChattingFragment();
                                            chattingFragment.setArguments(bundle);

                                            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                                            transaction.replace(R.id.container, chattingFragment);
                                            transaction.addToBackStack(null);
                                            transaction.commit();
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {}
                                    });
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
                    // ItemTouchHelper를 사용하여 스와이프 동작 추가
                    ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToDeleteCallback(taxiRoomsAdapter, getContext()));
                    itemTouchHelper.attachToRecyclerView(recyclerView);

                    taxiRoomsAdapter.notifyDataSetChanged();
                }else{showToast("데이터가 없습니다");}
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    // 채팅방 생성 //
    private void createdChatRoom(String roomKey, String uid, String nickname){
        DatabaseReference chatRef = databaseReference.child("chatsInfo");
        chatMemberData = new ChatMemberData(uid, nickName);
        chatRef.child(uid).child(roomKey).setValue(chatMemberData);
    }

    private void showToast(String msg){Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();}

}