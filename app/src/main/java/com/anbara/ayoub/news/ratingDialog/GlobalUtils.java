package com.anbara.ayoub.news.ratingDialog;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import com.anbara.ayoub.news.MainActivity;
import com.anbara.ayoub.news.R;


public class GlobalUtils {

    public static final String NAME_PREFERENCE_DIALOG_RATING = "DialogRating";
    public static final String KEY_IS_NEVER = "isNever";


    public static void showDialog(final Context context) {
// create the dialog

        final SharedPreferences preferences = context.getSharedPreferences(NAME_PREFERENCE_DIALOG_RATING, 0);
        final CustomDialog dialog = new CustomDialog(context, R.style.CustomDialogTheme);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        if (inflater == null) return;
        View view = inflater.inflate(R.layout.layout_dialog, null);
        dialog.setContentView(view);

        Button btn_later = dialog.findViewById(R.id.btn_later);
        Button btn_done = dialog.findViewById(R.id.btn_done);
        Button btn_never = dialog.findViewById(R.id.btn_never);
        final RatingBar ratingBar = dialog.findViewById(R.id.ratingBar);
        final TextView txt_numberStarsActive = dialog.findViewById(R.id.txt_numberStars);
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                int i = (int) rating;
                txt_numberStarsActive.setText(i + "/5");

            }
        });
        btn_later.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MainActivity.getInstance().finish();
                // dialog.dismiss();

            }
        });
        btn_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean(KEY_IS_NEVER, true);// action to not show another fois
                editor.apply();
                float startRating = ratingBar.getRating();
                if (3 <= startRating) {
                    final String appPackageName = context.getPackageName();
                    try {
                        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                    } catch (android.content.ActivityNotFoundException e) {
                        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName)));
                    }


                    //   dialog.dismiss();
                }
                MainActivity.getInstance().finish();


            }
        });
        btn_never.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean(KEY_IS_NEVER, true);
                editor.apply();
                MainActivity.getInstance().finish();

//                dialog.dismiss();
            }
        });
        dialog.show();
    }


}
