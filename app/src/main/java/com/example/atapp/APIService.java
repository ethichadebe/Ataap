package com.example.atapp;

import retrofit2.Call;
import retrofit2.http.GET;

public interface APIService {
    @GET("getArt")
    Call<Art> getArt();
}
