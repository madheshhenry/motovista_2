package com.example.motovista_deep.api;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import com.google.gson.GsonBuilder;
import com.google.gson.Gson;

public class RetrofitClient {

    public static final String BASE_URL = "http://192.168.0.104/motovista_api/";
    private static ApiService apiService;

    public static ApiService getApiService() {
        if (apiService == null) {
            // Add logging to see requests/responses
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(loggingInterceptor)
                    .build();

            // Create Gson with lenient mode
            Gson gson = new GsonBuilder()
                    .setLenient()  // This allows malformed JSON
                    .create();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
            apiService = retrofit.create(ApiService.class);
        }
        return apiService;
    }
}