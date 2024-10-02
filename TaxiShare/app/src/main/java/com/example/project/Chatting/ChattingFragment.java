package com.example.project.Chatting;

import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.project.Data.ChatData;
import com.example.project.R;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChattingFragment extends Fragment {
    private static final String TAG = "ChattingFragment";

    // 파이어베이스
    private FirebaseFirestore firestore;
    private RecyclerView recyclerView;
    private List<ChatData> chatList;
    private FirebaseUser user;
    private DatabaseReference databaseReference;
    private String uid, nickName, roomKey;
    private String roomName;
    private FirebaseDatabase database;
    private ChatAdapter chatAdapter;

    private EditText messageInput;
    private ImageView sendButton;

    // 툴바
    Toolbar toolbar_chat;
    private ImageView toolbar_back, toolbar_group;
    private TextView toolbar_title;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_chatting, container, false);

        // Firestore 초기화
        firestore = FirebaseFirestore.getInstance();

        user = FirebaseAuth.getInstance().getCurrentUser();
        uid = user.getUid();

        Bundle args = getArguments();
        roomKey = args.getString("roomKey");
        nickName = args.getString("nickName");
        roomName = args.getString("roomName");

        // 툴바 설정
        toolbar_chat = viewGroup.findViewById(R.id.toolbar_chat);
        toolbar_back = viewGroup.findViewById(R.id.toolbar_back);
        toolbar_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        toolbar_title = viewGroup.findViewById(R.id.toolbar_title);
        toolbar_title.setText(roomName);

        toolbar_group = viewGroup.findViewById(R.id.toolbar_group);
        toolbar_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        // RecyclerView 설정
        recyclerView = viewGroup.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        chatList = new ArrayList<>();
        chatAdapter = new ChatAdapter(chatList);
        recyclerView.setAdapter(chatAdapter);

        // 메시지 입력 필드와 버튼
        messageInput = viewGroup.findViewById(R.id.message_input);
        sendButton = viewGroup.findViewById(R.id.send_button);

        // 메시지 전송 버튼 클릭 이벤트
        sendButton.setOnClickListener(view -> {
            String messageText = messageInput.getText().toString().trim();
            if(!messageText.isEmpty()){
                sendMessage(messageText);
                messageInput.setText("");  // 입력 필드 비우기
            }
        });

        // Firestore에서 실시간 메시지 읽기
        listenForMessages();

        return viewGroup;
    }

    // 메시지 전송 //
    private void sendMessage(String messageText){
        ChatData chatData = new ChatData(roomKey, nickName,uid,messageText,new Timestamp(new Date()));

        // Firestore에 메시지 추가
        firestore.collection("chatsInfo")
                .document(roomKey)
                .collection(uid)
                .add(chatData)
                .addOnSuccessListener(documentReference -> {  // 메시지 전송 성공
                    Log.e(TAG, "메시지 전송 성공");
                })
                .addOnFailureListener(e -> {  // 메시지 전송 실패 처리
                    Log.e(TAG, "메시지 전송 실패", e);
                });
    }

    // 실시간으로 메시지 수신하는 메소드 //
    private void listenForMessages(){
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("chatsInfo")
                .document(roomKey)
                .collection(uid)
                .orderBy("time", Query.Direction.ASCENDING)
                .addSnapshotListener((queryDocumentSnapshots, e) -> {
                    if (e != null) {
                        Log.e(TAG, "메시지 로드 실패", e);
                        return;
                    }
                    if(queryDocumentSnapshots != null){
                        chatList.clear();  //기존 메시지 리스트를 초기화
                        for(QueryDocumentSnapshot doc : queryDocumentSnapshots){  //Firestore에서 가져온 문서를 반복하면서 채팅 데이터를 리스트에 추가
                            String nickName = doc.getString("nickName");
                            String senderID = doc.getString("senderID");
                            String message = doc.getString("message");

                            Log.d(TAG, "닉네임: " + nickName + ", 메시지: " + message);  //데이터가 제대로 불러와졌는지 확인

                            ChatData chatData = new ChatData(roomKey, nickName, uid, message, new Timestamp(new Date()));  //ChatData 객체 생성

                            chatList.add(chatData);  //메시지를 리스트에 추가
                        }
                    }
                    chatAdapter.notifyDataSetChanged();  //어댑터에 변경사항을 알리고 RecyclerView 갱신
                    recyclerView.scrollToPosition(chatList.size()-1);  //최신 메시지로 스크롤
                });
    }

}