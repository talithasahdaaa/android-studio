package com.example.gramedia.api;

import com.example.gramedia.model.OrderDetail;
import com.google.gson.annotations.SerializedName;

public class OrderDetailApiResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private OrderDetail data;

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public OrderDetail getData() { return data; }
}