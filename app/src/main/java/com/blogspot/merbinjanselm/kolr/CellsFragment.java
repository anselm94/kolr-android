package com.blogspot.merbinjanselm.kolr;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.blogspot.merbinjanselm.kolr.R;
import com.blogspot.merbinjanselm.kolr.kolrClasses.kolrGame;
import com.blogspot.merbinjanselm.kolr.kolrInterfaces.onGameStateChangeListener;

import java.lang.reflect.Field;
import java.util.StringTokenizer;

/**
 * A simple {@link Fragment} subclass.
 */
public class CellsFragment extends Fragment {

    boolean gameForceClosed;

    static int KOLR_COLOR_RED       = 71;
    static int KOLR_COLOR_GREEN     = 72;
    static int KOLR_COLOR_BLUE      = 73;
    static int KOLR_COLOR_YELLOW    = 74;
    static int KOLR_COLOR_ORANGE    = 75;
    static int KOLR_COLOR_VIOLET    = 76;
    static int KOLR_COLOR_BLACK     = 77;

    static int ACTION_SIGN_IN_ONLY = 27;
    static int ACTION_SHOW_LEADERBOARD = 29;
    static int ACTION_SHOW_ACHIEVMENTS = 31;

    static int KOLR_CODE_GAME_END = 549;
    static int KOLR_CODE_GAME_PAUSE = 551;
    static int KOLR_CODE_GAME_PLAY = 552;
    static int KOLR_CODE_GAME_SCORE_CHANGE = 553;
    static int KOLR_CODE_GAME_RESET = 554;
    static int KOLR_CODE_GAME_LEVEL_CHANGE = 555;

    static int KOLR_MODE_EASY = 37;
    static int KOLR_MODE_MEDIUM = 39;
    static int KOLR_MODE_HARD = 41;

    kolrGame kolr;
    Button btn11,btn12,btn13,btn21,btn22,btn23,btn31,btn32,btn33;

    private onGameStateChangeListener mOnGameStateChangeListener;

    SharedPreferences preference;
    SharedPreferences.Editor mPrefsEditor;
    private boolean miscSoundEnabled;

    public CellsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cells, container, false);

        preference = getActivity().getSharedPreferences("KolrPref", getActivity().MODE_PRIVATE);
        mPrefsEditor = preference.edit();

        btn11 = (Button) view.findViewById(R.id.cell11);
        btn12 = (Button) view.findViewById(R.id.cell12);
        btn13 = (Button) view.findViewById(R.id.cell13);
        btn21 = (Button) view.findViewById(R.id.cell21);
        btn22 = (Button) view.findViewById(R.id.cell22);
        btn23 = (Button) view.findViewById(R.id.cell23);
        btn31 = (Button) view.findViewById(R.id.cell31);
        btn32 = (Button) view.findViewById(R.id.cell32);
        btn33 = (Button) view.findViewById(R.id.cell33);

        Button[] btnArray = new Button[]{btn11,btn12,btn13,btn21,btn22,btn23,btn31,btn32,btn33};

        kolr = new kolrGame(getActivity(),btnArray);

        kolr.setOnGameStateChangeListener(new onGameStateChangeListener() {
            @Override
            public void onGameStateChange(int gameParameterChangeCode) {
                if (mOnGameStateChangeListener != null) {
                    mOnGameStateChangeListener.onGameStateChange(KOLR_CODE_GAME_SCORE_CHANGE);
                }
                if(gameParameterChangeCode == KOLR_CODE_GAME_END)
                {
                    if (mOnGameStateChangeListener != null) {
                        storeEverything();
                        storeCellStates();
                        mOnGameStateChangeListener.onGameStateChange(KOLR_CODE_GAME_END);
                    }
                    gameForceClosed = true;
                }
                if(gameParameterChangeCode == KOLR_CODE_GAME_LEVEL_CHANGE)
                {
                    if (mOnGameStateChangeListener != null) {
                        mPrefsEditor.putInt("GAME_LEVEL", kolr.getLevel());
                        mPrefsEditor.commit();
                        mOnGameStateChangeListener.onGameStateChange(KOLR_CODE_GAME_LEVEL_CHANGE);
                    }
                }
            }
        });
        kolr.setMiscSoundEnabled(miscSoundEnabled);
        gameForceClosed =false;
        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        if(!gameForceClosed) {
            gameStop();
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        gameRun();
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

    public void setOnGameStateChangeListener(onGameStateChangeListener listener) {
        mOnGameStateChangeListener = listener;
    }

    private void gameRun() {
        restoreEverything();
        restoreCellStates();
        kolr.gameStart();
    }

    private void gameStop() {
        storeEverything();
        storeCellStates();
        kolr.gameStop();
    }

    private void gameReset() {
        kolr.gameReset();
    }

    public long getScore()
    {
        return kolr.getScore();
    }

    public long getHighScore()
    {
        return kolr.getHighScore();
    }

    public void setMiscSoundEnabled(boolean state)
    {
        if(kolr != null) {
            kolr.setMiscSoundEnabled(state);
        }
        else
        {
            this.miscSoundEnabled = state;
        }
    }

    public void setBackgroundSoundEnabled(boolean state)
    {
        kolr.setBackgroundSoundEnabled(state);
    }

    public void setColor(int code)
    {
        kolr.setCellColor(code);
    }

    private void restoreEverything() {
        kolr.setCellColor(Integer.parseInt(preference.getString("CURRENT_COLOR_MODE", "72")));
        //Setting up gameMode
        kolr.setMode(preference.getInt("CURRENT_GAME_MODE", KOLR_MODE_EASY));
        //setting up game level
        kolr.setLevel(preference.getInt("GAME_LEVEL", 1));

        //Score
        kolr.setScore(preference.getLong("GAME_SCORE", 0));

        //High Score
        kolr.setHighScore(preference.getLong("GAME_HIGH_SCORE", 0));

        kolr.setMiscSoundEnabled(preference.getBoolean("PLAY_MISC_SOUND", true));

        kolr.setBackgroundSoundEnabled(preference.getBoolean("PLAY_BACKGROUND_SOUND", true));

        if (mOnGameStateChangeListener != null) {
            mOnGameStateChangeListener.onGameStateChange(KOLR_CODE_GAME_SCORE_CHANGE);
        }
    }

    public void storeEverything() {

        mPrefsEditor.putString("CURRENT_COLOR_MODE", Integer.toString(kolr.getCellsColor()));

        mPrefsEditor.putInt("CURRENT_GAME_MODE", kolr.getMode());

        mPrefsEditor.putInt("GAME_LEVEL", kolr.getLevel());

        mPrefsEditor.putLong("GAME_SCORE", kolr.getScore());

        mPrefsEditor.putLong("GAME_HIGH_SCORE", kolr.getHighScore());

        mPrefsEditor.putBoolean("PLAY_MISC_SOUND", kolr.getMiscSoundEnabled());

        mPrefsEditor.putBoolean("PLAY_BACKGROUND_SOUND", kolr.getBackgroundSoundEnabled());

        mPrefsEditor.apply();
    }

    public void storeCellStates()
    {
        //storing cell_states
        int[] cell_states_int_array = kolr.getCellStates();
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < cell_states_int_array.length; i++) {
            str.append(cell_states_int_array[i]).append(",");
        }

        mPrefsEditor.putString("CELL_STATES", str.toString());

        mPrefsEditor.apply();
    }

    public void restoreCellStates()
    {
        //setting up cell states
        String cell_states_string = preference.getString("CELL_STATES", "0,0,0,0,0,0,0,0,0,");
        StringTokenizer cell_states_string_tokenizer = new StringTokenizer(cell_states_string, ",");
        int[] cell_states_int_array = new int[9];
        for (int i = 0; i < 9; i++) {
            cell_states_int_array[i] = Integer.parseInt(cell_states_string_tokenizer.nextToken());
        }
        kolr.setCellStates(cell_states_int_array);
    }
}
