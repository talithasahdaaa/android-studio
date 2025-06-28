package com.example.gramedia;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gramedia.adapter.OrderHistoryAdapter;
import com.example.gramedia.api.OrderResponse;
import com.example.gramedia.api.RegisterAPI;
import com.example.gramedia.api.ServerAPI;
import com.example.gramedia.model.Order;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class OrderHistoryActivity extends AppCompatActivity {
    private RecyclerView rvOrderHistory;
    private OrderHistoryAdapter adapter;
    private List<Order> orderList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);

        rvOrderHistory = findViewById(R.id.rvOrderHistory);
        rvOrderHistory.setLayoutManager(new LinearLayoutManager(this));
        adapter = new OrderHistoryAdapter(this, orderList);
        rvOrderHistory.setAdapter(adapter);

        loadOrderHistory();
    }

    private void loadOrderHistory() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading order history...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        SharedPreferences sharedPreferences = getSharedPreferences("user_session", Context.MODE_PRIVATE);
        String email = sharedPreferences.getString("email", "");

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ServerAPI.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RegisterAPI api = retrofit.create(RegisterAPI.class);
        api.getOrderHistory(email).enqueue(new Callback<OrderResponse>() {
            @Override
            public void onResponse(Call<OrderResponse> call, Response<OrderResponse> response) {
                progressDialog.dismiss();
                if (response.isSuccessful() && response.body() != null) {
                    OrderResponse orderResponse = response.body();
                    if (orderResponse.isSuccess()) {
                        orderList.clear();
                        orderList.addAll(orderResponse.getData());
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(OrderHistoryActivity.this, orderResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(OrderHistoryActivity.this, "Failed to load order history", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<OrderResponse> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(OrderHistoryActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d("OrderHistory", "Error: " + t.getMessage());
            }
        });
    }
}