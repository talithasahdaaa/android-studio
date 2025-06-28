package com.example.gramedia.ui.order;

public class OrderItem {
    private String foto;
    private String merk;
    private double hargajual;
    private int stok;
    private int qty;


    // Konstruktor dengan parameter
    public OrderItem(String foto, String merk, double hargajual, int stok, int qty) {
        this.foto = foto;
        this.merk = merk;
        this.hargajual = hargajual;
        this.stok = stok;
        this.qty = qty;
    }

    public String getFoto() { return foto; }
    public String getMerk() { return merk; }
    public double getHargajual() { return hargajual; }
    public int getStok() { return stok; }
    public int getQty() { return qty; }

    public void setQty(int qty) { this.qty = qty; }
}