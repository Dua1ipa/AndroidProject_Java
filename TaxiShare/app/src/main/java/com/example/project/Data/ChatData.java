package com.example.project.Data;


import com.google.firebase.Timestamp;

public class ChatData {
    private String nickName;
    private String uid;
    private String message;
    private String profileUrl;
    private Timestamp time;

    public ChatData() {}  //Firestore에서 객체를 불러올 때 기본 생성자가 필요합니다.

    public ChatData(String nickName, String senderID, String message, Timestamp time) {
        this.nickName = nickName;
        this.uid = senderID;
        this.message = message;
        this.time = time;
    }

    public ChatData(String nickName,String uid, String message, String profileUrl, Timestamp time) {
        this.nickName = nickName;
        this.uid = uid;
        this.message = message;
        this.profileUrl = profileUrl;
        this.time = time;
    }

    public String getNickName() {return nickName;}
    public void setNickName(String nickName) {this.nickName = nickName;}

    public String getUid() {return uid;}
    public void setUid(String uid) {this.uid = uid;}

    public String getMessage() {return message;}
    public void setMessage(String message) {this.message = message;}

    public String getProfileUrl() {return profileUrl;}
    public void setProfileUrl(String profileUrl) {this.profileUrl = profileUrl;}

    public Timestamp getTime() {return time;}
    public void setTime(Timestamp time) {this.time = time;}

}
