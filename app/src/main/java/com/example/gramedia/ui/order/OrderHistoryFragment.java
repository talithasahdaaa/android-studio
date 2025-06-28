package com.example.gramedia.ui.order;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gramedia.R;
import com.example.gramedia.api.ApiService;
import com.example.gramedia.api.ServerAPI;
import com.example.gramedia.utils.FileUtils;

import java.io.File;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderHistoryFragment extends Fragment {

    private RecyclerView recyclerView;
    private OrderHistoryAdapter adapter;
    private static final int PICK_IMAGE_REQUEST = 1;
    private OrderModel selectedOrder;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recyclerOrder);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        loadOrderHistory();
    }

    private void loadOrderHistory() {
        String userId = "123456"; // nanti ambil dari sharedPreferences kamu
        ApiService apiService = ServerAPI.getClient().create(ApiService.class);
        Call<List<OrderModel>> call = apiService.getOrderHistory(userId);
        call.enqueue(new Callback<List<OrderModel>>() {
            @Override
            public void onResponse(Call<List<OrderModel>> call, Response<List<OrderModel>> response) {
                if (response.isSuccessful()) {
                    List<OrderModel> list = response.body();
                    adapter = new OrderHistoryAdapter(getContext(), list);
                    adapter.setOnUploadClickListener(model -> {
                        selectedOrder = model;
                        pickImage();
                    });
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<List<OrderModel>> call, Throwable t) {
                Toast.makeText(getContext(), "Gagal koneksi", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void pickImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            Uri uri = data.getData();
            uploadImage(uri);
        }
    }

    private void uploadImage(Uri uri) {
        File file = new File(FileUtils.getPath(getContext(), uri));
        RequestBody requestOrderId = RequestBody.create(MediaType.parse("text/plain"), selectedOrder.nomer_id);
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("bukti", file.getName(), requestFile);

        ApiService apiService = ServerAPI.getClient().create(ApiService.class);
        Call<ResponseBody> call = apiService.uploadBukti(requestOrderId, body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Toast.makeText(getContext(), "Upload berhasil!", Toast.LENGTH_SHORT).show();
                loadOrderHistory();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getContext(), "Upload gagal", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
