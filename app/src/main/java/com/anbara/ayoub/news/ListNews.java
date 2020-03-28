package com.anbara.ayoub.news;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.anbara.ayoub.news.Adapter.ListNewsAdapter;
import com.anbara.ayoub.news.Common.Common;
import com.anbara.ayoub.news.Interface.NewsService;
import com.anbara.ayoub.news.Model.Article;
import com.anbara.ayoub.news.Model.News;
import com.flaviofaria.kenburnsview.KenBurnsView;
import com.github.florent37.diagonallayout.DiagonalLayout;
import com.squareup.picasso.Picasso;

import java.util.List;

import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListNews extends AppCompatActivity {

    KenBurnsView kbv;
    DiagonalLayout diagonalLayout;
    AlertDialog dialog;
    NewsService mService;
    TextView top_author, top_title;
    SwipeRefreshLayout swipeRefreshLayout;
    String source = "", sortBy = "", webHotUrl = "";
    ListNewsAdapter adapter;
    RecyclerView lstNews;
    RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.layout_list_news);
//        AdView mAdView = findViewById(R.id.adViewList);
//        AdRequest adRequest = new AdRequest.Builder().build();
//        mAdView.loadAd(adRequest);
        //service
        mService = Common.getNewsService();
        dialog = new SpotsDialog(this);
        //View
        swipeRefreshLayout = findViewById(R.id.swipeRefresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadNews(source, true);
            }
        });
        diagonalLayout = findViewById(R.id.diagonalLayout);
        diagonalLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //click to hot / latest news to read
                Intent detail = new Intent(getBaseContext(), DetailArticle.class);
                detail.putExtra("webURL", webHotUrl);
                startActivity(detail);

            }
        });
        kbv = findViewById(R.id.top_image);
        top_author = findViewById(R.id.top_author);
        top_title = findViewById(R.id.top_title);
        lstNews = findViewById(R.id.lstNews);
        lstNews.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        lstNews.setLayoutManager(layoutManager);

        if (getIntent() != null) {
            source = getIntent().getStringExtra("source");
            if (!source.isEmpty()) {
                loadNews(source, false);
            }
        }

    }

    private void loadNews(String source, boolean isRefreshed) {
        if (!isRefreshed) {
            dialog.show();
            mService.getNewestarticles(Common.getAPIUrl(source, sortBy, Common.API_KEY))
                    .enqueue(new Callback<News>() {
                        @Override
                        public void onResponse(@NonNull Call<News> call, @NonNull Response<News> response) {
                            dialog.dismiss();
                            //get first article
                            if (response.body()==null)return;
                            Picasso.get() //with(getBaseContext())
                                    .load(response.body().getArticles().get(0).getUrltoImage())

                                    .into(kbv);
                            top_title.setText(response.body().getArticles().get(0).getTitle());
                            top_author.setText(response.body().getArticles().get(0).getAuthor());
                            webHotUrl = response.body().getArticles().get(0).getUrl();
//load remain article
                            List<Article> removeFirstItem = response.body().getArticles();
                            //because we already load first article to show on diagonal layout
                            //so we need remove it
                            removeFirstItem.remove(0);
                            adapter = new ListNewsAdapter(removeFirstItem, ListNews.this);
                            adapter.notifyDataSetChanged();
                            lstNews.setAdapter(adapter);


                        }

                        @Override
                        public void onFailure(@NonNull Call<News> call, @NonNull Throwable t) {

                        }
                    });

        } else {
            dialog.show();
            mService.getNewestarticles(Common.getAPIUrl(source, sortBy, Common.API_KEY))
                    .enqueue(new Callback<News>() {
                        @Override
                        public void onResponse(@NonNull Call<News> call, @NonNull Response<News> response) {
                            dialog.dismiss();
                            //get first article
                            if (response.body()==null)return;

                            Picasso.get() //with(getBaseContext())

                                    .load(response.body().getArticles().get(0).getUrltoImage())


                                    .into(kbv);
                            top_title.setText(response.body().getArticles().get(0).getTitle());
                            top_author.setText(response.body().getArticles().get(0).getAuthor());
                            webHotUrl = response.body().getArticles().get(0).getUrl();
//load remain article
                            List<Article> removeFirstItem = response.body().getArticles();
                            //because we already load first article to show on diagonal layout
                            //so we need remove it
                            removeFirstItem.remove(0);
                            adapter = new ListNewsAdapter(removeFirstItem, ListNews.this);
                            adapter.notifyDataSetChanged();
                            lstNews.setAdapter(adapter);


                        }

                        @Override
                        public void onFailure(@NonNull Call<News> call, @NonNull Throwable t) {

                        }
                    });
            swipeRefreshLayout.setRefreshing(false);


        }
    }
}
