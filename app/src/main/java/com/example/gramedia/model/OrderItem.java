package com.example.gramedia.model;

import com.google.gson.annotations.SerializedName;

public class OrderItem {
    @SerializedName("product_id")
    private String kode;

    @SerializedName("product_image")
    private String foto;

    @SerializedName("product_name")
    private String merk;

    @SerializedName("price")
    private double hargajual;

    // Tidak ada field stok di JSON, biarkan default 0
    private int stok;

    @SerializedName("quantity")
    private int qty;

    // Constructor
    public OrderItem(String kode, String foto, String merk, double hargajual, int stok, int qty) {
        this.kode = kode;
        this.foto = foto;
        this.merk = merk;
        this.hargajual = hargajual;
        this.stok = stok;
        this.qty = qty;
    }

    // Getters and Setters
    public String getKode() { return kode; }
    public String getFoto() { return foto; }
    public String getMerk() { return merk; }
    public double getHargajual() { return hargajual; }
    public int getStok() { return stok; }
    public int getQty() { return qty; }
    public void setQty(int qty) { this.qty = qty; }

    // Calculate total price based on quantity
    public double getTotal() {
        return hargajual * qty;
    }
}
