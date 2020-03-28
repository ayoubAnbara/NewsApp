package com.anbara.ayoub.news;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.ListAdapter;

import com.anbara.ayoub.news.Adapter.ListSourceAdapter;
import com.anbara.ayoub.news.Common.Common;
import com.anbara.ayoub.news.Interface.NewsService;
import com.anbara.ayoub.news.Model.WebSite;

import com.anbara.ayoub.news.ratingDialog.GlobalUtils;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;

import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.gson.Gson;

import dmax.dialog.SpotsDialog;
import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    RecyclerView listWebSite;
    RecyclerView.LayoutManager layoutManager;
    NewsService mService;
    ListSourceAdapter adapter;
    android.app.AlertDialog dialog;
    SwipeRefreshLayout swipeLayout;
    SharedPreferences preferences;
    public String language;
    public String keyLanguage = "lang";
    private String keyIndexFlag = "index";
    private String keyIsChecked = "isChecked";
    private Integer tabIndexFlag[];
    private String items[];
    private SwitchCompat drawerSwitch;
    private InterstitialAd mInterstitialAd;
    MenuItem menuItem;
    static MainActivity mainActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        preferences = getPreferences(0);
        mainActivity=this;
        MobileAds.initialize(this, getString(R.string.ADMOB_APP_ID));
//        AdView mAdView = findViewById(R.id.adView);
//        AdRequest adRequest = new AdRequest.Builder().build();
//        mAdView.loadAd(adRequest);
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.intertial_id));
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                // Load the next interstitial.
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }

        });
        items = new String[]{"en", "es", "fr", "zh", "de", "ru", "it", "nl"
                , "no", "se", "pt", "ar", "ud", "he"};
        tabIndexFlag = new Integer[]{R.drawable.ic_usa, R.drawable.ic_sp,
                R.drawable.ic_france, R.drawable.ic_china, R.drawable.ic_germany,
                R.drawable.ic_russia, R.drawable.ic_italy, R.drawable.ic_netherlands,
                R.drawable.ic_norway, R.drawable.ic_sweden,
                R.drawable.ic_brazil, R.drawable.ic_saudi_arabia,
                R.drawable.ic_pakistan, R.drawable.ic_israel};

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.app_name, R.string.app_name);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        //init cache
        Paper.init(this);
        //init service
        mService = Common.getNewsService();
        swipeLayout = findViewById(R.id.swipeRefresh);
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Toast.makeText(MainActivity.this, "onRefresh()", Toast.LENGTH_SHORT).show();
                loadWebSiteSource(true);
            }
        });
        //int view
        listWebSite = findViewById(R.id.list_source);
        listWebSite.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        listWebSite.setLayoutManager(layoutManager);
        dialog = new SpotsDialog(this);
        loadWebSiteSource(false);
        navigationView.setItemIconTintList(null);
        drawerSwitch = (SwitchCompat) navigationView.getMenu().findItem(R.id.switch_theme).getActionView();
        drawerSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);

                } else {
                    getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }
                final SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean(keyIsChecked, isChecked);
                editor.apply();

            }
        });
        if (preferences.getBoolean(keyIsChecked, false)) {
            drawerSwitch.setChecked(true);
            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        } else {
            drawerSwitch.setChecked(false);

            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        }

        if (isFirstTime()) {
            ListAdapter adapter = new ArrayAdapterWithIcon(this, items, tabIndexFlag);

            new AlertDialog.Builder(this).setTitle("Select Language")
//                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.dismiss();
//                        }
//                    })
                    .setCancelable(false)
                    .setAdapter(adapter, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int i) {
                            //Toast.makeText(MainActivity.this, "Item Selected: " + item, Toast.LENGTH_SHORT).show();

                            changeLaunguage(items[i], menuItem);
                        }
                    }).show();
        }
    }
    public static MainActivity getInstance() {
        return mainActivity;
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
           // super.onBackPressed();
            SharedPreferences preferences = getSharedPreferences(GlobalUtils.NAME_PREFERENCE_DIALOG_RATING, 0);
            if ((!preferences.getBoolean(GlobalUtils.KEY_IS_NEVER, false)) && isConnected(this))
                GlobalUtils.showDialog(this);
            else {
                super.onBackPressed();
                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                }
            }
        }
    }
    public boolean isConnected(Context context) {
// exist deprecation method
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netinfo = null;
        if (cm != null) {
            netinfo = cm.getActiveNetworkInfo();
        }

        if (netinfo != null && netinfo.isConnectedOrConnecting()) {
            android.net.NetworkInfo wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            android.net.NetworkInfo mobile = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            if ((mobile != null && mobile.isConnectedOrConnecting()) || (wifi != null &&
                    wifi.isConnectedOrConnecting())) return true;
            else return false;
        } else
            return false;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull final MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        final String appPackageName = getPackageName();
        if (id == R.id.nav_share) {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
            String txt = getString(R.string.great_application) + " " + "https://play.google.com/store/apps/details?id=" + appPackageName;
            shareIntent.putExtra(Intent.EXTRA_TEXT, txt);
            startActivity(Intent.createChooser(shareIntent, getString(R.string.share_app)));

        } else if (id == R.id.news_app2) {

            Intent intent = getPackageManager().getLaunchIntentForPackage("com.top.headlines");
            if (intent == null) {
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.top.headlines"));

            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

        } else if (id == R.id.nav_doMarket) {

            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
            } catch (android.content.ActivityNotFoundException e) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName)));
            }

        } else if (id == R.id.nav_privaty_policy) {
            Intent browserIntent = new Intent(this, DetailArticle.class);
            browserIntent.putExtra("webURL", getString(R.string.url_pravicy));
            startActivity(browserIntent);

        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void loadWebSiteSource(boolean isRefreshed) {
        if (!isRefreshed) {
            String cache = Paper.book().read("cache");
            if (cache != null && !cache.isEmpty() && !cache.equals("null")) {
                WebSite webSite = new Gson()
                        .fromJson(cache, WebSite.class);
                adapter = new ListSourceAdapter(MainActivity.this, webSite);
                adapter.notifyDataSetChanged();
                listWebSite.setAdapter(adapter);

            } else {
                dialog.show();
                mService.getSources("v2/sources?language=" + language + "&apiKey=" + Common.API_KEY).enqueue(new Callback<WebSite>() {
                    @Override
                    public void onResponse(@NonNull Call<WebSite> call, @NonNull Response<WebSite> response) {
                        adapter = new ListSourceAdapter(getBaseContext(), response.body());
                        adapter.notifyDataSetChanged();
                        listWebSite.setAdapter(adapter);
                        //save to cache
                        Paper.book().write("cache", new Gson().toJson(response.body()));
                        dialog.dismiss();
                    }

                    @Override
                    public void onFailure(@NonNull Call<WebSite> call, @NonNull Throwable t) {

                    }
                });
            }
        } else {
            swipeLayout.setRefreshing(true);

            mService.getSources("v2/sources?language=" + language + "&apiKey=" + Common.API_KEY).enqueue(new Callback<WebSite>() {
                @Override
                public void onResponse(@NonNull Call<WebSite> call, @NonNull Response<WebSite> response) {
                    adapter = new ListSourceAdapter(getBaseContext(), response.body());
                    adapter.notifyDataSetChanged();
                    listWebSite.setAdapter(adapter);
                    //save to cache
                    Paper.book().write("cache", new Gson().toJson(response.body()));

                    //dismiss refresh progressing
                    swipeLayout.setRefreshing(false);

                }

                @Override
                public void onFailure(@NonNull Call<WebSite> call, @NonNull Throwable t) {

                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        language = preferences.getString(keyLanguage, "en");
    }


    public void changeLaunguage(String lang, MenuItem item) {
        if (item == null) return;
        final SharedPreferences.Editor editor = preferences.edit();
        editor.putString(keyLanguage, lang);
        language = lang;
        int index = 0;
        for (int i = 0; i < items.length; i++) {
            if (items[i].equals(lang)) {
                index = i;
                break;
            }
        }
        item.setIcon(tabIndexFlag[index]);
        editor.putInt(keyIndexFlag, index);
        editor.apply();
        //Paper.clear(MainActivity.this);
        Paper.book().destroy();
        loadWebSiteSource(false);
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu, menu);
        int i = preferences.getInt(keyIndexFlag, 0);
        MenuItem settingsItem = menu.findItem(R.id.pays_appBar);
        this.menuItem = settingsItem;
        settingsItem.setIcon(ContextCompat.getDrawable(this, tabIndexFlag[i]));


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.pays_appBar) {

            ListAdapter adapter = new ArrayAdapterWithIcon(this, items, tabIndexFlag);

            new AlertDialog.Builder(this).setTitle("Select Language")
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setAdapter(adapter, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int i) {
                            //Toast.makeText(MainActivity.this, "Item Selected: " + item, Toast.LENGTH_SHORT).show();

                            changeLaunguage(items[i], item);
                        }
                    }).show();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean isFirstTime() {
        //SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        boolean ranBefore = preferences.getBoolean("RanBefore", false);
        if (!ranBefore) {
            // first time
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("RanBefore", true);
            editor.apply();
        }
        return !ranBefore;
    }

}






