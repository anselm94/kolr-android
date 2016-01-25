package com.blogspot.merbinjanselm.kolr;

import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;


public class SettingsDialog extends Dialog implements android.view.View.OnClickListener {

    static int KOLR_COLOR_RED       = 71;
    static int KOLR_COLOR_GREEN     = 72;
    static int KOLR_COLOR_BLUE      = 73;
    static int KOLR_COLOR_YELLOW    = 74;
    static int KOLR_COLOR_ORANGE    = 75;
    static int KOLR_COLOR_VIOLET    = 76;
    static int KOLR_COLOR_BLACK     = 77;

    Activity mActivity;
    Button btn_red, btn_green,btn_blue,btn_yellow,btn_orange,btn_violet,btn_black;
    Button btn_rate,btn_go_back;
    TextView tv_color;

    SharedPreferences preference;
    SharedPreferences.Editor mPrefsEditor;

    Typeface gameFont_main;

    public SettingsDialog()
    {
        super(null);
    }

    public SettingsDialog(Context mContext) {
        super(mContext);
        this.mActivity = (Activity)mContext;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setBackgroundDrawable(new ColorDrawable(android.R.color.transparent));
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
        setContentView(R.layout.dialog_settings);

        final View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
            }
        });

        gameFont_main = Typeface.createFromAsset(mActivity.getAssets(), "fonts/skranji.ttf");

        preference = mActivity.getSharedPreferences("KolrPref", Context.MODE_PRIVATE);
        mPrefsEditor = preference.edit();

        tv_color = (TextView)findViewById(R.id.tv_color);

        btn_red = (Button) findViewById(R.id.btn_red);
        btn_green = (Button) findViewById(R.id.btn_green);
        btn_blue = (Button) findViewById(R.id.btn_blue);
        btn_yellow = (Button) findViewById(R.id.btn_yellow);
        btn_orange = (Button) findViewById(R.id.btn_orange);
        btn_violet = (Button) findViewById(R.id.btn_violet);
        btn_black = (Button) findViewById(R.id.btn_black);

        btn_rate = (Button)findViewById(R.id.btn_settings_rate);
        btn_go_back = (Button)findViewById(R.id.btn_settings_go_back);

        btn_red.setOnClickListener(this);
        btn_green.setOnClickListener(this);
        btn_blue.setOnClickListener(this);
        btn_yellow.setOnClickListener(this);
        btn_orange.setOnClickListener(this);
        btn_violet.setOnClickListener(this);
        btn_black.setOnClickListener(this);

        btn_rate.setOnClickListener(this);
        btn_go_back.setOnClickListener(this);

        tv_color.setTypeface(gameFont_main);
        btn_rate.setTypeface(gameFont_main);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_red:
                mPrefsEditor.putString("CURRENT_COLOR_MODE", Integer.toString(KOLR_COLOR_RED));
                mPrefsEditor.apply();
                dismiss();
                break;
            case R.id.btn_green:
                mPrefsEditor.putString("CURRENT_COLOR_MODE", Integer.toString(KOLR_COLOR_GREEN));
                mPrefsEditor.apply();
                dismiss();
                break;
            case R.id.btn_blue:
                mPrefsEditor.putString("CURRENT_COLOR_MODE", Integer.toString(KOLR_COLOR_BLUE));
                mPrefsEditor.apply();
                dismiss();
                break;
            case R.id.btn_yellow:
                mPrefsEditor.putString("CURRENT_COLOR_MODE", Integer.toString(KOLR_COLOR_YELLOW));
                mPrefsEditor.apply();
                dismiss();
                break;
            case R.id.btn_orange:
                mPrefsEditor.putString("CURRENT_COLOR_MODE", Integer.toString(KOLR_COLOR_ORANGE));
                mPrefsEditor.apply();
                dismiss();
                break;
            case R.id.btn_violet:
                mPrefsEditor.putString("CURRENT_COLOR_MODE", Integer.toString(KOLR_COLOR_VIOLET));
                mPrefsEditor.apply();
                dismiss();
                break;
            case R.id.btn_black:
                mPrefsEditor.putString("CURRENT_COLOR_MODE", Integer.toString(KOLR_COLOR_BLACK));
                mPrefsEditor.apply();
                dismiss();
                break;
            case R.id.btn_settings_rate:
                launchMarket();
                dismiss();
                break;
            case R.id.btn_settings_go_back:
                dismiss();
                break;
        }
    }

    public void launchMarket()
    {
        try
        {
            Uri uri = Uri.parse("market://details?id=" + mActivity.getPackageName());
            Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
            mActivity.startActivity(myAppLinkToMarket);
        }
        catch (ActivityNotFoundException e)
        {
            Uri uri = Uri.parse("http://details?id=" + mActivity.getPackageName());
            Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
            mActivity.startActivity(myAppLinkToMarket);
        }
    }
}
