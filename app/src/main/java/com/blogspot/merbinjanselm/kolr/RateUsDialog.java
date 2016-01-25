package com.blogspot.merbinjanselm.kolr;

import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;


public class RateUsDialog extends Dialog {

    Activity mActivity;
    Button btn_rate,btn_goback;
    TextView tv_do_u_like,tv_help_us;
    Typeface gameFont_main;

    public RateUsDialog()
    {
        super(null);
    }

    public RateUsDialog(Context mContext) {
        super(mContext);
        this.mActivity = (Activity)mContext;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setBackgroundDrawable(new ColorDrawable(android.R.color.transparent));
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
        setContentView(R.layout.dialog_rate_us);


        final View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
            }
        });

        gameFont_main = Typeface.createFromAsset(mActivity.getAssets(), "fonts/skranji.ttf");

        tv_do_u_like = (TextView)findViewById(R.id.tv_do_u_like);
        tv_help_us = (TextView)findViewById(R.id.tv_help_us);

        btn_rate = (Button)findViewById(R.id.btn_rateus_rate);
        btn_goback = (Button)findViewById(R.id.btn_rateus_go_back);

        btn_rate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchMarket();
            }
        });

        btn_goback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });


        tv_do_u_like.setTypeface(gameFont_main);
        tv_help_us.setTypeface(gameFont_main);
        btn_rate.setTypeface(gameFont_main);
    }

    public void launchMarket() {
        try {
            Uri uri = Uri.parse("market://details?id=" + mActivity.getPackageName());
            Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
            mActivity.startActivity(myAppLinkToMarket);
        } catch (ActivityNotFoundException e) {
            Uri uri = Uri.parse("http://details?id=" + mActivity.getPackageName());
            Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
            mActivity.startActivity(myAppLinkToMarket);
        }
    }
}
