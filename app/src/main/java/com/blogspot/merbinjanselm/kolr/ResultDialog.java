package com.blogspot.merbinjanselm.kolr;

import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
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


public class ResultDialog extends Dialog implements android.view.View.OnClickListener {

    static int GAME_PLAY_COUNT_FOR_RATE = 20;

    int gamePlayCount;
    Activity mActivity;
    Button btn_rate, btn_share_score,btn_go_back;
    TextView tv_score,tv_high_score,tv_lbl_score,tv_lbl_hiscore;

    SharedPreferences preference;
    SharedPreferences.Editor mPrefsEditor;

    Typeface gameFont_main;

    public ResultDialog()
    {
        super(null);
    }

    public ResultDialog(Activity mactivity) {
        super(mactivity);
        this.mActivity = mactivity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setBackgroundDrawable(new ColorDrawable(android.R.color.transparent));
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
        setContentView(R.layout.dialog_result);

        final View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
            }
        });

        gameFont_main = Typeface.createFromAsset(mActivity.getAssets(), "fonts/skranji.ttf");

        preference = getContext().getSharedPreferences("KolrPref", getContext().MODE_PRIVATE);
        mPrefsEditor = preference.edit();

        btn_rate = (Button) findViewById(R.id.btn_result_rate);
        btn_share_score = (Button) findViewById(R.id.btn_result_share_score);
        btn_go_back = (Button)findViewById(R.id.btn_result_go_back);

        tv_score = (TextView)findViewById(R.id.tv_result_currentscore);
        tv_high_score = (TextView)findViewById(R.id.tv_result_highscore);
        tv_lbl_score = (TextView)findViewById(R.id.tv_lbl_result_score);
        tv_lbl_hiscore = (TextView)findViewById(R.id.tv_lbl_result_hiscore);

        btn_rate.setOnClickListener(this);
        btn_share_score.setOnClickListener(this);
        btn_go_back.setOnClickListener(this);

        tv_score.setTypeface(gameFont_main);
        tv_high_score.setTypeface(gameFont_main);
        tv_lbl_score.setTypeface(gameFont_main);
        tv_lbl_hiscore.setTypeface(gameFont_main);
        btn_rate.setTypeface(gameFont_main);
        btn_share_score.setTypeface(gameFont_main);

        tv_score.setText("  " + String.valueOf(preference.getLong("GAME_SCORE", 0)));
        tv_high_score.setText("  " + String.valueOf(preference.getLong("GAME_HIGH_SCORE", 0)));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_result_rate:
                launchMarket();
                break;
            case R.id.btn_result_share_score:
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "I've just scored " + String.valueOf(preference.getLong("GAME_SCORE", 0)) + " in Kolr. I have a high score of " + String.valueOf(preference.getLong("GAME_HIGH_SCORE", 0)) +". Get Kolr on Google Play @ https://play.google.com/store/apps/details?id=com.blogspot.merbinjanselm.kolr");
                sendIntent.setType("text/plain");
                mActivity.startActivity(Intent.createChooser(sendIntent, mActivity.getResources().getText(R.string.txt_share_score_via)));
                break;
            case R.id.btn_result_go_back:
                dismiss();
                gamePlayCount = preference.getInt("GAME_PLAY_COUNT",18);
                mPrefsEditor.putInt("GAME_PLAY_COUNT",(gamePlayCount + 1));
                mPrefsEditor.apply();
                if((gamePlayCount)%GAME_PLAY_COUNT_FOR_RATE == 0)
                {
                    RateUsDialog rateUsDialog = new RateUsDialog(mActivity);
                    rateUsDialog.show();
                }
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
