package com.ksacp2022.electronicinteriordesignapplication.models;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RoomDesign {
    String id;
    String name;
    String description;
    String creator_id;
    String creator_name;
    List<String> images=new ArrayList<>();
    List<Integer> colors=new ArrayList<>();
    List<String> share_list=new ArrayList<>();
    @ServerTimestamp
    Date created_at=null;

    public RoomDesign() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public List<Integer> getColors() {
        return colors;
    }

    public void setColors(List<Integer> colors) {
        this.colors = colors;
    }

    public List<String> getShare_list() {
        return share_list;
    }

    public void setShare_list(List<String> share_list) {
        this.share_list = share_list;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }

    public String getCreator_id() {
        return creator_id;
    }

    public void setCreator_id(String creator_id) {
        this.creator_id = creator_id;
    }

    public String getCreator_name() {
        return creator_name;
    }

    public void setCreator_name(String creator_name) {
        this.creator_name = creator_name;
    }
}
