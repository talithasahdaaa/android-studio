package com.example.gramedia.model;

import java.io.Serializable;

public class Product implements Serializable {
    private int qty = 1;

    public int getQty() {
        return qty;
    }
    private String kode;
    private String merk;
    private double hargajual;
    private int stok;
    private String foto;
    private String deskripsi;
    private String kategori;
    private  int view_count;
    private int viewCount;

    // Getters and Setters

    public String getKategori() { return kategori; }
    public String getKode() { return kode; }
    public String getMerk() { return merk; }
    public double getHargajual() { return hargajual; }
    public int getStok() { return stok; }
    public String getFoto() { return foto; }
    public String getDeskripsi() { return deskripsi; }
    public int getView() { return view_count; }
    public int getViewCount() {
        return viewCount;
    }

    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }
}
