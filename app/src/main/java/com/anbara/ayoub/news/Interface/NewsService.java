package com.anbara.ayoub.news.Interface;

import com.anbara.ayoub.news.Model.News;
import com.anbara.ayoub.news.Model.WebSite;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface NewsService {
   // @GET("v2/sources?language=en&apiKey="+ Common.API_KEY)
    @GET
    Call<WebSite> getSources(@Url String url);
    @GET
    Call<News> getNewestarticles(@Url String url);
}
