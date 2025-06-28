package com.example.gramedia.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.gramedia.DetailActivity;
import com.example.gramedia.api.Product;
import com.example.gramedia.R;
import com.example.gramedia.api.RegisterAPI;
import com.example.gramedia.api.ServerAPI;
import com.example.gramedia.ui.order.OrderItem;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {
    private List<Product> productList;
    private Context context;

    public ProductAdapter(List<Product> productList, Context context) {
        this.productList = productList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = productList.get(position);

        holder.tvMerk.setText(product.getMerk());
        holder.tvPrice.setText(String.format("Rp %,.0f", product.getHargajual()));
        holder.tvStock.setText("Stok: " + product.getStok());
        holder.tvDilihat.setText("Dilihat: " + product.getView()+"x");

        // Jika stok habis, ubah warna jadi merah
        if (product.getStok() <= 0) {
            holder.tvStock.setText("Stok: Habis");
            holder.tvStock.setTextColor(Color.RED);
            holder.imgbtnCart.setVisibility(View.INVISIBLE); // Sembunyikan tombol keranjang jika ingin
        } else {
            holder.tvStock.setTextColor(Color.rgb(0, 100, 0));
            holder.imgbtnCart.setVisibility(View.VISIBLE);
        }

        // Load gambar menggunakan Glide
        Glide.with(context)
                .load(ServerAPI.BASE_URL_Image + "product/" + product.getFoto())
                .into(holder.ivProduct);

        // Ketika tombol keranjang diklik
        holder.imgbtnCart.setOnClickListener(v -> {
            if (product.getStok() > 0) {
                OrderItem orderItem = new OrderItem(
                        product.getFoto(),
                        product.getMerk(),
                        product.getHargajual(),
                        product.getStok(),
                        1 // Default qty
                );

                saveProductToOrder(orderItem);
            } else {
                Toast.makeText(context, "Stok produk kosong", Toast.LENGTH_SHORT).show();
            }
        });

        // Pass the Product object using putExtra (no casting to CharSequence)
        holder.imgbtnDeskripsi.setOnClickListener(v -> {
            int currentViewCount = product.getView();
            currentViewCount++;

            // Update the view count in the database using the API
            RegisterAPI apiService = ServerAPI.getClient().create(RegisterAPI.class);
            Call<ResponseBody> call = apiService.postView(product.getKode(), currentViewCount);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {

                    } else {

                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {

                }
            });
            Intent intent = new Intent(context, DetailActivity.class);
            intent.putExtra("produk", product);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    private void saveProductToOrder(OrderItem orderItem) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("OrderPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        String orderJson = sharedPreferences.getString("order_items", "[]");
        Type type = new TypeToken<List<OrderItem>>() {}.getType();
        List<OrderItem> orderItems;

        try {
            orderItems = new Gson().fromJson(orderJson, type);
            if (orderItems == null) {
                orderItems = new ArrayList<>();
            }
        } catch (Exception e) {
            orderItems = new ArrayList<>();
            android.util.Log.e("ProductAdapter", "Gagal parsing JSON: " + e.getMessage());
        }

        boolean productExists = false;
        for (OrderItem item : orderItems) {
            if (item.getMerk().equals(orderItem.getMerk())) {
                item.setQty(item.getQty() + 1);
                productExists = true;
                break;
            }
        }

        if (!productExists) {
            orderItems.add(orderItem);
        }

        String updatedOrderJson = new Gson().toJson(orderItems);
        editor.putString("order_items", updatedOrderJson);

        boolean success = editor.commit();
        if (success) {
            Toast.makeText(context, "Berhasil menambahkan ke keranjang", Toast.LENGTH_SHORT).show();
            android.util.Log.d("ProductAdapter", "Data tersimpan ke SharedPreferences: " + updatedOrderJson);
        } else {
            Toast.makeText(context, "Gagal menyimpan ke keranjang", Toast.LENGTH_SHORT).show();
            android.util.Log.e("ProductAdapter", "Gagal menyimpan ke SharedPreferences");
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProduct, imgbtnCart, imgbtnDeskripsi;
        TextView tvMerk, tvPrice, tvStock, tvDilihat;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProduct = itemView.findViewById(R.id.imageView);
            tvMerk = itemView.findViewById(R.id.tvNama);
            tvPrice = itemView.findViewById(R.id.tvHarga);
            tvStock = itemView.findViewById(R.id.tvStok);
            imgbtnCart = itemView.findViewById(R.id.imgbtnCart);
            imgbtnDeskripsi = itemView.findViewById(R.id.imgbtnDeskripsi);
            tvDilihat = itemView.findViewById(R.id.tvDilihat);
        }
    }
}