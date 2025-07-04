package com.example.gramedia.auth;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gramedia.MainActivity;
import com.example.gramedia.R;
import com.example.gramedia.api.RegisterAPI;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.gramedia.api.ServerAPI;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {

    public static final String URL = new ServerAPI().BASE_URL;

    private ProgressDialog pd;
    private TextInputEditText etEmail, etPassword;
    private MaterialButton btnLogin;
    private TextView tvRegister, tv_guest_login;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        // Cek apakah user sudah login
        sharedPreferences = getSharedPreferences("login_session", MODE_PRIVATE);

        // Inisialisasi View
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvRegister);
        tv_guest_login = findViewById(R.id.tvGuestLogin);

        // Klik "Daftar"
        tvRegister.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });

        // Klik "Login sebagai tamu"
        tv_guest_login.setOnClickListener(view -> {
            // Simpan status login tamu
            editor = sharedPreferences.edit();
            editor.putBoolean("isLoggedIn", true);
            editor.putString("email", "Guest");
            editor.apply();

            // Buka MainActivity
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        });

        // Klik Login
        btnLogin.setOnClickListener(v -> {
            pd = new ProgressDialog(LoginActivity.this);
            pd.setTitle("Proses Login...");
            pd.setMessage("Tunggu Sebentar...");
            pd.setCancelable(true);
            pd.setIndeterminate(true);
            prosesLogin(
                    etEmail.getText().toString().trim(),
                    etPassword.getText().toString().trim()
            );
        });

        // Set Edge-to-Edge padding
        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.main),
                (v, insets) -> {
                    Insets sysBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                    v.setPadding(sysBars.left, sysBars.top, sysBars.right, sysBars.bottom);
                    return insets;
                }
        );
    }

    private void prosesLogin(String email, String password) {
        pd.show();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RegisterAPI api = retrofit.create(RegisterAPI.class);

        api.login(email, password).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                pd.dismiss();
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        JSONObject json = new JSONObject(response.body().string());
                        if ("1".equals(json.optString("result"))) {
                            JSONObject data = json.getJSONObject("data");
                            if ("1".equals(data.optString("status"))) {
                                // ✅ Simpan ke SharedPreferences
                                SharedPreferences sharedPreferences = getSharedPreferences("user_session", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("email", data.optString("email"));
                                editor.putString("nama", data.optString("nama"));
                                editor.putBoolean("is_logged_in", true);
                                editor.apply();

                                Toast.makeText(LoginActivity.this, "Login Berhasil", Toast.LENGTH_SHORT).show();

                                // Pindah ke Home (MainActivity yang berisi OrderFragment)
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();

                            } else {
                                showError("Status User Ini Tidak Aktif");
                            }
                        } else {
                            showError("Email atau Password Salah");
                        }
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                        showError("Terjadi Kesalahan: " + e.getMessage());
                    }
                } else {
                    showError("Terjadi Kesalahan pada Server");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                pd.dismiss();
                Log.e("LoginError", t.toString());
                showError("Gagal Terhubung ke Server: " + t.getMessage());
            }
        });
    }

    private void showError(String message) {
        new AlertDialog.Builder(LoginActivity.this)
                .setMessage(message)
                .setNegativeButton("Retry", null)
                .create()
                .show();
    }

}