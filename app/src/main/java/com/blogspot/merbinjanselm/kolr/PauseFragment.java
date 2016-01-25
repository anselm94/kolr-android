package com.blogspot.merbinjanselm.kolr;


import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesStatusCodes;
import com.google.android.gms.games.leaderboard.LeaderboardVariant;
import com.google.android.gms.games.leaderboard.Leaderboards;
import com.google.android.gms.plus.Plus;
import com.google.example.games.basegameutils.BaseGameUtils;

import java.lang.reflect.Field;


/**
 * A simple {@link Fragment} subclass.
 */
public class PauseFragment extends Fragment implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    static int KOLR_MODE_EASY = 37;
    static int KOLR_MODE_MEDIUM = 39;
    static int KOLR_MODE_HARD = 41;

    static int ACTION_SIGN_IN_ONLY = 27;
    static int ACTION_SHOW_LEADERBOARD = 29;
    static int ACTION_SHOW_ACHIEVMENTS = 31;

    private static int RC_SIGN_IN = 9001;

    private static int REQUEST_LEADERBOARD = 39;
    private static int REQUEST_ACHIEVEMENTS = 41;

    private static String KOLR_LEADEBOARD_ID = "CgkIlfqJqfQVEAIQAQ";

    private boolean mResolvingConnectionFailure = false;
    private boolean mAutoStartSignInFlow = true;
    private boolean mSignInClicked = false;

    int actionGiven;

    private GoogleApiClient mGoogleApiClient;
    Handler mHandler;

    ToggleButton btn_level_easy, btn_level_medium, btn_level_hard;
    Button btn_rate, btn_leaderboard, btn_achievements, btn_settings, btn_fb, btn_twitter, btn_gplus;

    ProgressDialog pd_loading;

    SharedPreferences preference;
    SharedPreferences.Editor mPrefsEditor;

    Typeface gameFont_main;

    public PauseFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pause, container, false);

        gameFont_main = Typeface.createFromAsset(getActivity().getAssets(), "fonts/skranji.ttf");

        btn_level_easy = (ToggleButton) view.findViewById(R.id.btn_level_easy);
        btn_level_medium = (ToggleButton) view.findViewById(R.id.btn_level_medium);
        btn_level_hard = (ToggleButton) view.findViewById(R.id.btn_level_hard);

        btn_rate = (Button) view.findViewById(R.id.btn_playpause_panel_rate);
        btn_leaderboard = (Button) view.findViewById(R.id.btn_leaderboard);
        btn_achievements = (Button) view.findViewById(R.id.btn_achievement);
        btn_settings = (Button) view.findViewById(R.id.btn_settings);
        btn_fb = (Button) view.findViewById(R.id.btn_fb);
        btn_twitter = (Button) view.findViewById(R.id.btn_twitter);
        btn_gplus = (Button) view.findViewById(R.id.btn_googleplus);

        btn_level_easy.setOnClickListener(this);
        btn_level_medium.setOnClickListener(this);
        btn_level_hard.setOnClickListener(this);

        btn_rate.setOnClickListener(this);
        btn_leaderboard.setOnClickListener(this);
        btn_achievements.setOnClickListener(this);
        btn_settings.setOnClickListener(this);
        btn_fb.setOnClickListener(this);
        btn_twitter.setOnClickListener(this);
        btn_gplus.setOnClickListener(this);

        btn_level_easy.setTypeface(gameFont_main);
        btn_level_medium.setTypeface(gameFont_main);
        btn_level_hard.setTypeface(gameFont_main);
        btn_rate.setTypeface(gameFont_main);

        pd_loading = new ProgressDialog(getActivity());
        pd_loading.setMessage("Connecting with Google Games...");

        preference = getActivity().getSharedPreferences("KolrPref", getActivity().MODE_PRIVATE);
        mPrefsEditor = preference.edit();

        mHandler = new Handler();

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API).addScope(Plus.SCOPE_PLUS_LOGIN)
                .addApi(Games.API).addScope(Games.SCOPE_GAMES)
                        // add other APIs and scopes here as needed
                .build();

        setLevel(preference.getInt("CURRENT_GAME_MODE", KOLR_MODE_EASY));
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);

        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_playpause_panel_rate:
                launchMarket();
                break;
            case R.id.btn_leaderboard:
                actionGiven = ACTION_SHOW_LEADERBOARD;
                if (mGoogleApiClient != null) {
                    if (mGoogleApiClient.isConnected()) {
                        Log.d("SIGN","BUTTON - is connected. so reconnecting");
                        mGoogleApiClient.reconnect();
                    } else {
                        Log.d("SIGN","BUTTON - connecting");
                        mGoogleApiClient.connect();
                    }
                    pd_loading.show();
                }
                break;
            case R.id.btn_achievement:
                actionGiven = ACTION_SHOW_ACHIEVMENTS;
                if (mGoogleApiClient != null) {
                    if (mGoogleApiClient.isConnected()) {
                        mGoogleApiClient.reconnect();
                    } else {
                        mGoogleApiClient.connect();
                    }
                    pd_loading.show();
                }
                break;
            case R.id.btn_settings:
                SettingsDialog settingsDialog = new SettingsDialog(getActivity());
                settingsDialog.show();
                break;
            case R.id.btn_fb:
                String facebookUrl = "https://www.facebook.com/kolrgame";
                try {
                    int versionCode = getActivity().getPackageManager().getPackageInfo("com.facebook.katana", 0).versionCode;
                    if (versionCode >= 3002850) {
                        Uri uri = Uri.parse("fb://facewebmodal/f?href=" + facebookUrl);
                        startActivity(new Intent(Intent.ACTION_VIEW, uri));
                        ;
                    } else {
                        // open the Facebook app using the old method (fb://profile/id or fb://page/id)
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("fb://page/710806025701857")));
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    // Facebook is not installed. Open the browser
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(facebookUrl)));
                }
                break;
            case R.id.btn_twitter:
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse("twitter://user?screen_name=kolrgame"));
                    startActivity(intent);

                } catch (Exception e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("https://twitter.com/kolrgame")));
                }
                break;
            case R.id.btn_googleplus:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://plus.google.com/104026769902942102211")));
                break;
            case R.id.btn_level_easy:
                setLevel(KOLR_MODE_EASY);
                break;
            case R.id.btn_level_medium:
                setLevel(KOLR_MODE_MEDIUM);
                break;
            case R.id.btn_level_hard:
                setLevel(KOLR_MODE_HARD);
                break;
        }
    }

    public void setLevel(int mode) {
        if (mode == KOLR_MODE_EASY) {
            btn_level_easy.setChecked(true);
            btn_level_medium.setChecked(false);
            btn_level_hard.setChecked(false);
        } else if (mode == KOLR_MODE_MEDIUM) {
            btn_level_easy.setChecked(false);
            btn_level_medium.setChecked(true);
            btn_level_hard.setChecked(false);
        } else if (mode == KOLR_MODE_HARD) {
            btn_level_easy.setChecked(false);
            btn_level_medium.setChecked(false);
            btn_level_hard.setChecked(true);
        }

        mPrefsEditor.putInt("CURRENT_GAME_MODE", mode);
        mPrefsEditor.apply();
    }

    public void launchMarket() {
        try {
            Uri uri = Uri.parse("market://details?id=" + getActivity().getPackageName());
            Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
            getActivity().startActivity(myAppLinkToMarket);
        } catch (ActivityNotFoundException e) {
            Uri uri = Uri.parse("http://details?id=" + getActivity().getPackageName());
            Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
            getActivity().startActivity(myAppLinkToMarket);
        }
    }

    public void onActivityResult(int requestCode, int resultCode,
                                 Intent intent) {
        Log.d("SIGN","onActivityResult");
        if (requestCode == RC_SIGN_IN) {
            mSignInClicked = false;
            mResolvingConnectionFailure = false;
            if (resultCode == getActivity().RESULT_OK) {
                mGoogleApiClient.connect();
            } else {
                // Bring up an error dialog to alert the user that sign-in
                // failed. The R.string.signin_failure should reference an error
                // string in your strings.xml file that tells the user they
                // could not be signed in, such as "Unable to sign in."
                BaseGameUtils.showActivityResultError(getActivity(), requestCode, resultCode, R.string.signin_failure);
            }
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        // (your code here: update UI, enable functionality that depends on sign in, etc)
        //loading things
        pd_loading.setMessage("Loading.");
        Log.d("SIGN", "Connected!");
        syncLeaderboardAchievement syncingThread = new syncLeaderboardAchievement();
        syncingThread.run();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (mResolvingConnectionFailure) {
            // already resolving
            if (connectionResult != null) {
                if (connectionResult.getErrorMessage() != null) {
                    Log.d("SIGN", connectionResult.getErrorMessage());
                } else {
                        Log.d("SIGN", String.valueOf(connectionResult.getErrorCode()));
                }
            }
            return;
        }

        // if the sign-in button was clicked or if auto sign-in is enabled,
        // launch the sign-in flow
        if (mSignInClicked || mAutoStartSignInFlow) {
            mAutoStartSignInFlow = false;
            mSignInClicked = false;
            mResolvingConnectionFailure = true;

            // Attempt to resolve the connection failure using BaseGameUtils.
            // The R.string.signin_other_error value should reference a generic
            // error string in your strings.xml file, such as "There was
            // an issue with sign-in, please try again later."
            pd_loading.dismiss();
            if (!BaseGameUtils.resolveConnectionFailure(getActivity(),
                    mGoogleApiClient, connectionResult,
                    RC_SIGN_IN, getResources().getString(R.string.signin_other_error))) {
                mResolvingConnectionFailure = false;
            }
        }
    }

    private boolean isScoreResultValid(final Leaderboards.LoadPlayerScoreResult scoreResult) {
        return scoreResult != null && GamesStatusCodes.STATUS_OK == scoreResult.getStatus().getStatusCode() && scoreResult.getScore() != null;
    }

    class syncLeaderboardAchievement implements Runnable {
        @Override
        public void run() {
            // Moves the current Thread into the background
            Log.d("SIGN","syncLeaderboardAchievement is running");
            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
            loadScore();
        }

        private void loadScore() {
            pd_loading.setMessage("Loading..");
            Games.Leaderboards.submitScore(mGoogleApiClient, KOLR_LEADEBOARD_ID, preference.getLong("GAME_HIGH_SCORE", 0));
            Games.Leaderboards.loadCurrentPlayerLeaderboardScore(mGoogleApiClient, KOLR_LEADEBOARD_ID, LeaderboardVariant.TIME_SPAN_ALL_TIME, LeaderboardVariant.COLLECTION_PUBLIC).setResultCallback(new ResultCallback<Leaderboards.LoadPlayerScoreResult>() {
                long onlineScore = 0;
                long highScore = 0;

                @Override
                public void onResult(final Leaderboards.LoadPlayerScoreResult scoreResult) {
                    pd_loading.setMessage("Loading...");
                    if (isScoreResultValid(scoreResult)) {
                        // here you can get the score like this
                        onlineScore = (Long) scoreResult.getScore().getRawScore();
                        if (onlineScore < preference.getLong("GAME_HIGH_SCORE", 0)) {
                            highScore = preference.getLong("GAME_HIGH_SCORE", 0);
                            Games.Leaderboards.submitScore(mGoogleApiClient, KOLR_LEADEBOARD_ID, preference.getLong("GAME_HIGH_SCORE", 0));
                        } else {
                            highScore = onlineScore;
                            SharedPreferences.Editor prefEditor = preference.edit();
                            prefEditor.putLong("GAME_HIGH_SCORE", onlineScore);
                            prefEditor.commit();
                        }

                        //Processing Achievement

                        if (highScore > 5000) {
                            Games.Achievements.unlock(mGoogleApiClient, getResources().getString(R.string.achievement_iron_medal));
                        }

                        if (highScore > 20000) {
                            Games.Achievements.unlock(mGoogleApiClient, getResources().getString(R.string.achievement_tin_medal));
                        }

                        if (highScore > 35000) {
                            Games.Achievements.unlock(mGoogleApiClient, getResources().getString(R.string.achievement_copper_medal));
                        }

                        if (highScore > 50000) {
                            Games.Achievements.unlock(mGoogleApiClient, getResources().getString(R.string.achievement_bronze_medal));
                        }

                        if (highScore > 100000) {
                            Games.Achievements.unlock(mGoogleApiClient, getResources().getString(R.string.achievement_silver_medal));
                        }

                        if (highScore > 500000) {
                            Games.Achievements.unlock(mGoogleApiClient, getResources().getString(R.string.achievement_gold_medal));
                        }

                        if (highScore > 1000000) {
                            Games.Achievements.unlock(mGoogleApiClient, getResources().getString(R.string.achievement_platinum_medal));
                        }

                        if (highScore > 5000000) {
                            Games.Achievements.unlock(mGoogleApiClient, getResources().getString(R.string.achievement_eternity));
                        }

                        //Processing required request
                        if (actionGiven == ACTION_SIGN_IN_ONLY) {

                        } else if (actionGiven == ACTION_SHOW_ACHIEVMENTS) {
                            Log.d("SIGN","onResult loadScore Achievement");
                            pd_loading.dismiss();
                            startActivityForResult(Games.Achievements.getAchievementsIntent(mGoogleApiClient), REQUEST_ACHIEVEMENTS);
                        } else if (actionGiven == ACTION_SHOW_LEADERBOARD) {
                            Log.d("SIGN","onResult leaderboard Achievement");
                            pd_loading.dismiss();
                            startActivityForResult(Games.Leaderboards.getLeaderboardIntent(mGoogleApiClient, KOLR_LEADEBOARD_ID), REQUEST_LEADERBOARD);
                        }
                    }
                }
            });
        }
    }

}
