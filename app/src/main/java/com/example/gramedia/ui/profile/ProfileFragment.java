package com.example.gramedia.ui.profile;

import static com.example.gramedia.api.ServerAPI.BASE_URL_Image;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.example.gramedia.ChangePasswordActivity;
import com.example.gramedia.EditProfileActivity;
import com.example.gramedia.KontakActivity;
import com.example.gramedia.OrderHistoryActivity;
import com.example.gramedia.api.RegisterAPI;
import com.example.gramedia.api.ServerAPI;
import com.example.gramedia.auth.LoginActivity;
import com.example.gramedia.R;
import com.example.gramedia.databinding.FragmentProfileBinding;

import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;

    private SharedPreferences sharedPreferences;

    private RegisterAPI api;
    private String email,nama;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ProfileViewModel profileViewModel =
                new ViewModelProvider(this).get(ProfileViewModel.class);

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        api = ServerAPI.getClient().create(RegisterAPI.class);

        sharedPreferences = requireActivity().getSharedPreferences("user_session", Context.MODE_PRIVATE);
        nama = sharedPreferences.getString("nama", null);
        email = sharedPreferences.getString("email", null);

        // Jika belum login (nama kosong atau null)
        if (nama == null || nama.isEmpty()) {
            // Tampilkan alert dialog
            new AlertDialog.Builder(requireContext())
                    .setTitle("Peringatan")
                    .setMessage("Anda harus login dulu untuk mengakses halaman ini.")
                    .setCancelable(false) // Tidak bisa dismiss tanpa klik tombol
                    .setPositiveButton("OK", (dialog, which) -> {
                        // Arahkan ke halaman login dan hapus history activity
                        Intent intent = new Intent(requireActivity(), LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        requireActivity().finish();
                    })
                    .show();

            // Kembalikan view kosong agar tidak lanjut render UI profil
            return new View(requireContext());
        }

        loadProfile();

        // Tombol Edit Profile
        binding.btnEditProfile.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), EditProfileActivity.class);
            startActivity(intent);
        });

        // Tombol Ganti Password
        binding.btnChangePassword.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ChangePasswordActivity.class);
            startActivity(intent);
        });

        // Tombol Order History
        binding.btnOrderHistory.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), OrderHistoryActivity.class);
            startActivity(intent);
        });

        // Tombol Kontak Kami
        binding.btnContactUs.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), KontakActivity.class);
            startActivity(intent);
        });

        // Tombol Logout
        binding.btnLogout.setOnClickListener(v -> {
            new AlertDialog.Builder(getActivity())
                    .setTitle("Logout")
                    .setMessage("Apakah kamu yakin ingin logout?")
                    .setPositiveButton("Ya", (dialog, which) -> {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.clear();
                        editor.apply();

                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        getActivity().finish();
                    })
                    .setNegativeButton("Batal", null)
                    .show();
        });

        return root;
    }

    private void loadProfile() {
        api.getProfile(email).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.isSuccessful() && response.body() != null) {
                        JSONObject json = new JSONObject(response.body().string());
                        if (json.getInt("result") == 1) {
                            JSONObject data = json.getJSONObject("data");
                            updateUI(
                                    data.getString("email"),
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
//                binding.loadingProgressBar.setVisibility(View.GONE);
                showError("Koneksi gagal: " + t.getMessage());
            }
        });
    }

    private void updateUI(String email, String foto) {
        binding.tvEmail.setText(email);
        Glide.with(requireContext())
                .load(BASE_URL_Image + "avatar/" + foto)
                .centerCrop()
                .placeholder(R.drawable.ic_profile_black_24dp)
                .error(R.drawable.ic_profile_black_24dp)
                .into(binding.imgProfilePicture);
    }

    private void showError(String message) {
        if (isAdded()) { // Check if the fragment is attached to an activity
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
        } else {
            Log.e("ProfileFragment", "Fragment not attached to context. Error: " + message);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}