package com.example.gramedia.ui.home;

import static com.example.gramedia.api.ServerAPI.BASE_URL_Image;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;

import com.example.gramedia.adapter.BestSellerAdapter;
import com.example.gramedia.adapter.HomeProductAdapter;
import com.example.gramedia.model.Product;
import com.example.gramedia.R;
import com.example.gramedia.api.RegisterAPI;
import com.example.gramedia.api.ServerAPI;
import com.example.gramedia.databinding.FragmentHomeBinding;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;

    private HomeViewModel mViewModel;
    private HomeProductAdapter homeProductAdapter;
    private BestSellerAdapter bestSellerAdapter;
    private SharedPreferences sharedPreferences;
    private RegisterAPI api;

    private List<Product> allProducts = new ArrayList<>();
    private List<Product> filteredProducts = new ArrayList<>();

    private String email;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        api = ServerAPI.getClient().create(RegisterAPI.class);

        ImageSlider imageSlider = binding.imageSlider.findViewById(R.id.image_slider);
        List<SlideModel> imageList = new ArrayList<>();
        imageList.add(new SlideModel(R.drawable.slider1, ScaleTypes.FIT));
        imageList.add(new SlideModel(R.drawable.slider2, ScaleTypes.FIT));
        imageList.add(new SlideModel(R.drawable.slider3, ScaleTypes.FIT));
        imageSlider.setImageList(imageList, ScaleTypes.FIT);

        binding.rvProduk.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        homeProductAdapter = new HomeProductAdapter();
        binding.rvProduk.setAdapter(homeProductAdapter);

        binding.rvBestSeller.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        bestSellerAdapter = new BestSellerAdapter();
        binding.rvBestSeller.setAdapter(bestSellerAdapter);

        loadProfile();
        loadAllProducts();
        loadBestSellerProducts();

        return root;
    }

    private void loadProfile() {
        Log.d("HomeFragment", "Loading profile...");
        sharedPreferences = requireActivity().getSharedPreferences("user_session", Context.MODE_PRIVATE);
        email = sharedPreferences.getString("email", "Guest@gmail.com");

        RegisterAPI api = ServerAPI.getClient().create(RegisterAPI.class);
        api.getProfile(email).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.isSuccessful() && response.body() != null) {
                        JSONObject json = new JSONObject(response.body().string());
                        if (json.getInt("result") == 1) {
                            JSONObject data = json.getJSONObject("data");
                            updateUI(
                                    data.getString("nama"),
                                    data.getString("foto")
                            );
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    showError("Gagal memuat data profil");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                showError("Koneksi gagal: " + t.getMessage());
            }
        });
    }

    private void updateUI(String nama, String foto) {
        binding.tvGreeting.setText("Hai " + nama + " \uD83D\uDC4B");
        Glide.with(requireContext())
                .load(BASE_URL_Image + "avatar/" + foto)
                .centerCrop()
                .placeholder(R.drawable.ic_profile_black_24dp)
                .error(R.drawable.ic_profile_black_24dp)
                .into(binding.imgProfile);
    }

    private void showError(String message) {
        if (isAdded()) { // Check if the fragment is attached to an activity
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
        } else {
            Log.e("ProfileFragment", "Fragment not attached to context. Error: " + message);
        }
    }

    private void loadAllProducts() {
        api.getProducts().enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    allProducts.clear();
                    allProducts.addAll(response.body());

                    // Setup kategori Spinner
                    Set<String> kategoriSet = new HashSet<>();
                    for (Product p : allProducts) {
                        if (p.getKategori() != null) {
                            kategoriSet.add(p.getKategori());
                        }
                    }

                    List<String> kategoriList = new ArrayList<>();
                    kategoriList.add("Semua");
                    kategoriList.addAll(kategoriSet);

                    ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, kategoriList);
                    spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    binding.spinnerKategori.setAdapter(spinnerAdapter);

                    // Default tampilkan semua produk
                    filterProducts("Semua");

                } else {
                    Toast.makeText(getContext(), "Gagal memuat produk", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadBestSellerProducts() {
        api.getBestSellerProducts().enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    bestSellerAdapter.setProductList(response.body());
                } else {
                    Toast.makeText(getContext(), "Gagal memuat best seller", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterProducts(String kategori) {
        filteredProducts.clear();
        for (Product p : allProducts) {
            String kat = p.getKategori() != null ? p.getKategori() : "";
            boolean matchKategori = kategori.equals("Semua") || kat.equalsIgnoreCase(kategori);
            if (matchKategori) {
                filteredProducts.add(p);
            }
        }
        homeProductAdapter.setProductList(filteredProducts);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
    }
}
