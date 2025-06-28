package com.example.gramedia;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.gramedia.model.Product;
import com.example.gramedia.api.ServerAPI;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // Mengambil data produk dari Intent
        Product product = (Product) getIntent().getSerializableExtra("produk");

        // Inisialisasi komponen UI
        ImageView imgDetail = findViewById(R.id.imgDetail);
        TextView tvNamaDetail = findViewById(R.id.tvNamaDetail);
        TextView tvHargaDetail = findViewById(R.id.tvHargaDetail);
        TextView tvStokDetail = findViewById(R.id.tvStokDetail);
        TextView tvDilihat = findViewById(R.id.tvDilihat);
        TextView tvKategoriDetail = findViewById(R.id.tvKategoriDetail);
        TextView tvDeskripsiDetail = findViewById(R.id.tvDeskripsiDetail);
        Button btnKembali = findViewById(R.id.button);

        if (product != null) {
            int savedViewCount = product.getView()+1;
            // Menampilkan data produk pada UI
            Glide.with(this).load(ServerAPI.BASE_URL_Image + "product/" + product.getFoto()).into(imgDetail);
            tvNamaDetail.setText(product.getMerk());
            tvHargaDetail.setText(String.format("Rp %,.0f", product.getHargajual()));
            tvStokDetail.setText("Stok: " + product.getStok());
            tvDilihat.setText("Dilihat: " + savedViewCount+"x");
            tvKategoriDetail.setText("Kategori: " + product.getKategori());
            tvDeskripsiDetail.setText("Deskripsi: " + product.getDeskripsi());
        }

        // Menangani aksi klik tombol Kembali
        btnKembali.setOnClickListener(v -> {
            finish();
        });
    }
}