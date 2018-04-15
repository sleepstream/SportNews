package com.example.george.sportnews;

import com.google.gson.Gson;
import okhttp3.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ServerLoader {

    OkHttpClient client;// = new OkHttpClient().retryOnConnectionFailure();
    private String urlGet;
    private Map<String, String> headerGet;
    public String body;
    public RequestBody bodyRec;

    public ServerLoader() {
        this.client = new OkHttpClient.Builder().retryOnConnectionFailure(false).build();
    }

    public void setUrlGet( String url) {
        this.urlGet = url;
    }

    public Response runGet() throws IOException {
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(urlGet)
                .build();
        return client.newCall(request).execute();
    }


}
