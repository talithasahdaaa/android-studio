package com.example.gramedia;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.gramedia.api.RegisterAPI;
import com.example.gramedia.api.ServerAPI;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class OrderConfirmationActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView ivPaymentProof;
    private Button btnConfirmPayment;
    private Bitmap paymentProofBitmap;
    private int orderId;
    private double totalAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_confirmation);

        // Get order data from intent
        orderId = getIntent().getIntExtra("order_id", 0);
        totalAmount = getIntent().getDoubleExtra("total_amount", 0);

        // Initialize views
        ivPaymentProof = findViewById(R.id.ivPaymentProof);
        btnConfirmPayment = findViewById(R.id.btnConfirmPayment);
        TextView tvOrderId = findViewById(R.id.tvOrderId);
        TextView tvTotalAmount = findViewById(R.id.tvTotalAmount);

        // Set order info
        tvOrderId.setText(String.format("ORD-%d", orderId));
        tvTotalAmount.setText(String.format("Rp %,.0f", totalAmount));

        // Button listeners
        findViewById(R.id.btnSelectImage).setOnClickListener(v -> openImageChooser());
        btnConfirmPayment.setOnClickListener(v -> uploadPaymentProof());
    }

    private void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Payment Proof"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            try {
                paymentProofBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                ivPaymentProof.setImageBitmap(paymentProofBitmap);
                btnConfirmPayment.setEnabled(true);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void uploadPaymentProof() {
        if (paymentProofBitmap == null) {
            Toast.makeText(this, "Please select payment proof first", Toast.LENGTH_SHORT).show();
            return;
        }

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading payment proof...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        // Convert bitmap to byte array
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        paymentProofBitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
        byte[] imageBytes = byteArrayOutputStream.toByteArray();

        RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), imageBytes);
        MultipartBody.Part body = MultipartBody.Part.createFormData("payment_proof", "proof_" + orderId + ".jpg", requestFile);
        RequestBody orderIdBody = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(orderId));

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ServerAPI.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RegisterAPI api = retrofit.create(RegisterAPI.class);
        api.uploadPaymentProof(orderIdBody, body).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                progressDialog.dismiss();
                if (response.isSuccessful()) {
                    Toast.makeText(OrderConfirmationActivity.this, "Payment proof uploaded successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(OrderConfirmationActivity.this, OrderHistoryActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(OrderConfirmationActivity.this, "Failed to upload payment proof", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(OrderConfirmationActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}