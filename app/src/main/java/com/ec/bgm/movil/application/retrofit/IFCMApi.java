package com.ec.bgm.movil.application.retrofit;

import com.ec.bgm.movil.application.model.FCMBody;
import com.ec.bgm.movil.application.model.FCMResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface IFCMApi {

    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAAuYjND6k:APA91bEFXCkYsor3s8OuRktFJxdrbWbC9grVXZ1ccZ1JJ0FYJ2HD-CVorqA2ZyFlpQ1xk0mbuQEL_8yOFhudjCd3WpkQ8hBmfQcgwznyz9X_bPXeZ6X6qA3bddOJpLHf4GrliKr7Efgi"
    })

    @POST("fcm/send")
    Call<FCMResponse> send(@Body FCMBody body);
}
