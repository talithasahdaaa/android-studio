package com.example.gramedia.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.example.gramedia.R;
import com.example.gramedia.model.OrderItem;
import com.example.gramedia.model.Product;
import com.example.gramedia.api.RegisterAPI;
import com.example.gramedia.api.ServerAPI;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeProductAdapter extends RecyclerView.Adapter<HomeProductAdapter.ProductViewHolder> {

    private List<Product> productList = new ArrayList<>();
    private Context context;

    public void setProductList(List<Product> products) {
        this.productList = products;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.bind(product);
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct, imgBtnAddToCart, imgBtnDescription;
        TextView tvProductName, tvProductCategory, tvProductPrice;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.imgProduct);
            imgBtnAddToCart = itemView.findViewById(R.id.imgBtnAddToCart);
            imgBtnDescription = itemView.findViewById(R.id.imgBtnDescription);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvProductCategory = itemView.findViewById(R.id.tvProductCategory);
            tvProductPrice = itemView.findViewById(R.id.tvProductPrice);
        }

        public void bind(Product product) {
            RegisterAPI api = ServerAPI.getClient().create(RegisterAPI.class);

            tvProductName.setText(product.getMerk());
            tvProductCategory.setText("Kategori: " + product.getKategori());
            tvProductPrice.setText("Rp " + String.format("%,.0f", product.getHargajual()));

            String imageUrl = ServerAPI.BASE_URL_Image + product.getFoto();
            Glide.with(context)
                    .load(imageUrl)
                    .placeholder(R.color.primary)
                    .error(R.drawable.ic_produk_black_24dp)
                    .into(imgProduct);

            imgBtnAddToCart.setOnClickListener(v -> {
                if (product.getStok() > 0) {
                    saveProductToOrder(product);
                } else {
                    Toast.makeText(context, "Stok produk kosong", Toast.LENGTH_SHORT).show();
                }
            });

            imgBtnDescription.setOnClickListener(v -> {
                Call<ResponseBody> callUpdateView = api.updateView(product.getKode());
                callUpdateView.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            product.setViewCount(product.getViewCount() + 1);

                            Intent intent = new Intent(context, DetailActivity.class);
                            intent.putExtra("produk", product);
                            context.startActivity(intent);
                        } else {
                            Toast.makeText(context, "Gagal update view", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            });

        }

        private void saveProductToOrder(Product product) {
            SharedPreferences sharedPreferences = context.getSharedPreferences("OrderPrefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            String orderJson = sharedPreferences.getString("order_items", "[]");
            Type type = new TypeToken<List<OrderItem>>() {}.getType();
            List<OrderItem> orderItems;

            try {
                orderItems = new Gson().fromJson(orderJson, type);
                if (orderItems == null) orderItems = new ArrayList<>();
            } catch (Exception e) {
                orderItems = new ArrayList<>();
            }

            boolean productExists = false;
            for (OrderItem item : orderItems) {
                if (item.getMerk().equals(product.getMerk())) {
                    item.setQty(item.getQty() + 1);
                    productExists = true;
                    break;
                }
            }

            if (!productExists) {
                orderItems.add(new OrderItem(
                        product.getKode(),
                        product.getFoto(),
                        product.getMerk(),
                        product.getHargajual(),
                        product.getStok(),
                        1
                ));
            }

            String updatedOrderJson = new Gson().toJson(orderItems);
            editor.putString("order_items", updatedOrderJson);
            boolean success = editor.commit();

            if (success) {
                Toast.makeText(context, "Berhasil menambahkan ke keranjang", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Gagal menyimpan ke keranjang", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
