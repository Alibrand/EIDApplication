package com.ksacp2022.electronicinteriordesignapplication.models;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Chat {
    String id;
    Map<String,String> participants=new HashMap<>();
    @ServerTimestamp
    Date last_update;

    public Chat() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<String,String> getParticipants() {
        return participants;
    }

    public void setParticipants(Map<String,String> participants) {
        this.participants = participants;
    }

    public Date getLast_update() {
        return last_update;
    }

    public void setLast_update(Date last_update) {
        this.last_update = last_update;
    }
}
