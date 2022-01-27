package com.ec.bgm.movil.application.providers;

import com.ec.bgm.movil.application.model.FCMBody;
import com.ec.bgm.movil.application.model.FCMResponse;
import com.ec.bgm.movil.application.retrofit.IFCMApi;
import com.ec.bgm.movil.application.retrofit.RetrofitClient;

import retrofit2.Call;

public class NotificationProvider {

    private String url = "https://fcm.googleapis.com";

    public NotificationProvider() {
    }

    public Call<FCMResponse> sendNotification(FCMBody body) {
        return RetrofitClient.getClient(url).create(IFCMApi.class).send(body);
    }
}
