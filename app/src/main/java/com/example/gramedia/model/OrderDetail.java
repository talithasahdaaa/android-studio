package com.example.gramedia.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class OrderDetail {
    @SerializedName("order_id")
    private int orderId;

    @SerializedName("order_date")
    private String orderDate;

    @SerializedName("status")
    private int status;

    @SerializedName("payment_method")
    private String paymentMethod;

    @SerializedName("payment_proof")
    private String paymentProof;

    @SerializedName("payment_date")
    private String paymentDate;

    @SerializedName("customer_name")
    private String customerName;

    @SerializedName("customer_phone")
    private String customerPhone;

    @SerializedName("shipping_address")
    private String shippingAddress;

    @SerializedName("shipping_city")
    private String shippingCity;

    @SerializedName("shipping_postal_code")
    private String shippingPostalCode;

    @SerializedName("subtotal")
    private double subtotal;

    @SerializedName("shipping_cost")
    private double shippingCost;

    @SerializedName("total_payment")
    private double totalPayment;

    @SerializedName("items")
    private List<OrderItem> items;

    // Getter and Setter
    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getPaymentProof() {
        return paymentProof;
    }

    public void setPaymentProof(String paymentProof) {
        this.paymentProof = paymentProof;
    }

    public String getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(String paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public String getShippingCity() {
        return shippingCity;
    }

    public void setShippingCity(String shippingCity) {
        this.shippingCity = shippingCity;
    }

    public String getShippingPostalCode() {
        return shippingPostalCode;
    }

    public void setShippingPostalCode(String shippingPostalCode) {
        this.shippingPostalCode = shippingPostalCode;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    public double getShippingCost() {
        return shippingCost;
    }

    public void setShippingCost(double shippingCost) {
        this.shippingCost = shippingCost;
    }

    public double getTotalPayment() {
        return totalPayment;
    }

    public void setTotalPayment(double totalPayment) {
        this.totalPayment = totalPayment;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }
}
