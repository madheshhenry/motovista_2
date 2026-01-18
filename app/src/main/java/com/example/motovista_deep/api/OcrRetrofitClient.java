package com.example.motovista_deep.api;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import com.google.gson.GsonBuilder;
import com.google.gson.Gson;

import java.util.concurrent.TimeUnit;

public class OcrRetrofitClient {
    // Port 5000 for the Flask OCR server
    // Use the actual IP address for physical devices on same network
    public static final String OCR_BASE_URL = "http://192.168.0.100:5000/";

    private static OcrApiService apiService;

    public static OcrApiService getOcrApiService() {
        if (apiService == null) {
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(loggingInterceptor)
                    .connectTimeout(120, TimeUnit.SECONDS) // OCR can be slow
                    .readTimeout(120, TimeUnit.SECONDS)
                    .writeTimeout(120, TimeUnit.SECONDS)
                    .build();

            Gson gson = new GsonBuilder().setLenient().create();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(OCR_BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();

            apiService = retrofit.create(OcrApiService.class);
        }
        return apiService;
    }
}
