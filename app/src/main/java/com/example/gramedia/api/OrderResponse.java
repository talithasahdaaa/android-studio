package com.example.gramedia.api;

import com.example.gramedia.model.Order;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class OrderResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("data")
    private List<Order> data;

    @SerializedName("message")
    private String message;

    // Getters
    public boolean isSuccess() { return success; }
    public List<Order> getData() { return data; }
    public String getMessage() { return message; }
}
