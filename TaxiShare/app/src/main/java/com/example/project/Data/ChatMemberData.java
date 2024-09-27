package com.example.project.Data;

public class ChatMemberData {
    private String uid;
    private String nickName;

    public ChatMemberData() {}

    public ChatMemberData(String uid, String nickName) {
        this.uid = uid;
        this.nickName = nickName;
    }

    public String getUid() {return uid;}
    public void setUid(String uid) {this.uid = uid;}

    public String getNickName() {return nickName;}
    public void setNickName(String nickName) {this.nickName = nickName;}

}
