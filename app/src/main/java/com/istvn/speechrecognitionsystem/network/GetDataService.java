package com.istvn.speechrecognitionsystem.network;

import com.istvn.speechrecognitionsystem.BuildConfig;
import com.istvn.speechrecognitionsystem.R;
import com.istvn.speechrecognitionsystem.model.AudioUploadResponse;
import com.istvn.speechrecognitionsystem.model.LoginResponse;
import com.istvn.speechrecognitionsystem.model.RecognitionListResponse;
import com.istvn.speechrecognitionsystem.model.RecognitionViewResponse;

import java.io.File;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface GetDataService {

    @Headers("x-api-key: " + BuildConfig.API_KEY)
    @POST("/api-v1/users/")
    @FormUrlEncoded
    Call<LoginResponse> registerUser(@Field("email") String email,
                                @Field("password") String password,
                                @Field("login_type") int login_type);

    @Headers("x-api-key: " + BuildConfig.API_KEY)
    @GET()
    Call<LoginResponse>getUser(@Url String url);

    @Headers("x-api-key: " + BuildConfig.API_KEY)
    @GET()
    Call<LoginResponse>isExistUser(@Url String url);

    @Headers("x-api-key: " + BuildConfig.API_KEY)
    @POST()
    Call<LoginResponse>passwordResetRequest(@Url String url);

    @Headers("x-api-key: " + BuildConfig.API_KEY)
    @PUT()
    @FormUrlEncoded
    Call<LoginResponse>userUpdate(@Url String url, @Field("name") String name,
                                  @Field("email") String email,
                                  @Field("phone") String phone,
                                  @Field("password") String password);

    @Headers("x-api-key: " + BuildConfig.API_KEY)
    @GET()
    Call<RecognitionListResponse>getRecognitionList(@Url String url);


    @Headers("x-api-key: "+ BuildConfig.API_KEY)
    @GET()
    Call<RecognitionViewResponse>getRecognitiobByID(@Url String url);

    @Headers("x-api-key: " + BuildConfig.API_KEY)
    @DELETE()
    Call<LoginResponse>deleteVoiceRecognition(@Url String url);

    @Headers("x-api-key: " + BuildConfig.API_KEY)
    @Multipart
    @POST("/api-v1/voiceReqs/")
    Call<AudioUploadResponse>audioUpload(@Part("user_id") RequestBody user_id,
                                         @Part("record_type") RequestBody record_type,
                                         @Part("record_end_time") RequestBody record_end_time,
                                         @Part("record_start_time") RequestBody record_start_time,
                                         @Part MultipartBody.Part file);

    @Headers("x-api-key: " + BuildConfig.API_KEY)
    @Multipart
    @POST("/api-v1/voiceReqs/")
    Call<AudioUploadResponse>callRecordUpload(@Part("user_id") RequestBody user_id,
                                         @Part("record_type") RequestBody record_type,
                                         @Part("record_end_time") RequestBody record_end_time,
                                         @Part("record_start_time") RequestBody record_start_time,
                                         @Part("caller_phone_no") RequestBody caller_phone_no,
                                         @Part("receiver_phone_no") RequestBody receiver_phone_no,
                                         @Part MultipartBody.Part file);

}
