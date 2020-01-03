package com.istvn.speechrecognitionsystem.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Client Server Communication class
 */
public class RetrofitClientInstance {
    /**
     * For static method we need static variable/object/instance
     * We have used these variable in static method
     */
    private static Retrofit retrofit;

    // Predefined BASE_URL
    private static final String BASE_URL = "http://192.168.100.74:8080/";

    /**
     * static -> So that we can access this method from all class
     * This method will create retrofit instance according to Predefined BASE_URL
     * @return  -> Instance of retrofit
     */
    public static Retrofit getRetrofitInstance() {

        retrofit = new retrofit2.Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit;
    }
}
