package com.blogspot.merbinjanselm.kolr;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

/**
 * Created by anselm94 on 26/7/15.
 */
public class AboutDialog extends Dialog{

    Context mActivity;

    TextView tv_help;

    Typeface gameFont_main;

    Button btn_goBack;

    public AboutDialog()
    {
        super(null);
    }

    public AboutDialog(Context mContext) {
        super(mContext);
        this.mActivity = mContext;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setBackgroundDrawable(new ColorDrawable(mActivity.getResources().getColor(android.R.color.transparent)));
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
        setContentView(R.layout.dialog_about);


        final View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
            }
        });

        gameFont_main = Typeface.createFromAsset(mActivity.getAssets(),"fonts/skranji.ttf");

        tv_help = (TextView)findViewById(R.id.tv_about_text);
        btn_goBack = (Button)findViewById(R.id.btn_about_go_back);

        tv_help.setTypeface(gameFont_main);
        btn_goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

}
