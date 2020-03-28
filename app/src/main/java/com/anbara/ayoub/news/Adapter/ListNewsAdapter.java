package com.anbara.ayoub.news.Adapter;


import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.anbara.ayoub.news.Common.ISO8601Parse;
import com.anbara.ayoub.news.DetailArticle;
import com.anbara.ayoub.news.Interface.ItemClickListener;
import com.anbara.ayoub.news.Model.Article;
import com.anbara.ayoub.news.R;
import com.github.curioustechizen.ago.RelativeTimeTextView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

class ListNewsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    ItemClickListener itemClickListener;
    TextView article_title;
    RelativeTimeTextView article_time;
    CircleImageView article_image;

    public ListNewsViewHolder(@NonNull View itemView) {
        super(itemView);
        article_image = itemView.findViewById(R.id.article_image);
        article_time = itemView.findViewById(R.id.article_time);
        article_title = itemView.findViewById(R.id.article_title);
        itemView.setOnClickListener(this);
    }

    public ListNewsViewHolder(@NonNull View itemView, int i) {
        super(itemView);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view, getAdapterPosition(), false);
    }

}

class MyAdViewHolder extends ListNewsViewHolder {

    MyAdViewHolder(View view) {
        super(view, 5);
    }
}

public class ListNewsAdapter extends RecyclerView.Adapter<ListNewsViewHolder> {
    private List<Article> articleList;
    private Context context;

    public ListNewsAdapter(List<Article> articleList, Context context) {
        this.articleList = articleList;
        this.context = context;
    }

    @NonNull
    @Override
    public ListNewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == 1) {
            // show banner admob
            View v3 = inflater.inflate(R.layout.item_banner_admob_ads, parent, false);
            AdView adView = new AdView(context); //ads admob
            //adView.setAdSize(AdSize.SMART_BANNER);
            adView.setAdSize(AdSize.LARGE_BANNER);
            adView.setAdUnitId(context.getString(R.string.banner_id));
            CardView cardViewContainer = v3.findViewById(R.id.native_ad_container);
            adView.loadAd(new AdRequest.Builder().build());
            cardViewContainer.addView(adView);
            return new MyAdViewHolder(v3);
        } else {
            View itemView = inflater.inflate(R.layout.news_layout, parent, false);
            return new ListNewsViewHolder(itemView);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position % 5 == 0)
            return 1;  // to show banner
        return 0;      // to show normal item
    }

    @Override
    public void onBindViewHolder(@NonNull ListNewsViewHolder holder, int position) {
        if (getItemViewType(position) == 1) return;
        Picasso.get()
                .load(articleList.get(position).getUrltoImage())
                .into(holder.article_image);
        if (articleList.get(position).getTitle().length() > 65)
            holder.article_title.setText(articleList.get(position).getTitle().substring(0, 65) + "...");
        else holder.article_title.setText(articleList.get(position).getTitle());
        if (articleList.get(position).getPublishedAt() != null) {
            Date date = null;
            try {
                date = ISO8601Parse.parse(articleList.get(position).getPublishedAt());

            } catch (ParseException ex) {
                ex.printStackTrace();
            }
            if (date != null)
                holder.article_time.setReferenceTime(date.getTime());
        }
        //set event click
        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                Intent detail = new Intent(context, DetailArticle.class);
                detail.putExtra("webURL", articleList.get(position).getUrl());
                //detail.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(detail);
            }
        });

    }

    @Override
    public int getItemCount() {
        return articleList.size();
    }
}
