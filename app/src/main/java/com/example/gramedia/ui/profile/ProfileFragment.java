package com.example.gramedia.ui.profile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.gramedia.auth.LoginActivity;
import com.example.gramedia.R;
import com.example.gramedia.databinding.FragmentProfileBinding;


public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ProfileViewModel profileViewModel =
                new ViewModelProvider(this).get(ProfileViewModel.class);

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        SharedPreferences sharedPreferences = requireActivity()
                .getSharedPreferences("user_session", Context.MODE_PRIVATE);
        String nama = sharedPreferences.getString("nama", "Guest");
        String email = sharedPreferences.getString("email", "Guest");
        if ("Guest".equals(nama)) {
            requireActivity().getSharedPreferences("user_session", Context.MODE_PRIVATE)
                    .edit()
                    .clear()
                    .apply();
            requireActivity().getSharedPreferences("login_session", Context.MODE_PRIVATE)
                    .edit()
                    .clear()
                    .apply();
            Intent intent = new Intent(requireContext(), LoginActivity.class);
            startActivity(intent);
            requireActivity().finish();
        }
        binding.txtWelcome.setText(nama);
        binding.editProfile.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
            navController.navigate(com.example.gramedia.R.id.action_profileFragment_to_editProfileFragment);
        });

        binding.btnKontak.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
            navController.navigate(R.id.action_profileFragment_to_kontakFragment);
        });

        binding.btnLogout.setOnClickListener(v -> {
            // Menghapus sesi login
            requireActivity().getSharedPreferences("user_session", Context.MODE_PRIVATE)
                    .edit()
                    .clear()
                    .apply();
            requireActivity().getSharedPreferences("login_session", Context.MODE_PRIVATE)
                    .edit()
                    .clear()
                    .apply();
            // Mengarahkan ke LoginActivity
            Intent intent = new Intent(requireContext(), LoginActivity.class);
            Log.d("ProfileFragment", "Logging out and redirecting to LoginActivity");
            startActivity(intent);
            requireActivity().finish();
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}