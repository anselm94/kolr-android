package com.blogspot.merbinjanselm.kolr;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.blogspot.merbinjanselm.kolr.kolrClasses.UpdateInfo;

/**
 * Created by anselm94 on 15/7/15.
 */
public class UpdateDialog extends Dialog {

    Context mActivity;
    Typeface gameFont_main;
    Button btn_go_back;
    Button btn_action;

    TextView tv_header;
    TextView tv_detail;

    UpdateInfo updateInfo;

    public UpdateDialog()
    {
        super(null);
    }

    public UpdateDialog(Context mContext,UpdateInfo mUpdateInfo) {
        super(mContext);
        this.mActivity = mContext;
        this.updateInfo = mUpdateInfo;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setBackgroundDrawable(new ColorDrawable(mActivity.getResources().getColor(android.R.color.transparent)));
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
        setContentView(R.layout.dialog_update);

        final View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
            }
        });

        btn_go_back = (Button)findViewById(R.id.btn_update_go_back);
        btn_action = (Button)findViewById(R.id.btn_update_action);
        tv_header = (TextView)findViewById(R.id.tv_update_header);
        tv_detail = (TextView)findViewById(R.id.tv_update_detail);

        gameFont_main = Typeface.createFromAsset(mActivity.getAssets(), "fonts/skranji.ttf");

        tv_detail.setTypeface(gameFont_main);
        tv_header.setTypeface(gameFont_main);
        btn_action.setTypeface(gameFont_main);

        tv_detail.setText(updateInfo.getInfoDetail());
        tv_header.setText(updateInfo.getInfoHeader());
        btn_action.setText(updateInfo.getBtnText());
        btn_action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(updateInfo.getBtnActionURL()));
                mActivity.startActivity(intent);
            }
        });
        btn_go_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

}
