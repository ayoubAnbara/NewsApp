package com.anbara.ayoub.news.Interface;

import com.anbara.ayoub.news.Model.IconBetterIdea;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface  IconsBetterIdeaService {
    @GET
    Call<IconBetterIdea> getIconUrl(@Url String  url);
}
