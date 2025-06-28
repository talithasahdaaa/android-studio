package com.example.gramedia.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServerAPI {

    public static String BASE_URL="https://talita.umrmaulana.my.id/gramedia/";
    public static String BASE_URL_Image= BASE_URL + "img/";
//    public static String BASE_URL="http://10.0.2.2/gramedia/";
//    public static String BASE_URL_Image="http://10.0.2.2/gramedia/img/";

    private static Retrofit retrofit;

    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
