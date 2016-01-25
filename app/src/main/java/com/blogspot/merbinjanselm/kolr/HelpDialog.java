package com.blogspot.merbinjanselm.kolr;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;


public class HelpDialog extends Dialog {

    private static int MAX_HELP_STEPS = 4;

    Context mActivity;
    Button btn_go_back;
    Button btn_prev;
    Button btn_next;

    TextSwitcher tv_help;
    TextView tv_step;

    Typeface gameFont_main;

    int currentStep = 0;
    String helpSteps[] = new String[MAX_HELP_STEPS];
    String helpStepsDetail[] = new String[MAX_HELP_STEPS];

    public HelpDialog()
    {
        super(null);
    }

    public HelpDialog(Context mContext) {
        super(mContext);
        this.mActivity = mContext;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setBackgroundDrawable(new ColorDrawable(mActivity.getResources().getColor(android.R.color.transparent)));
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
        setContentView(R.layout.dialog_help);


        final View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
            }
        });

        gameFont_main = Typeface.createFromAsset(mActivity.getAssets(),"fonts/skranji.ttf");

        tv_help = (TextSwitcher)findViewById(R.id.tv_help);
        tv_step = (TextView)findViewById(R.id.tv_help_step);

        tv_step.setTypeface(gameFont_main);

        tv_help.setFactory(new ViewSwitcher.ViewFactory() {

            public View makeView() {
                // TODO Auto-generated method stub
                // create new textView and set the properties like color, size etc
                TextView myText = new TextView(mActivity,null,R.style.kolr_shadowed_text);
                myText.setGravity(Gravity.CENTER);
                myText.setTextSize(mActivity.getResources().getDimensionPixelSize(R.dimen.text_size_small));
                myText.setTextColor(mActivity.getResources().getColor(R.color.orange_5));
                myText.setShadowLayer(0.01f, 1, 3, mActivity.getResources().getColor(android.R.color.black));
                myText.setPadding(mActivity.getResources().getDimensionPixelSize(R.dimen.padding_8dp),mActivity.getResources().getDimensionPixelSize(R.dimen.padding_8dp),mActivity.getResources().getDimensionPixelSize(R.dimen.padding_8dp),mActivity.getResources().getDimensionPixelSize(R.dimen.padding_8dp));
                myText.setTypeface(gameFont_main);
                return myText;
            }
        });

        Animation in = AnimationUtils.loadAnimation(mActivity,android.R.anim.slide_in_left);
        Animation out = AnimationUtils.loadAnimation(mActivity, android.R.anim.slide_out_right);

        // set the animation type of textSwitcher
        tv_help.setInAnimation(in);
        tv_help.setOutAnimation(out);

        helpSteps[0] = mActivity.getString(R.string.txt_step_1);
        helpSteps[1] = mActivity.getString(R.string.txt_step_2);
        helpSteps[2] = mActivity.getString(R.string.txt_step_3);
        helpSteps[3] = mActivity.getString(R.string.txt_step_4);

        helpStepsDetail[0] = mActivity.getString(R.string.txt_step_detail_1);
        helpStepsDetail[1] = mActivity.getString(R.string.txt_step_detail_2);
        helpStepsDetail[2] = mActivity.getString(R.string.txt_step_detail_3);
        helpStepsDetail[3] = mActivity.getString(R.string.txt_step_detail_4);

        btn_go_back = (Button)findViewById(R.id.btn_help_go_back);
        btn_prev = (Button)findViewById(R.id.btn_help_prev);
        btn_next = (Button)findViewById(R.id.btn_help_next);

        tv_step.setText(helpSteps[0]);
        tv_help.setText(helpStepsDetail[0]);

        btn_go_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        btn_prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prevStep();
            }
        });
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextStep();
            }
        });
    }

    private void nextStep()
    {
        if(currentStep < MAX_HELP_STEPS - 1)
        {
            currentStep++;
            tv_help.setText(helpStepsDetail[currentStep]);
            tv_step.setText(helpSteps[currentStep]);
        }
    }

    private void prevStep()
    {
        if(currentStep > 0)
        {
            currentStep--;
            tv_help.setText(helpStepsDetail[currentStep]);
            tv_step.setText(helpSteps[currentStep]);
        }
    }

}
