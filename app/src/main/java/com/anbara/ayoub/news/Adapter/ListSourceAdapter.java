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

import com.anbara.ayoub.news.Common.Common;
import com.anbara.ayoub.news.Interface.IconsBetterIdeaService;
import com.anbara.ayoub.news.Interface.ItemClickListener;
import com.anbara.ayoub.news.ListNews;
import com.anbara.ayoub.news.Model.IconBetterIdea;
import com.anbara.ayoub.news.Model.WebSite;
import com.anbara.ayoub.news.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

class ListSourceViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    ItemClickListener itemClickListener;
    TextView source_title;
    CircleImageView source_image;


    public ListSourceViewHolder(@NonNull View itemView) {
        super(itemView);
        source_image = itemView.findViewById(R.id.source_image);
        source_title = itemView.findViewById(R.id.source_name);
        itemView.setOnClickListener(this);

    }

    public ListSourceViewHolder(@NonNull View itemView, int i) {
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

class AdViewHolder extends ListSourceViewHolder {

    AdViewHolder(View view) {
        super(view, 5);
    }
}


public class ListSourceAdapter extends RecyclerView.Adapter<ListSourceViewHolder> {
    private Context context;
    private WebSite webSite;
    private IconsBetterIdeaService mService;


    public ListSourceAdapter(Context context, WebSite webSite) {
        this.context = context;
        this.webSite = webSite;
        mService = Common.getIconService();
    }

    @NonNull
    @Override
    public ListSourceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
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
            return new AdViewHolder(v3);
        } else {
            View itemView = inflater.inflate(R.layout.source_layout, parent, false);
            return new ListSourceViewHolder(itemView);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position % 5 == 0)
            return 1;  // to show banner
        return 0;      // to show normal item
    }

    @Override
    public void onBindViewHolder(@NonNull final ListSourceViewHolder holder, int position) {
        if (getItemViewType(position) == 1) return;
        StringBuilder iconBetterAPI = new StringBuilder("https://besticon-demo.herokuapp.com/allicons.json?url=");
        iconBetterAPI.append(webSite.getSources().get(position).getUrl());
        mService.getIconUrl(iconBetterAPI.toString())
                .enqueue(new Callback<IconBetterIdea>() {
                    @Override
                    public void onResponse(@NonNull Call<IconBetterIdea> call, @NonNull Response<IconBetterIdea> response) {
                        if (response.body() == null) return;
                        if (response.body().getIcons().size() > 0) {
                            Picasso.get()
                                    .load(response.body().getIcons().get(0).getUrl())
                                    .into(holder.source_image);
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<IconBetterIdea> call, @NonNull Throwable t) {

                    }
                });

        holder.source_title.setText(webSite.getSources().get(position).getName());
        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                Intent intent = new Intent(context, ListNews.class);
                intent.putExtra("source", webSite.getSources().get(position).getId());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return webSite.getSources().size();
    }
}
