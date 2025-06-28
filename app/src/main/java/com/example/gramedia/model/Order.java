package com.example.gramedia.model;

import com.google.gson.annotations.SerializedName;

public class Order {
    @SerializedName("order_id")
    private int orderId;

    @SerializedName("date")
    private String date;

    @SerializedName("total")
    private double total;

    @SerializedName("status")
    private int status;

    @SerializedName("item_count")
    private int itemCount;

    // Constructor
    public Order(int orderId, String date, double total, int status, int itemCount) {
        this.orderId = orderId;
        this.date = date;
        this.total = total;
        this.status = status;
        this.itemCount = itemCount;
    }

    // Getters
    public int getOrderId() { return orderId; }
    public String getDate() { return date; }
    public double getTotal() { return total; }
    public int getStatus() { return status; }
    public int getItemCount() { return itemCount; }
}
