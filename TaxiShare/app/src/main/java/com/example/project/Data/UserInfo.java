package com.example.project.Data;

import androidx.annotation.NonNull;

public class UserInfo {

    public String NickName;
    public String ID;
    public String profileImg;

    public UserInfo(){};

    public UserInfo(String NickName, String ID){
        this.NickName = NickName;
        this.ID = ID;
    }

    public UserInfo(String NickName, String ID, String profileImg){
        this.NickName = NickName;
        this.ID = ID;
        this.profileImg = profileImg;
    }

    public String getNickName() {return NickName;}
    public void setNickName(String name) {this.NickName = NickName;}

    public String getID() {return ID;}
    public void setID(String ID) {this.ID = ID;}

    public String getprofileImg() {return profileImg;}
    public void setprofileImg(String profileImg) {this.profileImg = profileImg;}

    @NonNull
    @Override
    public String toString() {
        return "User{" +
                "NickName: '" + NickName + '\'' +
                ", ID: '" + ID + '\'';
    }

}
