package com.example.project.Chatting;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project.Data.ChatData;
import com.example.project.R;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {
    private List<ChatData> chatList;

    public ChatAdapter(List<ChatData> chatList){
        this.chatList = chatList;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatAdapter.ChatViewHolder holder, int position) {
        ChatData chatData = chatList.get(position);
        holder.text_message.setText(chatData.getMessage());
        holder.text_nickName.setText(chatData.getNickName());
    }

    @Override
    public int getItemCount() {return chatList.size();}

    static class ChatViewHolder extends RecyclerView.ViewHolder {
        private TextView text_message, text_nickName;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            text_message = itemView.findViewById(R.id.text_message);
            text_nickName = itemView.findViewById(R.id.text_nickName);
        }
    }
}
