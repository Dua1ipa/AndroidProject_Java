package com.example.project.Chatting;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.project.Data.ChatData;
import com.example.project.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChattingFragment extends Fragment {

    // 파이어베이스
    private FirebaseFirestore firestore;
    private RecyclerView recyclerView;
    private List<ChatData> chatList;
    private FirebaseUser user;
    private DatabaseReference databaseReference;
    private String uid, nickName;
    private FirebaseDatabase database;
    private ChatAdapter chatAdapter;

    private EditText messageInput;
    private Button sendButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_chatting, container, false);

        // Firestore 초기화
        firestore = FirebaseFirestore.getInstance();

        user = FirebaseAuth.getInstance().getCurrentUser();
        uid = user.getUid();

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
        sendButton.setOnClickListener(v -> sendMessage());

        // Firestore에서 실시간 메시지 읽기
        listenForMessages();

        return viewGroup;
    }

    private void sendMessage(){
        String messageText = messageInput.getText().toString().trim();
        if (!messageText.isEmpty()) {  // 채팅 메시지 데이터 생성
            Map<String, Object> message = new HashMap<>();
            message.put("senderId", FirebaseAuth.getInstance().getCurrentUser().getUid());
            message.put("message", messageText);
            message.put("timestamp", FieldValue.serverTimestamp());

            // Firestore에 메시지 추가
            firestore.collection("chatsInfo")
                    .add(message)
                    .addOnSuccessListener(documentReference -> {  // 메시지 전송 성공
                        messageInput.setText("");  // 입력 필드 비우기
                    })
                    .addOnFailureListener(e -> {  // 메시지 전송 실패 처리
                        Log.e("ChatFragment", "메시지 전송 실패", e);
                    });
        }
    }

    private void listenForMessages(){
        firestore.collection("chatsInfo")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener((queryDocumentSnapshots, e) -> {
                    if (e != null) {
                        Log.e("ChatFragment", "메시지 로드 실패", e);
                        return;
                    }
                    if (queryDocumentSnapshots != null) {
                        chatList.clear();
                        for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                            ChatData chatMessage = snapshot.toObject(ChatData.class);
                            chatList.add(chatMessage);
                        }
                        chatAdapter.notifyDataSetChanged();
//                        recyclerView.smoothScrollToPosition(chatList.size() - 1);
                    }
                });
    }

}