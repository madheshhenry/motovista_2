package com.example.motovista_deep.api;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import com.google.gson.GsonBuilder;
import com.google.gson.Gson;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.lang.reflect.Type;

public class RetrofitClient {

    public static final String BASE_URL = "http://192.168.0.104/motovista_api/";
    private static ApiService apiService;

    public static ApiService getApiService() {
        if (apiService == null) {
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(loggingInterceptor)
                    .build();

            // Create Gson with custom type adapter for boolean
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .registerTypeAdapter(Boolean.class, new BooleanTypeAdapter())
                    .registerTypeAdapter(boolean.class, new BooleanTypeAdapter())
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

    // Custom Boolean type adapter to handle 0/1 from PHP
    static class BooleanTypeAdapter extends TypeAdapter<Boolean> {
        @Override
        public void write(JsonWriter out, Boolean value) throws IOException {
            if (value == null) {
                out.nullValue();
            } else {
                out.value(value);
            }
        }

        @Override
        public Boolean read(JsonReader in) throws IOException {
            switch (in.peek()) {
                case BOOLEAN:
                    return in.nextBoolean();
                case NUMBER:
                    int intValue = in.nextInt();
                    return intValue == 1;
                case STRING:
                    String stringValue = in.nextString();
                    if (stringValue.equals("1") || stringValue.equalsIgnoreCase("true")) {
                        return true;
                    } else if (stringValue.equals("0") || stringValue.equalsIgnoreCase("false")) {
                        return false;
                    }
                    return false;
                case NULL:
                    in.nextNull();
                    return false;
                default:
                    in.skipValue();
                    return false;
            }
        }
    }
}