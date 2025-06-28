package com.example.gramedia;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gramedia.api.RegisterAPI;
import com.example.gramedia.api.ServerAPI;
import com.example.gramedia.databinding.ActivityChangePasswordBinding;

import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePasswordActivity extends AppCompatActivity {

    private ActivityChangePasswordBinding binding;
    private RegisterAPI api;

    SharedPreferences sharedPreferences;
    String email, oldPassword, newPassword, confirmNewPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChangePasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        api = ServerAPI.getClient().create(RegisterAPI.class);

        sharedPreferences = getSharedPreferences("user_session", Context.MODE_PRIVATE);
        email = sharedPreferences.getString("email", "");

        binding.btnSubmit.setOnClickListener(v -> {
            updatePassword();
        });

        binding.btnBack.setOnClickListener(v -> {
            finish();
        });
    }

    private void updatePassword() {
        oldPassword = binding.etOldPassword.getText().toString();
        newPassword = binding.etNewPassword.getText().toString();
        confirmNewPassword = binding.etConfirmNewPassword.getText().toString();

        if (oldPassword.isEmpty() || newPassword.isEmpty() || confirmNewPassword.isEmpty()) {
            Toast.makeText(this, "Semua field harus diisi", Toast.LENGTH_SHORT).show();
        }

        if (!newPassword.equals(confirmNewPassword)) {
            Toast.makeText(this, "Konfirmasi password tidak sesuai", Toast.LENGTH_SHORT).show();
        }

        Call<ResponseBody> call = api.changePassword(email, oldPassword, newPassword);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.isSuccessful() && response.body() != null) {
                        String result = response.body().string();
                        JSONObject json = new JSONObject(result);
                        String status = json.getString("status");
                        String message = json.getString("message");

                        Toast.makeText(ChangePasswordActivity.this, message, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ChangePasswordActivity.this, "Gagal menghubungi server", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(ChangePasswordActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}