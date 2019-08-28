package com.example.user.mvvmcollabreusableapp.network.model;

import com.google.gson.annotations.SerializedName;

class Parent {
    @SerializedName("_id")
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
