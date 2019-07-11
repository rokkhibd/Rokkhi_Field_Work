package com.rokkhi.rokkhifieldwork;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.GeoPoint;

import java.io.Serializable;
import java.util.ArrayList;

public class Buildings implements Serializable {

   @Exclude private String id;
   // private String id;
    private String address;
    private String field_description;
    private String imageUrl;
    private String manager_name;
    private String manager_number;
    private String owner_name;
    private String owner_number;
    private String todaysDate;
    private String followUpDate;
    private String person_We_Talked;
   // private GeoPoint geo_point;
    private ArrayList<String> tags;


    public Buildings(){

    }

    public Buildings(String address, String field_description, String imageUrl, String manager_name, String manager_number,
                     String owner_name, String owner_number, String todaysDate, String followUpDate,String person_We_Talked) {
        this.address = address;
        this.field_description = field_description;
        this.imageUrl = imageUrl;
        this.manager_name = manager_name;
        this.manager_number = manager_number;
        this.owner_name = owner_name;
        this.owner_number = owner_number;
        this.todaysDate=todaysDate;
        this.followUpDate=followUpDate;
       // this.geo_point = geo_point;
        this.person_We_Talked=person_We_Talked;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }

    public String getPerson_We_Talked() {
        return person_We_Talked;
    }

    public void setPerson_We_Talked(String person_We_Talked) {
        this.person_We_Talked = person_We_Talked;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTodaysDate() {
        return todaysDate;
    }

    public void setTodaysDate(String todaysDate) {
        this.todaysDate = todaysDate;
    }

    public String getFollowUpDate() {
        return followUpDate;
    }

    public void setFollowUpDate(String followUpDate) {
        this.followUpDate = followUpDate;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getField_description() {
        return field_description;
    }

    public void setField_description(String field_description) {
        this.field_description = field_description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getManager_name() {
        return manager_name;
    }

    public void setManager_name(String manager_name) {
        this.manager_name = manager_name;
    }

    public String getManager_number() {
        return manager_number;
    }

    public void setManager_number(String manager_number) {
        this.manager_number = manager_number;
    }

    public String getOwner_name() {
        return owner_name;
    }

    public void setOwner_name(String owner_name) {
        this.owner_name = owner_name;
    }

    public String getOwner_number() {
        return owner_number;
    }

    public void setOwner_number(String owner_number) {
        this.owner_number = owner_number;
    }

   // public GeoPoint getGeo_point() {
        //return geo_point;
   // }

    //public void setGeo_point(GeoPoint geo_point) {
       // this.geo_point = geo_point;
    //}
}
