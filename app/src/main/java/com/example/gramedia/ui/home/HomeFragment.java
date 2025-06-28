package com.example.gramedia.ui.home;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;

import com.example.gramedia.DetailActivity;
import com.example.gramedia.api.Product;
import com.example.gramedia.adapter.ProductAdapter;
import com.example.gramedia.R;
import com.example.gramedia.api.RegisterAPI;
import com.example.gramedia.api.ServerAPI;
import com.example.gramedia.adapter.SuggestionAdapter;
import com.example.gramedia.databinding.FragmentHomeBinding;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    private ProductAdapter adapter;
    private RegisterAPI api;
    private List<Product> allProducts = new ArrayList<>();
    private List<Product> filteredSuggestions = new ArrayList<>();
    private SuggestionAdapter suggestionAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("user_session", 0);
        String nama = sharedPreferences.getString("nama", "Guest");
        binding.tvGreeting.setText("Hai, " + nama + "\uD83D\uDC4B");

        ImageSlider imageSlider = binding.imageSlider.findViewById(R.id.image_slider);
        List<SlideModel> imageList = new ArrayList<>();
        imageList.add(new SlideModel(R.drawable.slider1, ScaleTypes.FIT));
        imageList.add(new SlideModel(R.drawable.slider2, ScaleTypes.FIT));
        imageList.add(new SlideModel(R.drawable.slider3, ScaleTypes.FIT));
        imageSlider.setImageList(imageList, ScaleTypes.FIT);

        recyclerViewRekomendasi();

        recyclerViewSuggestion();

        return root;
    }

    private void recyclerViewRekomendasi() {
        binding.recyclerViewRekomendasi.setLayoutManager(new GridLayoutManager(getContext(), 2));
        adapter = new ProductAdapter(allProducts, getContext());
        binding.recyclerViewRekomendasi.setAdapter(adapter);

        loadProducts();
    }

    private void loadProducts() {
        api = ServerAPI.getClient().create(RegisterAPI.class);
        Call<List<Product>> call = api.getProducts();

        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    allProducts.clear();
                    allProducts.addAll(response.body());
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getContext(), "Gagal memuat data produk", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void recyclerViewSuggestion() {
        suggestionAdapter = new SuggestionAdapter(filteredSuggestions, product -> {
            // Arahkan ke detail
            Intent intent = new Intent(getContext(), DetailActivity.class);
            intent.putExtra("produk", product);
            startActivity(intent);

            binding.recyclerViewSuggestions.setVisibility(View.GONE);
        });

        binding.recyclerViewSuggestions.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewSuggestions.setHasFixedSize(true);
        binding.recyclerViewSuggestions.setNestedScrollingEnabled(false);
        binding.recyclerViewSuggestions.setAdapter(suggestionAdapter);

        // Load semua produk dari API
        fetchAllProducts();

        // Saat user mengetik
        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String keyword = s.toString().trim();
                if (!keyword.isEmpty()) {
                    filterSuggestions(keyword);
                } else {
                    filteredSuggestions.clear();
                    suggestionAdapter.updateList(filteredSuggestions);
                    binding.recyclerViewSuggestions.setVisibility(View.GONE);
                }
            }
        });
    }

    private void fetchAllProducts() {
        RegisterAPI api = ServerAPI.getClient().create(RegisterAPI.class);
        Call<List<Product>> call = api.getProducts();
        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    allProducts = response.body();
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Toast.makeText(getContext(), "Gagal fetch produk", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterSuggestions(String keyword) {
        List<Product> filtered = new ArrayList<>();
        for (Product p : allProducts) {
            if (p.getMerk().toLowerCase().contains(keyword.toLowerCase())) {
                filtered.add(p);
            }
        }
        if (!filtered.isEmpty()) {
            binding.recyclerViewSuggestions.setVisibility(View.VISIBLE);
        } else {
            binding.recyclerViewSuggestions.setVisibility(View.GONE);
        }
        suggestionAdapter.updateList(filtered);
    }
}
