package com.example.gramedia.api;

import com.example.gramedia.ui.order.OrderModel;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface ApiService {

    @GET("get_order_history.php")
    Call<List<OrderModel>> getOrderHistory(@Query("user_id") String userId);

    @Multipart
    @POST("upload_bukti_pembayaran.php")
    Call<ResponseBody> uploadBukti(
            @Part("order_id") RequestBody orderId,
            @Part MultipartBody.Part bukti
    );
}
