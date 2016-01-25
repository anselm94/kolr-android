//                              ||
//                          __________
//                          ----------
//                              ||
//                              ||

package com.blogspot.merbinjanselm.kolr;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.Xml;
import android.view.Surface;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.blogspot.merbinjanselm.kolr.kolrClasses.UpdateInfo;
import com.blogspot.merbinjanselm.kolr.kolrInterfaces.onGameStateChangeListener;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.games.GamesStatusCodes;
import com.google.android.gms.games.leaderboard.LeaderboardVariant;
import com.google.android.gms.games.leaderboard.Leaderboards;
import com.google.example.games.basegameutils.BaseGameUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;


public class KolrActivity extends FragmentActivity implements View.OnClickListener{

    String updateURL = "http://anselm94.5gbfree.com/AppPushNotifications/kolr.xml";

    static final String KEY_VERSION = "version";
    static final String KEY_PUSH_INFO = "pushinfo";
    static final String KEY_HEADER = "header";
    static final String KEY_INFO = "info";
    static final String KEY_BUTTON_TEXT = "buttontext";
    static final String KEY_BUTTON_LINK = "buttonlink";

    static int KOLR_COLOR_RED       = 71;
    static int KOLR_COLOR_GREEN     = 72;
    static int KOLR_COLOR_BLUE      = 73;
    static int KOLR_COLOR_YELLOW    = 74;
    static int KOLR_COLOR_ORANGE    = 75;
    static int KOLR_COLOR_VIOLET    = 76;
    static int KOLR_COLOR_BLACK     = 77;

    static int KOLR_CODE_GAME_END = 549;
    static int KOLR_CODE_GAME_PAUSE = 551;
    static int KOLR_CODE_GAME_PLAY = 552;
    static int KOLR_CODE_GAME_SCORE_CHANGE = 553;
    static int KOLR_CODE_GAME_RESET = 554;
    static int KOLR_CODE_GAME_LEVEL_CHANGE = 555;

    static int KOLR_MODE_EASY = 37;
    static int KOLR_MODE_MEDIUM = 39;
    static int KOLR_MODE_HARD = 41;

    int currentColorMode;
    int gameLevel = 1;
    boolean showAd;
    boolean playGame = false;
    boolean firstTime;

    FrameLayout fragment_container;
    int gridWidth, gridHeight;
    int screenRotation;

    InterstitialAd interstitialAds = null;
    AdRequest.Builder adRequestBuilder;

    CellsFragment games_fragment;
    PauseFragment paused_fragment;

    Button btn_help,btn_info;
    ToggleButton btn_playpause,btn_bgmusictoggle, btn_miscsoundtoggle;

    TextView tv_currentScore, tv_highScore;

    ProgressBar pb_game_level;

    android.support.v4.app.FragmentManager fragmentManager;
    android.support.v4.app.FragmentTransaction fragmentTransaction;

    SharedPreferences preference;
    SharedPreferences.Editor mPrefsEditor;

    Typeface gameFont_main;

    MediaPlayer mMediaPlayer;

    BackgroundSound mBackgroundSound;

    UpdateInfo updateInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kolr);

        //===========================================================================

        fragmentManager = getSupportFragmentManager();

        games_fragment = new CellsFragment();
        paused_fragment = new PauseFragment();

        fragment_container = (FrameLayout) findViewById(R.id.fragment_container);

        gameFont_main = Typeface.createFromAsset(getAssets(),"fonts/skranji.ttf");

        btn_playpause = (ToggleButton)findViewById(R.id.btn_playpause);
        btn_help = (Button)findViewById(R.id.btn_help);
        btn_info = (Button)findViewById(R.id.btn_info);
        btn_bgmusictoggle = (ToggleButton)findViewById(R.id.btn_bgmusictoggle);
        btn_miscsoundtoggle = (ToggleButton)findViewById(R.id.btn_miscsoundtoggle);

        tv_currentScore = (TextView) findViewById(R.id.tv_current_score);
        tv_highScore = (TextView) findViewById(R.id.tv_high_score);

        preference = getSharedPreferences("KolrPref", MODE_PRIVATE);
        mPrefsEditor = preference.edit();

        btn_playpause.setOnClickListener(this);
        btn_help.setOnClickListener(this);
        btn_info.setOnClickListener(this);
        btn_bgmusictoggle.setOnClickListener(this);
        btn_miscsoundtoggle.setOnClickListener(this);

        pb_game_level = (ProgressBar)findViewById(R.id.pb_gameLevel);

        tv_highScore.setTypeface(gameFont_main);
        tv_currentScore.setTypeface(gameFont_main);
        btn_playpause.setTypeface(gameFont_main);

        //=====================================================================

        games_fragment.setOnGameStateChangeListener(new onGameStateChangeListener() {
            @Override
            public void onGameStateChange(final int gameCode) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tv_currentScore.setText(String.valueOf(games_fragment.getScore()));
                        tv_highScore.setText(String.valueOf(games_fragment.getHighScore()));
                        if (gameCode == KOLR_CODE_GAME_END) {
                            setFragments(false);
                            ResultDialog resultDialog = new ResultDialog(KolrActivity.this);
                            resultDialog.show();
                            gameStop();
                        }
                        if (gameCode == KOLR_CODE_GAME_LEVEL_CHANGE) {
                            setLevel(preference.getInt("GAME_LEVEL", 1));
                        }
                    }
                });

            }
        });

        firstTime = preference.getBoolean("FIRST_TIME", true);
        if(firstTime)
        {
            HelpDialog helpDialog = new HelpDialog(this);
            helpDialog.show();
            firstTime =false;
            mPrefsEditor.putBoolean("FIRST_TIME",firstTime);
            mPrefsEditor.apply();
        }
        showAd = false;
        setupAd();

        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        new Thread(new Runnable(){
            InputStream inputStream;
            String  response;
            @Override
            public void run() {
                try {
                    URL url = new URL(updateURL);
                    HttpURLConnection con = (HttpURLConnection) url
                            .openConnection();
                    inputStream = con.getInputStream();

                } catch (Exception e) {
                    e.printStackTrace();
                }
                updateInfo = new UpdateInfo();

                XmlPullParser parser = Xml.newPullParser();
                try {
                    parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                }
                if (inputStream != null) {
                    try {
                        response = convertStreamToString(inputStream);
                        parser.setInput(new StringReader(response));
                    } catch (XmlPullParserException e) {
                    }
                    try {
                        updateInfo = parseXML(parser);
                    } catch (XmlPullParserException e) {
                    } catch (IOException e) {
                    }

                    if (updateInfo != null) {
                        if (updateInfo.getVersionCode() > BuildConfig.VERSION_CODE) {
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "Update Available", Toast.LENGTH_SHORT).show();
                                    UpdateDialog updateDialog = new UpdateDialog(KolrActivity.this, updateInfo);
                                    updateDialog.show();
                                }
                            });
                        } else {
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "No update available!", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                }
            }
        }).start();
    }

    private UpdateInfo parseXML(XmlPullParser parser) throws XmlPullParserException,IOException {
        ArrayList<UpdateInfo> updateInfoList = null;
        int eventType = parser.getEventType();
//        Log.e("TAG INFO",parser.getText());
        UpdateInfo updateInfo = null;

        while (eventType != XmlPullParser.END_DOCUMENT) {
            String name = null;
            switch (eventType) {
                case XmlPullParser.START_DOCUMENT:
                    updateInfoList = new ArrayList<>();
                    break;
                case XmlPullParser.START_TAG:
                    name = parser.getName();
                    if (name.equals("pushinfo")) {
                        updateInfo = new UpdateInfo();
                    } else if (updateInfo != null) {
                        if (name.equals("versioncode")) {
                            updateInfo.setVersionCode(Integer.valueOf(parser.nextText()));
                        } else if (name.equals("header")) {
                            updateInfo.setInfoHeader(parser.nextText());
                        } else if (name.equals("info")) {
                            updateInfo.setInfoDetail(parser.nextText());
                        } else if (name.equals("buttontext")) {
                            updateInfo.setBtnText(parser.nextText());
                        } else if (name.equals("buttonlink")) {
                            updateInfo.setBtnActionURL(parser.nextText());
                        }
                    }
                    break;
                case XmlPullParser.END_TAG:
                    name = parser.getName();
                    if (name.equalsIgnoreCase("pushinfo") && updateInfo != null) {
                        updateInfoList.add(updateInfo);
                    }
            }
            eventType = parser.next();
        }
        return updateInfo;
    }

    private String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onResume() {
        super.onResume();
        ViewTreeObserver observer = fragment_container.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                init();
                fragment_container.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });

        final View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
        restoreEverything();
        setFragments(false);
    }

    @Override
    protected void onStop() {
        super.onStop();
        storeEverything();
        setFragments(false);
        playMusic(false);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        ViewTreeObserver observer = fragment_container.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                init();
                fragment_container.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });
        gameStop();
    }


    protected void init() {
        gridHeight = fragment_container.getHeight();
        gridWidth = fragment_container.getWidth();
        screenRotation = this.getWindow().getWindowManager().getDefaultDisplay().getRotation();
        if (screenRotation == Surface.ROTATION_0 || screenRotation == Surface.ROTATION_180) {
            // portrait mode
            fragment_container.setMinimumHeight(gridWidth);
        } else if (screenRotation == Surface.ROTATION_90 || screenRotation == Surface.ROTATION_270) {
            // landscape
            fragment_container.setMinimumWidth(gridHeight);
        }

    }

    private void restoreEverything() {
        tv_currentScore.setText(String.valueOf(preference.getLong("GAME_SCORE", 0)));
        tv_highScore.setText(String.valueOf(preference.getLong("GAME_HIGH_SCORE", 0)));
        btn_miscsoundtoggle.setChecked(preference.getBoolean("PLAY_MISC_SOUND", true));
        btn_bgmusictoggle.setChecked(preference.getBoolean("PLAY_BACKGROUND_SOUND", true));
        playMusic(preference.getBoolean("PLAY_BACKGROUND_SOUND", true));
        setLevel(preference.getInt("GAME_LEVEL", 1));
    }

    public void storeEverything() {

    }

    public void gameRun() {
        showAd = false;
        loadAd();
    }

    public void gameStop() {
        //Show > (play) button
        showAd = true;
        showAd();
    }

    public void gameReset() {
        //Show Ads
        //TODO Show Ad
        showAd();
    }

    public void setLevel(int level)
    {

        this.gameLevel = level;
        pb_game_level.setProgress(level*100);

//        runOnUiThread(new Runnable() {
//            public void run() {
//                if(pb_game_level.getProgress() < (gameLevel*100))
//                {
//                    while(pb_game_level.getProgress() != (gameLevel*100))
//                    {
//                        pb_game_level.incrementProgressBy(1);
//                        try {
//                            Thread.sleep(10);
//                        } catch (InterruptedException e) {
//                            // TODO Auto-generated catch block
//                            e.printStackTrace();
//                        }
//                    }
//                }else if(pb_game_level.getProgress() > (gameLevel*100))
//                {
//                    while(pb_game_level.getProgress() != (gameLevel*100))
//                    {
//                        pb_game_level.incrementProgressBy(-1);
//                        try {
//                            Thread.sleep(10);
//                        } catch (InterruptedException e) {
//                            // TODO Auto-generated catch block
//                            e.printStackTrace();
//                        }
//                    }
//                }
//            }
//        });
    }

    public void setFragments(boolean setGameFragment)
    {
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_in_left,R.anim.slide_in_right);
        if(setGameFragment)
        {
            fragmentTransaction.replace(R.id.fragment_container, games_fragment);
            playGame = true;
            btn_playpause.setChecked(true);
            fragmentTransaction.commitAllowingStateLoss();
        }
        else
        {
            fragmentTransaction.replace(R.id.fragment_container, paused_fragment);
            playGame = false;
            btn_playpause.setChecked(false);
            fragmentTransaction.commitAllowingStateLoss();
        }
    }

    public void setupAd() {
        showAd = false;

        interstitialAds = new InterstitialAd(this);
        interstitialAds.setAdUnitId("ca-app-pub-1292088626957289/8877363854");//my Admob Id

        // Create an ad request.
        adRequestBuilder = new AdRequest.Builder();

        // Optionally populate the ad request builder.
        adRequestBuilder.addTestDevice(AdRequest.DEVICE_ID_EMULATOR);
        adRequestBuilder.addTestDevice("0123456789ABCDEF");

        // Set an AdListener.
        interstitialAds.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                if (showAd) {
                    interstitialAds.show();
                    showAd = false;
                }
            }

            @Override
            public void onAdClosed() {

            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                super.onAdFailedToLoad(errorCode);
            }
        });
    }

    public void loadAd() {
        interstitialAds.loadAd(adRequestBuilder.build());
    }

    public void showAd()
    {
        showAd = true;
        showGoogleAds();
    }
    public void showGoogleAds() {
        if (interstitialAds.isLoaded()) {
            interstitialAds.show();
            showAd = false;
            loadAd();
        } else {
            loadAd();
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.btn_playpause:
                setFragments(!playGame);
                if(!playGame)
                {
                    gameStop();
                }
                else
                {
                    gameRun();
                }
                break;
            case R.id.btn_help:
                HelpDialog helpDialog = new HelpDialog(this);
                helpDialog.show();
                break;
            case R.id.btn_info:
//                Uri uri = Uri.parse("http://www.kolrgame.wordpress.com");
//                Intent openKolrWebsite = new Intent(Intent.ACTION_VIEW, uri);
//                this.startActivity(openKolrWebsite);
                AboutDialog aboutDialog = new AboutDialog(this);
                aboutDialog.show();
                break;
            case R.id.btn_bgmusictoggle:
                if(btn_bgmusictoggle.isChecked())
                {
                    mPrefsEditor.putBoolean("PLAY_BACKGROUND_SOUND", true);
                    playMusic(true);
                }
                else
                {
                    mPrefsEditor.putBoolean("PLAY_BACKGROUND_SOUND", false);
                    playMusic(false);
                }
                mPrefsEditor.apply();
                break;
            case R.id.btn_miscsoundtoggle:
                if(btn_miscsoundtoggle.isChecked())
                {
                    games_fragment.setMiscSoundEnabled(true);
                    mPrefsEditor.putBoolean("PLAY_MISC_SOUND", true);
                }
                else
                {
                    games_fragment.setMiscSoundEnabled(false);
                    mPrefsEditor.putBoolean("PLAY_MISC_SOUND", false);
                }
                mPrefsEditor.apply();
                break;
        }
    }

    public void playMusic(boolean state)
    {
        if(state)
        {
            mBackgroundSound = new BackgroundSound();
            mBackgroundSound.execute();
        }
        else
        {
            if(mMediaPlayer != null) {
                try {
                    mMediaPlayer.stop();
                    mMediaPlayer.release();
                }
                catch (Exception e)
                {}
            }
        }
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        // if no network is available networkInfo will be null
        // otherwise check if we are connected
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        }
        return false;
    }

    public class BackgroundSound extends AsyncTask<Void, Void, Void> {
        protected void onPreExecute() {
            mMediaPlayer = MediaPlayer.create(KolrActivity.this, R.raw.bg_music);
        }
        protected Void doInBackground(Void... params) {
                mMediaPlayer.setLooping(true); // Set looping
                mMediaPlayer.setVolume(100, 100);
                mMediaPlayer.start();
            return null;
        }
        protected void onCancelled(Void v) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent intent) {
        if(playGame){
            paused_fragment.onActivityResult(requestCode,resultCode,intent);
        }
    }

}

