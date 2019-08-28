package com.example.user.mvvmcollabreusableapp.network.model;

import com.google.gson.annotations.SerializedName;

public class Ptype_Properties {

    @SerializedName("type")
    private String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
