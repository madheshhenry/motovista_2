package com.example.motovista_deep.api;

import com.example.motovista_deep.models.OcrResponse;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface OcrApiService {
    @Multipart
    @POST("ocr/engine-chassis")
    Call<OcrResponse> scanEngineChassis(@Part MultipartBody.Part image);
}
