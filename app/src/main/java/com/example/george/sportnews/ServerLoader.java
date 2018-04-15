package com.example.george.sportnews;

import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;
import java.util.Map;

public class ServerLoader {

    private OkHttpClient client;// = new OkHttpClient().retryOnConnectionFailure();
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
