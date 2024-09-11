package com.example.project.Data;

import androidx.annotation.NonNull;

public class userInfo {

    public String name;
    public String ID;
    public String PW;

    public userInfo(){};

    public userInfo(String name, String ID, String PW){
        this.name = name;
        this.ID = ID;
        this.PW = PW;
    }

    public String getName() {return name;}
    public void setName(String name) {this.name = name;}

    public String getID() {return ID;}
    public void setID(String ID) {this.ID = ID;}

    public String getPW() {return PW;}
    public void setPW(String PW) {this.PW = PW;}

    @NonNull
    @Override
    public String toString() {
        return "User{" +
                "Name: '" + name + '\'' +
                ", ID: '" + ID + '\'' +
                ", PW: '" + PW + '\'' + "}";
    }

}
