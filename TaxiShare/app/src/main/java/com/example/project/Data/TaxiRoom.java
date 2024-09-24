package com.example.project.Data;

public class TaxiRoom {

    private  String uid , roomName,countPerson ,  departure,  destination, dateOfDeparture,timeOfDeparture,description,roomKey;

    public TaxiRoom() {}

    public TaxiRoom(String roomKey,String uid, String roomName, String countPerson, String departure, String destination, String dateOfDeparture, String timeOfDeparture, String description) {
        this.roomKey = roomKey;
        this.uid = uid;
        this.roomName = roomName;
        this.countPerson = countPerson;
        this.departure = departure;
        this.destination = destination;
        this.dateOfDeparture = dateOfDeparture;
        this.timeOfDeparture = timeOfDeparture;
        this.description = description;
    }

    public String getRoomKey() {return roomKey;}
    public void setRoomKey(String roomKey) {this.roomKey = roomKey;}

    public String getUserId() {
        return uid;
    }
    public void setUserId(String userId) {
        this.uid = uid;
    }

    public String getRoomName() {
        return roomName;
    }
    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getCountPerson() {
        return countPerson;
    }
    public void setCountPerson(String countPerson) {
        this.countPerson = countPerson;
    }

    public String getDeparture() {
        return departure;
    }
    public void setDeparture(String departure) {
        this.departure = departure;
    }

    public String getDestination() {
        return destination;
    }
    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getDateOfDeparture() {
        return dateOfDeparture;
    }
    public void setDateOfDeparture(String dateOfDeparture) {this.dateOfDeparture = dateOfDeparture;}

    public String getTimeOfDeparture() {
        return timeOfDeparture;
    }
    public void setTimeOfDeparture(String timeOfDeparture) {this.timeOfDeparture = timeOfDeparture;}

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

}
