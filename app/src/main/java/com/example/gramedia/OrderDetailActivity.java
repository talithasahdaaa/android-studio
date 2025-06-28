package com.example.gramedia;


import static com.example.gramedia.api.ServerAPI.BASE_URL_Image;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.gramedia.adapter.OrderDetailAdapter;
import com.example.gramedia.api.OrderDetailApiResponse;
import com.example.gramedia.api.OrderResponse;
import com.example.gramedia.api.RegisterAPI;
import com.example.gramedia.api.ServerAPI;
import com.example.gramedia.model.OrderDetail;
import com.example.gramedia.model.OrderItem;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class OrderDetailActivity extends AppCompatActivity {

    private TextView tvOrderNumber, tvOrderDate, tvOrderStatus, tvOrderTotal, tvPaymentMethod;
    private TextView tvCustomerName, tvCustomerPhone, tvShippingAddress, tvShippingCity, tvShippingPostal;
    private TextView tvSubtotal, tvShippingCost, tvGrandTotal;
    private RecyclerView rvOrderItems;
    private CardView cardPaymentProof;
    private ImageView ivPaymentProof;
    private TextView tvPaymentDate;
    private Button btnSelectImage, btnConfirmPayment;

    private static final int PICK_IMAGE_REQUEST = 1;
    private Bitmap paymentProofBitmap;
    private int orderId;
    private OrderDetailAdapter adapter;
    private List<OrderItem> orderItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        // Get order ID from intent
        orderId = getIntent().getIntExtra("order_id", 0);
        if (orderId == 0) {
            Toast.makeText(this, "Order ID tidak valid", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize views
        initViews();

        // Load order details
        loadOrderDetails();
    }

    private void initViews() {
        tvOrderNumber = findViewById(R.id.tvOrderNumber);
        tvOrderDate = findViewById(R.id.tvOrderDate);
        tvOrderStatus = findViewById(R.id.tvOrderStatus);
        tvOrderTotal = findViewById(R.id.tvOrderTotal);
        tvPaymentMethod = findViewById(R.id.tvPaymentMethod);

        tvCustomerName = findViewById(R.id.tvCustomerName);
        tvCustomerPhone = findViewById(R.id.tvCustomerPhone);
        tvShippingAddress = findViewById(R.id.tvShippingAddress);
        tvShippingCity = findViewById(R.id.tvShippingCity);
        tvShippingPostal = findViewById(R.id.tvShippingPostal);

        tvSubtotal = findViewById(R.id.tvSubtotal);
        tvShippingCost = findViewById(R.id.tvShippingCost);
        tvGrandTotal = findViewById(R.id.tvGrandTotal);

        rvOrderItems = findViewById(R.id.rvOrderItems);
        rvOrderItems.setLayoutManager(new LinearLayoutManager(this));
        adapter = new OrderDetailAdapter(this, orderItems);
        rvOrderItems.setAdapter(adapter);

        cardPaymentProof = findViewById(R.id.cardPaymentProof);
        ivPaymentProof = findViewById(R.id.ivPaymentProof);
        tvPaymentDate = findViewById(R.id.tvPaymentDate);
        btnSelectImage = findViewById(R.id.btnSelectImage);
        btnConfirmPayment = findViewById(R.id.btnConfirmPayment);
    }

    private void loadOrderDetails() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Memuat detail pesanan...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        Log.d("OrderDetail", "Order ID: " + orderId);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ServerAPI.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RegisterAPI api = retrofit.create(RegisterAPI.class);
        api.getOrderDetail(orderId).enqueue(new Callback<OrderDetailApiResponse>() {
            @Override
            public void onResponse(Call<OrderDetailApiResponse> call, Response<OrderDetailApiResponse> response) {
                progressDialog.dismiss();
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    updateUI(response.body().getData());
                } else {
                    Toast.makeText(OrderDetailActivity.this, "Gagal memuat detail pesanan", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<OrderDetailApiResponse> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(OrderDetailActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void updateUI(OrderDetail orderDetail) {
        // Set order header info
        tvOrderNumber.setText(String.format("ORDER #%d", orderDetail.getOrderId()));
        tvOrderDate.setText(formatDate(orderDetail.getOrderDate()));
        tvOrderTotal.setText(String.format("Rp %,.0f", orderDetail.getTotalPayment()));
        tvPaymentMethod.setText(orderDetail.getPaymentMethod());

        // Set shipping address
        tvCustomerName.setText(orderDetail.getCustomerName());
        tvCustomerPhone.setText(orderDetail.getCustomerPhone());
        tvShippingAddress.setText(orderDetail.getShippingAddress());
        tvShippingCity.setText(orderDetail.getShippingCity());
        tvShippingPostal.setText(orderDetail.getShippingPostalCode());

        // Set order items
        // Set order items
        orderItems.clear();
        if (orderDetail.getItems() != null) {
            orderItems.addAll(orderDetail.getItems());
        }
        adapter.notifyDataSetChanged();

        // Set payment summary
        tvSubtotal.setText(String.format("Rp %,.0f", orderDetail.getSubtotal()));
        tvShippingCost.setText(String.format("Rp %,.0f", orderDetail.getShippingCost()));
        tvGrandTotal.setText(String.format("Rp %,.0f", orderDetail.getTotalPayment()));

        // Set order status with color
        switch (orderDetail.getStatus()) {
            case 0:
                tvOrderStatus.setText("Menunggu Pembayaran");
                tvOrderStatus.setTextColor(ContextCompat.getColor(this, R.color.colorWarning));
                break;
            case 1:
                tvOrderStatus.setText("Pembayaran Diterima");
                tvOrderStatus.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
                break;
            case 2:
                tvOrderStatus.setText("Pesanan Diproses");
                tvOrderStatus.setTextColor(ContextCompat.getColor(this, R.color.colorInfo));
                break;
            case 3:
                tvOrderStatus.setText("Pesanan Dikirim");
                tvOrderStatus.setTextColor(ContextCompat.getColor(this, R.color.colorSuccess));
                break;
            case 4:
                tvOrderStatus.setText("Pesanan Selesai");
                tvOrderStatus.setTextColor(ContextCompat.getColor(this, R.color.colorSuccessDark));
                break;
            default:
                tvOrderStatus.setText("Status Tidak Diketahui");
                tvOrderStatus.setTextColor(ContextCompat.getColor(this, R.color.colorGray));
        }

        // Show payment proof if not COD
        String paymentMethod = orderDetail.getPaymentMethod();
        if (paymentMethod != null && !paymentMethod.equalsIgnoreCase("COD")) {
            cardPaymentProof.setVisibility(View.VISIBLE);
            if (orderDetail.getPaymentProof() != null && !orderDetail.getPaymentProof().isEmpty()) {
                Glide.with(this)
                        .load(BASE_URL_Image + "proofs/" + orderDetail.getPaymentProof())
                        .into(ivPaymentProof);

                tvPaymentDate.setText(formatDate(orderDetail.getPaymentDate()));
            } else {
                btnSelectImage.setVisibility(View.VISIBLE);
                btnConfirmPayment.setVisibility(View.VISIBLE);
                findViewById(R.id.btnSelectImage).setOnClickListener(v -> openImageChooser());
                btnConfirmPayment.setOnClickListener(v -> uploadPaymentProof());
            }
        }
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

    private void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Payment Proof"), PICK_IMAGE_REQUEST);
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
                    Toast.makeText(OrderDetailActivity.this, "Payment proof uploaded successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(OrderDetailActivity.this, OrderHistoryActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(OrderDetailActivity.this, "Failed to upload payment proof", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(OrderDetailActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String formatDate(String dateString) {
        if (dateString == null || dateString.isEmpty()) {
            return "-";
        }
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
            Date date = inputFormat.parse(dateString);
            return outputFormat.format(date);
        } catch (ParseException e) {
            return dateString;
        }
    }
}