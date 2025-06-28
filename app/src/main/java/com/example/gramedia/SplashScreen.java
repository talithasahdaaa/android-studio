package com.example.gramedia;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SplashScreen extends AppCompatActivity {
    private static final int SPLASH_DELAY = 1500; // 1.5 detik

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash_screen);
        getSupportActionBar().hide();
        ImageView imgHurufLogo = findViewById(R.id.imgHuruflogo);

        // Animasi turun ke bawah untuk gambar logo
        Animation animDown = AnimationUtils.loadAnimation(this, R.anim.fade_in_translate_down);
        imgHurufLogo.startAnimation(animDown);


        // Cek status login dari SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("user_session", Context.MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.contains("email");

        new Handler().postDelayed(() -> {
            Intent intent;
            if (isLoggedIn) {
                // Arahkan ke halaman utama (misalnya OrderFragment atau MainActivity)
                intent = new Intent(SplashScreen.this, MainActivity.class);  // Ganti dengan aktivitas beranda Anda
            } else {
                // Arahkan ke halaman login
                intent = new Intent(SplashScreen.this, MainActivity.class);
            }
            startActivity(intent);
            finish();
        }, SPLASH_DELAY);

    }
}