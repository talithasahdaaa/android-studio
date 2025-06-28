package com.example.gramedia.api;

import com.example.gramedia.model.Product;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface RegisterAPI {
    @FormUrlEncoded
    @POST("get_login.php")
    Call<ResponseBody> login(
            @Field("email") String email, // Changed from "nama" to "email"
            @Field("password") String password
    );

    @GET("get_profile.php")
    Call<ResponseBody> getProfile(
            @Query("email") String email
    );

    @FormUrlEncoded
    @POST("post_register.php")
    Call<ResponseBody> register(@Field("email") String email,
                                @Field("nama") String nama,
                                @Field("password") String password);

    @FormUrlEncoded
    @POST("post_profile.php")
    Call<ResponseBody> updateProfile(
            @Field("nama") String nama,
            @Field("alamat") String alamat,
            @Field("kota") String kota,
            @Field("provinsi") String provinsi,
            @Field("telp") String telp,
            @Field("kodepos") String kodepos,
            @Field("email") String email
    );

    @GET("get_produk.php") // Sesuaikan dengan URL API Anda
    Call<List<Product>> getProducts();



    @GET("get_view.php")
    Call<ResponseBody> getViewCount(
            @Query("kode") String kode
    );


    @FormUrlEncoded
    @POST("update_view.php")
    Call<ResponseBody> updateView(
            @Field("kode") String kode
    );

    @GET("get_produk_desc.php")
    Call<List<Product>> getBestSellerProducts();

    @Multipart
    @POST("upload_profile.php")
    Call<ResponseBody> uploadFoto(
            @Part("email") RequestBody email,
            @Part MultipartBody.Part image
    );

    @POST("post_change_password.php")
    Call<ResponseBody> changePassword(
            @Query("email") String email,
            @Query("old_password") String oldPassword,
            @Query("new_password") String newPassword
    );

    @Multipart
    @POST("post_order.php")
    Call<ResponseBody> createOrder(
            @Part("order") RequestBody order,
            @Part("order_detail") RequestBody orderDetail
    );

    @Multipart
    @POST("post_payment.php")
    Call<ResponseBody> uploadPaymentProof(
            @Part("order_id") RequestBody orderId,
            @Part MultipartBody.Part paymentProof
    );

    @GET("get_order_history.php")
    Call<OrderResponse> getOrderHistory(@Query("email") String email);

    @GET("get_order_detail.php")
    Call<OrderDetailApiResponse> getOrderDetail(@Query("order_id") int orderId);
}
