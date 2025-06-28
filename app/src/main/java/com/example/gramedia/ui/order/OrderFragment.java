package com.example.gramedia.ui.order;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gramedia.CheckoutActivity;
import com.example.gramedia.adapter.OrderAdapter;
import com.example.gramedia.R;
import com.example.gramedia.auth.LoginActivity;
import com.example.gramedia.databinding.FragmentOrderBinding;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class OrderFragment extends Fragment{

    private RecyclerView recyclerView;
    private OrderAdapter adapter;
    private List<OrderItem> orderItemList = new ArrayList<>();
    private FragmentOrderBinding binding;

    TextView tvTotalHarga;
    Button btnCheckout;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order, container, false);
        recyclerView = view.findViewById(R.id.rvCart);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Perbaiki di sini dengan menambahkan requireContext()
        adapter = new OrderAdapter(orderItemList, requireContext());
        recyclerView.setAdapter(adapter);

        tvTotalHarga = view.findViewById(R.id.tvTotalHarga);
        btnCheckout = view.findViewById(R.id.btnCheckout);

        // Set listener tombol checkout
        btnCheckout.setOnClickListener(v -> {
            SharedPreferences sp = requireContext().getSharedPreferences("user_session", Context.MODE_PRIVATE);
            boolean isLoggedIn = sp.contains("is_logged_in");
            Log.d("OrderFragment", "isLoggedIn: "+isLoggedIn);

            if (!isLoggedIn) {
                // Tampilkan dialog konfirmasi login dulu dan arahkan ke login
                new AlertDialog.Builder(requireContext())
                        .setTitle("Akses Ditolak")
                        .setMessage("Anda harus login terlebih dahulu untuk melakukan checkout.")
                        .setPositiveButton("Login", (dialog, which) -> {
                            Intent intent = new Intent(getActivity(), LoginActivity.class);
                            // Clear stack agar tidak bisa kembali ke OrderFragment tanpa login
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        })
                        .setNegativeButton("Batal", null)
                        .show();
            } else {
                // Jika sudah login, arahkan ke halaman Checkout
                Intent intent = new Intent(getActivity(), CheckoutActivity.class);
                startActivity(intent);
            }
        });


        adapter.setOnCartChangedListener(total -> {
            tvTotalHarga.setText(formatRupiah(total));
        });

        loadOrderItems(); // Load cart items from SharedPreferences

        return view;
    }

    private void loadOrderItems() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("OrderPrefs", Context.MODE_PRIVATE);
        String orderJson = sharedPreferences.getString("order_items", "[]");

        // Convert the JSON string back to a list of OrderItem objects
        Type type = new TypeToken<List<OrderItem>>() {}.getType();
        List<OrderItem> orderItems = new Gson().fromJson(orderJson, type);

        // Update the list and notify the adapter
        orderItemList.clear();
        orderItemList.addAll(orderItems);
        adapter.notifyDataSetChanged();

        updateTotal();
    }

    public void updateTotal() {
        double total = 0;
        for (OrderItem item : orderItemList) {
            total += item.getHargajual() * item.getQty();
        }
        tvTotalHarga.setText(formatRupiah(total));
    }
    private String formatRupiah(double harga) {
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        return formatRupiah.format(harga).replace(",00", "");
    }
}