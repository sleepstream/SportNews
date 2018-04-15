package com.example.george.sportnews;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import okhttp3.Response;

import java.io.IOException;

public class DisplayNews extends AppCompatActivity{

    private String article;
    private String urlNewsArticle="http://mikonatoruri.win/post.php?article=";
    private Obj dataFromServer;
    private TextView team1;
    private TextView team2;
    private TextView place;
    private TextView time;
    private TextView tournament;
    private TextView prediction;
    private Context context;
    private LoadNews loadNews;

    private RelativeLayout loadingPanel;
    private RelativeLayout _404;
private LinearLayout articleNews;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_displaynews);
        Intent intent = getIntent();
        context = this.getApplicationContext();

        articleNews = findViewById(R.id.article);

        _404= findViewById(R.id._404);

        loadingPanel = findViewById(R.id.loadingPanel);
        article = intent.getStringExtra("url");
        team1 = findViewById(R.id.team1);
        team2 = findViewById(R.id.team2);
        time = findViewById(R.id.time);
        place = findViewById(R.id.place);
        prediction = findViewById(R.id.prediction);
        tournament = findViewById(R.id.tournament);

        if(loadNews != null && loadNews.getStatus() == AsyncTask.Status.RUNNING)
        {
            loadNews.cancel(true);
        }
        loadNews = new LoadNews();
        loadNews.execute();
    }
    private void bodyJsonParse(String body)
    {
        try {
            Gson gSon = new Gson();
            dataFromServer = gSon.fromJson(body, Obj.class);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            dataFromServer = null;
        }

    }

    public class LoadNews extends AsyncTask<String, String, Void>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadingPanel.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            if(values[0]!= null)
            {
                Toast.makeText(context, values[0],Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            loadingPanel.setVisibility(View.GONE);
            if(dataFromServer != null) {
                team1.setText(dataFromServer.team1);
                team2.setText(dataFromServer.team2);
                time.setText(dataFromServer.time);
                place.setText(dataFromServer.place);
                tournament.setText(dataFromServer.tournament);
                prediction.setText(dataFromServer.prediction);

                LayoutInflater inflater = LayoutInflater.from(context);
                for (Article item : dataFromServer.article) {
                    View view = inflater.inflate(R.layout.row_article, articleNews, false);
                    TextView header = view.findViewById(R.id.header);
                    TextView text = view.findViewById(R.id.text);

                    header.setText(item.header);
                    text.setText(item.text);
                    articleNews.addView(view);
                }
            }
            else
            {
                _404.setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected Void doInBackground(String... strings) {
            ServerLoader serverLoader = new ServerLoader();
            String currentUrl = urlNewsArticle + article;
            serverLoader.setUrlGet(currentUrl);
            try {
                Response response = serverLoader.runGet();
                if (response.code() == 200) {
                    bodyJsonParse(response.body().string());

                }
                else
                {
                    publishProgress(context.getString(R.string.error_server)+" "+ response.code());
                    return null;
                }
            } catch (IOException e) {
                e.printStackTrace();
                publishProgress(context.getString(R.string.connectionError));
                return null;
            }
            return null;
        }
    }

    class Obj
    {

        public String team1;
        public String team2;
        public String time;
        public String tournament;
        public String place;
        public String prediction;
        public Article[] article;
    }
    class  Article
    {
        public String header;
        public String text;
    }
}
