package com.example.george.sportnews;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.google.gson.Gson;
import okhttp3.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements  SwipeRefreshLayout.OnRefreshListener{

    public static String selectedCategory = "";
    private String[] categories=new String[]{ "football", "hockey", "tennis", "basketball", "volleyball", "cybersport"};
    private String urlNewsList = "http://mikonatoruri.win/list.php?category=";

    public static Context context;

    private LoadNews loadNews;
    private DrawerLayout mDrawerLayout;
    private RelativeLayout loadingPanel;
    private NewsListAdapter newsListAdapter;
    private LinearLayoutManager llm;
    public RecyclerView recyclerViewNewsList;
    private SwipeRefreshLayout swipe_container;

    private List<Events> listNews = new ArrayList<Events>();

    public Obj dataFromServer;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this.getApplicationContext();

        swipe_container = findViewById(R.id.swipe_container);
        swipe_container.setOnRefreshListener(this);

        mDrawerLayout = findViewById(R.id.drawer_layout);
        recyclerViewNewsList = findViewById(R.id.newsList);
        loadingPanel = findViewById(R.id.loadingPanel);
        llm = new LinearLayoutManager(context);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerViewNewsList.setLayoutManager(llm);

        newsListAdapter = new NewsListAdapter(context, this, listNews);
        recyclerViewNewsList.setAdapter(newsListAdapter);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);

        NavigationView navigationView = findViewById(R.id.slidPanel);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        menuItem.setChecked(true);
                        mDrawerLayout.closeDrawers();
                        switch(menuItem.getItemId())
                        {
                            case R.id.football:
                                selectedCategory = "football";
                                break;
                            case R.id.basketball:
                                selectedCategory = "basketball";
                                break;
                            case R.id.cybersport:
                                selectedCategory = "cybersport";
                                break;
                            case R.id.tennis:
                                selectedCategory = "tennis";
                                break;
                            case R.id.hockey:
                                selectedCategory = "hockey";
                                break;
                            case R.id.volleyball:
                                selectedCategory = "volleyball";
                                break;
                            case R.id.allNews:
                                selectedCategory = "";
                                break;
                        }
                        if(loadNews != null && loadNews.getStatus() == AsyncTask.Status.RUNNING)
                        {
                            loadNews.cancel(true);
                        }
                        loadNews = new LoadNews();
                        loadNews.execute();

                        return true;
                    }
                });

        if(loadNews != null && loadNews.getStatus() == AsyncTask.Status.RUNNING)
        {
            loadNews.cancel(true);
        }
        loadNews = new LoadNews();
        loadNews.execute();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if(newsListAdapter!= null)
            newsListAdapter.notifyDataSetChanged();
    }





    public void bodyJsonParse(String body)
    {
        Gson gSon = new Gson();
        dataFromServer  = gSon.fromJson(body, Obj.class);

    }

    @Override
    public void onRefresh() {
        LoadNews loadNews = new LoadNews();
        loadNews.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        swipe_container.setRefreshing(false);
    }

    class Obj
    {

        public Events[] events;
        public String category;
    }
    class Events
    {

        public String title;
        public String coefficient;
        public String time;
        public String place;
        public String preview;
        public String article;
        public String category;
        public boolean descritionIsOn;
    }
    public class LoadNews extends AsyncTask<String, String, Void>
    {
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(listNews.size()>0)
                newsListAdapter.notifyDataSetChanged();
        }

        @Override
        protected void onPreExecute() {
            loadingPanel.setVisibility(View.VISIBLE);
            listNews.clear();
            newsListAdapter.selected_id=-1;
            newsListAdapter.notifyDataSetChanged();
        }

        @Override
        protected void onProgressUpdate(String... values) {
            if(values!= null && values.length>0)
            {

                Toast.makeText(context, values[0], Toast.LENGTH_LONG).show();
            }
            loadingPanel.setVisibility(View.GONE);
            if(listNews.size()>0)
            {
                newsListAdapter.notifyItemInserted(listNews.size()-1);
            }
        }

        @Override
        protected Void doInBackground(String... strings) {
            ServerLoader serverLoader = new ServerLoader();
            for(String category : categories) {
                if(isCancelled())
                    return null;
                if (selectedCategory.equalsIgnoreCase("") || selectedCategory.equalsIgnoreCase(category)) {
                    String currentUrl = urlNewsList + category;
                    serverLoader.setUrlGet(currentUrl);
                    try {
                        Response response = serverLoader.runGet();
                        if(isCancelled())
                            return null;
                        if (response.code() == 200) {
                            bodyJsonParse(response.body().string());
                            dataFromServer.category = category;
                            for (Events event : dataFromServer.events) {
                                event.category = category;
                                if(isCancelled())
                                    return null;
                                listNews.add(event);
                                publishProgress();
                            }
                        }
                        else
                        {
                            publishProgress(context.getString(R.string.error_server)+" "+response.code());
                            return null;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        publishProgress(context.getString(R.string.connectionError));
                        //Toast.makeText(context, context.getString(R.string.connectionError), Toast.LENGTH_LONG).show();
                        return null;
                    }
                }
            }
            return null;
        }
    }

}
