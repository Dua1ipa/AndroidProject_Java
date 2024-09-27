package com.example.project.Chatting;

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

    public ChatAdapter (List<ChatData> chatList){
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
        holder.bind(chatData);
    }

    @Override
    public int getItemCount() {return chatList.size();}

    static class ChatViewHolder extends RecyclerView.ViewHolder {
        private TextView messageText;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.text_message);
        }

        public void bind(ChatData chatMessage) {
            messageText.setText(chatMessage.getMessage());
        }
    }

}
