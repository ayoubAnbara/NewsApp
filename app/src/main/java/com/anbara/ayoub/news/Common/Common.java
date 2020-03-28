package com.anbara.ayoub.news.Common;

import com.anbara.ayoub.news.Interface.IconsBetterIdeaService;
import com.anbara.ayoub.news.Interface.NewsService;
import com.anbara.ayoub.news.Remote.IconBetterIdeaClient;
import com.anbara.ayoub.news.Remote.RetrofitClient;

public class Common {


    private static final String BASE_URL = "https://newsapi.org/";
   // public static final String API_KEY = "4de29a40c25c43deb21c80f26a5c65af";//anbara
    public static final String API_KEY = "78ed00dcde294dbc8af81abb90e6663d";

    public static NewsService getNewsService() {
        return RetrofitClient.getClient(BASE_URL)
                .create(NewsService.class);
    }

    public static IconsBetterIdeaService getIconService() {
        return IconBetterIdeaClient.getClient()
                .create(IconsBetterIdeaService.class);
    }

    //https://newsapi.org/v2/top-headlines?sources=techcrunch&apiKey=4de29a40c25c43deb21c80f26a5c65af
    public static String getAPIUrl(String source, String sortBy, String apiKEY) {
        StringBuilder apiUrl = new StringBuilder("https://newsapi.org/v2/top-headlines?sources=");
        return apiUrl.append(source)
                .append("&apiKey=")
                .append(apiKEY)
                .toString();

    }

}
