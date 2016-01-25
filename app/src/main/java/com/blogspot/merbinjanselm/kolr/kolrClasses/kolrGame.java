package com.blogspot.merbinjanselm.kolr.kolrClasses;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.widget.Button;

import com.blogspot.merbinjanselm.kolr.R;
import com.blogspot.merbinjanselm.kolr.kolrInterfaces.onGameStateChangeListener;
import com.blogspot.merbinjanselm.kolr.kolrInterfaces.onStateChangeListener;

import java.util.HashMap;
import java.util.Random;

/**
 * Created by anselm94 on 8/1/15.
 */
public class kolrGame {

    static int KOLR_CELLS_COUNT = 9;
    static int KOLR_STEPS_FOR_AI_ANALYSIS = 30;

    static int KOLR_CODE_GAME_END = 549;
    static int KOLR_CODE_GAME_PAUSE = 551;
    static int KOLR_CODE_GAME_PLAY = 552;
    static int KOLR_CODE_GAME_SCORE_CHANGE = 553;
    static int KOLR_CODE_GAME_RESET = 554;
    static int KOLR_CODE_GAME_LEVEL_CHANGE = 555;

    static int KOLR_MODE_EASY = 37;
    static int KOLR_MODE_MEDIUM = 39;
    static int KOLR_MODE_HARD = 41;

    static int KOLR_SPAWNTIME_LEVEL_1 = 650;
    static int KOLR_SPAWNTIME_LEVEL_2 = 500;
    static int KOLR_SPAWNTIME_LEVEL_3 = 350;
    static int KOLR_SPAWNTIME_LEVEL_4 = 225;
    static int KOLR_SPAWNTIME_LEVEL_5 = 150;

    static double KOLR_LEVEL_2_SWITCH_SD = 0.333333;//TODO Set Benchmark value for SD
    static double KOLR_LEVEL_3_SWITCH_SD = 0.5;
    static double KOLR_LEVEL_4_SWITCH_SD = 0.65;
    static double KOLR_LEVEL_5_SWITCH_SD = 0.8;

    static int KOLR_LEVEL_1_VALUE = 1;
    static int KOLR_LEVEL_2_VALUE = 2;
    static int KOLR_LEVEL_3_VALUE = 3;
    static int KOLR_LEVEL_4_VALUE = 4;
    static int KOLR_LEVEL_5_VALUE = 5;

    static int CELL_SHAPE_ROUNDEDSQUARE = 1;
    static int CELL_SHAPE_SQUARE = 2;
    static int CELL_SHAPE_ROUND = 3;
    static int CELL_SHAPE_FUNNY = 4;

    static int KOLR_COLOR_RED       = 71;
    static int KOLR_COLOR_GREEN     = 72;
    static int KOLR_COLOR_BLUE      = 73;
    static int KOLR_COLOR_YELLOW    = 74;
    static int KOLR_COLOR_ORANGE    = 75;
    static int KOLR_COLOR_VIOLET    = 76;
    static int KOLR_COLOR_BLACK     = 77;

    private static int ACTION_SIGN_IN_ONLY = 27;
    private static int ACTION_SHOW_LEADERBOARD = 29;
    private static int ACTION_SHOW_ACHIEVMENTS = 31;

    static int SOUND_CLICK = 256;
    static int SOUND_LEVEL_CHANGE = 257;
    static int SOUND_GAME_OVER = 258;
    static int SOUND_CELL_DEACTIVATED = 259;

    Context context;
    onGameStateChangeListener mOnGameStateChangeListener;
    kolrCellManager kolrManager;
    spawnThread spawnTask;

    int gamePlayCount;//Total no. of game plays to force the player to rate
    boolean gameRunning;
    long kolrScore;
    long kolrhighScore;
    int kolrMode;
    long kolrSteps;
    int kolrLevel;
    int kolrLevelValue;
    int kolrSpawnTime;
    int kolrCellsShape;

    boolean playMiscSound;
    boolean playBackgoundSound;

    //Spawn time for each mode
    int kolrSpawnTimeLevel1;
    int kolrSpawnTimeLevel2;
    int kolrSpawnTimeLevel3;

    //levels for each mode
    int kolrLevel1 = 1;
    int kolrLevel2 = 2;
    int kolrLevel3 = 3;

    int kolrLevelValue1;
    int kolrLevelValue2;
    int kolrLevelValue3;

    double kolrLevel2SwitchSD;
    double kolrLevel3SwitchSD;

    private int color_1;
    private int color_2;
    private int color_3;
    private int color_4;
    private int color_5;

    int kolrCellColor;

    SoundPool kolrSoundPool;
    HashMap<Integer, Integer> kolrSoundPoolMap;

    public kolrGame(Context mContext,Button[] btnArray) {
        this.context = mContext;
        kolrManager = new kolrCellManager(this.context, btnArray);

        this.gamePlayCount = 0;
        this.gameRunning = false;
        this.kolrScore = 0;
        this.kolrhighScore = 0;

        this.kolrCellColor = KOLR_COLOR_GREEN;
        setCellColor(kolrCellColor);

        setMode(KOLR_MODE_EASY);
        this.kolrLevel = kolrLevel1;
        this.kolrLevelValue = kolrLevelValue1;
        this.kolrSpawnTime = kolrSpawnTimeLevel1;
        this.kolrSteps = 0;

        kolrManager.setOnStateChangeListener(new onStateChangeListener() {
            @Override
            public void onStateChange(int cellId,int cellState) {
                if(cellState > 0 && cellState <= 5) {
                    kolrSteps++;
                    kolrScore = kolrScore + (kolrManager.getActivatedCellsCount() * kolrLevelValue);
                    if (kolrScore > kolrhighScore)// High Score setting up
                    {
                        kolrhighScore = kolrScore;
                    }
                    if(playMiscSound)
                    {
                        kolrSoundPool.play(kolrSoundPoolMap.get(SOUND_CLICK), 1, 1, 1, 0, 1);
                    }
                }
                else if(cellState == 6)
                {
                    if(playMiscSound)
                    {
                        kolrSoundPool.play(kolrSoundPoolMap.get(SOUND_CELL_DEACTIVATED), 1, 1, 1, 0, 1);
                    }
                }
                if (mOnGameStateChangeListener != null) {
                    mOnGameStateChangeListener.onGameStateChange(KOLR_CODE_GAME_SCORE_CHANGE);
                }
            }
        });

        loadSounds();
    }

    public void gameReset()
    {
        this.gamePlayCount++;
        this.gameRunning = false;

        try {
            spawnTask.interrupt();
        } catch (Exception e) {
        }
        //set blank cells
        kolrManager.reset();
        //reset score and indicator
        this.kolrScore = 0;
        setLevel(kolrLevel1);
        if (mOnGameStateChangeListener != null) {
            mOnGameStateChangeListener.onGameStateChange(KOLR_CODE_GAME_RESET);
        }
    }

    public void gameStart()
    {

        if (kolrManager.getActivatedCellsCount() != 0) {
            //set run flag parameter TRUE
            this.gameRunning = true;

            // start spawning cells
            try {
                spawnTask = new spawnThread();
                spawnTask.start();
            } catch (Exception e) {
            }

        } else {
            gameReset();
            gameStart();
        }
    }

    public void gameStop()
    {
        //set run flag as FALSE (to stop spawn thread)
        this.gameRunning = false;
        try {
            spawnTask.interrupt();
        } catch (Exception e) {
        }
        if (kolrManager.getActivatedCellsCount() == 0) {
            if(playMiscSound){
            kolrSoundPool.play(kolrSoundPoolMap.get(SOUND_GAME_OVER), 1, 1, 1, 0, 1);}
            if (mOnGameStateChangeListener != null) {
                mOnGameStateChangeListener.onGameStateChange(KOLR_CODE_GAME_END);
            }
            //TODO Notify Activity to show Result Dialog box
            //then show Ad and reset
        }
//        else {
//            kolrManager.paused();
//        }
    }

    public boolean getBackgroundSoundEnabled()
    {
        return this.playBackgoundSound;
    }

    public int getCellsColor()
    {
        return this.kolrCellColor;
    }

    public int getCellsShape()
    {
        return this.kolrCellsShape;
    }

    public int[] getCellStates()
    {
        return kolrManager.getCellStates();
    }

    public long getHighScore()
    {
        return kolrhighScore;
    }

    public int getLevel()
    {
        return kolrLevel;
    }

    public boolean getMiscSoundEnabled()
    {
        return this.playMiscSound;
    }

    public int getMode()
    {
        return this.kolrMode;
    }

    public boolean getRunningStatus()
    {
        return this.gameRunning;
    }

    public long getScore()
    {
        return kolrScore;
    }

    public double getStandardDeviationofKolrCells()
    {
        double cellStateAverage = 0;
        double SDofCellStates = 0;
        int[] cellStates = this.getCellStates();

        for (int i = 0; i < KOLR_CELLS_COUNT; i++) {
            cellStateAverage = cellStateAverage + cellStates[i];
        }
        cellStateAverage = cellStateAverage / KOLR_CELLS_COUNT;

        for (int i = 0; i < KOLR_CELLS_COUNT; i++) {
            SDofCellStates = SDofCellStates + (cellStates[i] - cellStateAverage)*(cellStates[i] - cellStateAverage);
        }
        SDofCellStates = SDofCellStates / (KOLR_CELLS_COUNT - 1);
        SDofCellStates = Math.sqrt(SDofCellStates);

        return SDofCellStates;
    }

    public long getSteps()
    {
        return kolrSteps;
    }

    public void loadSounds()
    {

        kolrSoundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 100);
        kolrSoundPoolMap = new HashMap<Integer, Integer>();
        kolrSoundPoolMap.put(SOUND_CLICK, kolrSoundPool.load(context, R.raw.blip, 1));
        kolrSoundPoolMap.put(SOUND_LEVEL_CHANGE, kolrSoundPool.load(context, R.raw.level_change, 1));
        kolrSoundPoolMap.put(SOUND_GAME_OVER, kolrSoundPool.load(context, R.raw.kolr_game_over, 1));
        kolrSoundPoolMap.put(SOUND_CELL_DEACTIVATED, kolrSoundPool.load(context, R.raw.deactivated, 1));
    }
    public void setOnGameStateChangeListener(onGameStateChangeListener listener) {
        mOnGameStateChangeListener = listener;
    }

    public void setBackgroundSoundEnabled(boolean state)
    {
        this.playBackgoundSound = state;
    }

    public void setCellColor(int colorCode)
    {
        this.kolrCellColor = colorCode;
        if(colorCode == KOLR_COLOR_RED)
        {
            this.color_1 = this.context.getResources().getColor(R.color.red_1);
            this.color_2 = this.context.getResources().getColor(R.color.red_2);
            this.color_3 = this.context.getResources().getColor(R.color.red_3);
            this.color_4 = this.context.getResources().getColor(R.color.red_4);
            this.color_5 = this.context.getResources().getColor(R.color.red_5);
        }
        else if(colorCode == KOLR_COLOR_GREEN)
        {
            this.color_1 = this.context.getResources().getColor(R.color.green_1);
            this.color_2 = this.context.getResources().getColor(R.color.green_2);
            this.color_3 = this.context.getResources().getColor(R.color.green_3);
            this.color_4 = this.context.getResources().getColor(R.color.green_4);
            this.color_5 = this.context.getResources().getColor(R.color.green_5);
        }
        else if(colorCode == KOLR_COLOR_BLUE)
        {
            this.color_1 = this.context.getResources().getColor(R.color.blue_1);
            this.color_2 = this.context.getResources().getColor(R.color.blue_2);
            this.color_3 = this.context.getResources().getColor(R.color.blue_3);
            this.color_4 = this.context.getResources().getColor(R.color.blue_4);
            this.color_5 = this.context.getResources().getColor(R.color.blue_5);
        }
        else if(colorCode == KOLR_COLOR_YELLOW)
        {
            this.color_1 = this.context.getResources().getColor(R.color.yellow_1);
            this.color_2 = this.context.getResources().getColor(R.color.yellow_2);
            this.color_3 = this.context.getResources().getColor(R.color.yellow_3);
            this.color_4 = this.context.getResources().getColor(R.color.yellow_4);
            this.color_5 = this.context.getResources().getColor(R.color.yellow_5);
        }
        else if(colorCode == KOLR_COLOR_ORANGE)
        {
            this.color_1 = this.context.getResources().getColor(R.color.orange_1);
            this.color_2 = this.context.getResources().getColor(R.color.orange_2);
            this.color_3 = this.context.getResources().getColor(R.color.orange_3);
            this.color_4 = this.context.getResources().getColor(R.color.orange_4);
            this.color_5 = this.context.getResources().getColor(R.color.orange_5);
        }
        else if(colorCode == KOLR_COLOR_VIOLET)
        {
            this.color_1 = this.context.getResources().getColor(R.color.violet_1);
            this.color_2 = this.context.getResources().getColor(R.color.violet_2);
            this.color_3 = this.context.getResources().getColor(R.color.violet_3);
            this.color_4 = this.context.getResources().getColor(R.color.violet_4);
            this.color_5 = this.context.getResources().getColor(R.color.violet_5);
        }
        else if(colorCode == KOLR_COLOR_BLACK)
        {
            this.color_1 = this.context.getResources().getColor(R.color.black_1);
            this.color_2 = this.context.getResources().getColor(R.color.black_2);
            this.color_3 = this.context.getResources().getColor(R.color.black_3);
            this.color_4 = this.context.getResources().getColor(R.color.black_4);
            this.color_5 = this.context.getResources().getColor(R.color.black_5);
        }

        int colors[] = new int[]{color_1,color_2,color_3,color_4,color_5};
        kolrManager.setCellsColor(colors);
    }

    public void setCellStates(int[] mCellStates)
    {
        this.kolrManager.setCellStates(mCellStates);
    }

    public void setHighScore(long hiScore)
    {
        this.kolrhighScore = hiScore;
    }

    public void setMiscSoundEnabled(boolean state)
    {
        this.playMiscSound = state;
    }

    public void setMode(int mode)
    {
        this.kolrMode = mode;
        if(mode == KOLR_MODE_EASY)
        {
            this.kolrLevelValue1 = KOLR_LEVEL_1_VALUE;
            this.kolrLevelValue2 = KOLR_LEVEL_2_VALUE;
            this.kolrLevelValue3 = KOLR_LEVEL_3_VALUE;

            this.kolrSpawnTimeLevel1 = KOLR_SPAWNTIME_LEVEL_1;
            this.kolrSpawnTimeLevel2 = KOLR_SPAWNTIME_LEVEL_2;
            this.kolrSpawnTimeLevel3 = KOLR_SPAWNTIME_LEVEL_3;

            this.kolrLevel2SwitchSD = KOLR_LEVEL_2_SWITCH_SD;
            this.kolrLevel3SwitchSD = KOLR_LEVEL_3_SWITCH_SD;
        }
        else if(mode == KOLR_MODE_MEDIUM)
        {
            this.kolrLevelValue1 = KOLR_LEVEL_2_VALUE;
            this.kolrLevelValue2 = KOLR_LEVEL_3_VALUE;
            this.kolrLevelValue3 = KOLR_LEVEL_4_VALUE;

            this.kolrSpawnTimeLevel1 = KOLR_SPAWNTIME_LEVEL_2;
            this.kolrSpawnTimeLevel2 = KOLR_SPAWNTIME_LEVEL_3;
            this.kolrSpawnTimeLevel3 = KOLR_SPAWNTIME_LEVEL_4;

            this.kolrLevel2SwitchSD = KOLR_LEVEL_3_SWITCH_SD;
            this.kolrLevel3SwitchSD = KOLR_LEVEL_4_SWITCH_SD;
        }
        else if(mode == KOLR_MODE_HARD)
        {
            this.kolrLevelValue1 = KOLR_LEVEL_3_VALUE;
            this.kolrLevelValue2 = KOLR_LEVEL_4_VALUE;
            this.kolrLevelValue3 = KOLR_LEVEL_5_VALUE;

            this.kolrSpawnTimeLevel1 = KOLR_SPAWNTIME_LEVEL_3;
            this.kolrSpawnTimeLevel2 = KOLR_SPAWNTIME_LEVEL_4;
            this.kolrSpawnTimeLevel3 = KOLR_SPAWNTIME_LEVEL_5;

            this.kolrLevel2SwitchSD = KOLR_LEVEL_4_SWITCH_SD;
            this.kolrLevel3SwitchSD = KOLR_LEVEL_5_SWITCH_SD;
        }
    }

    public void setPaused()
    {
        kolrManager.paused();
    }

    public void setScore(long score)
    {
        this.kolrScore = score;
    }

    public void setCellShape(int shapeCode)
    {
        this.kolrCellsShape = shapeCode;
        kolrManager.setCellShapes(kolrCellsShape);
    }

    public void setLevel(int level)
    {
        switch(level)
        {
            case 1:
                this.kolrLevel = 1;
                this.kolrLevelValue = kolrLevelValue1;
                this.kolrSpawnTime = kolrSpawnTimeLevel1;
                break;
            case 2:
                this.kolrLevel = 2;
                this.kolrLevelValue = kolrLevelValue2;
                this.kolrSpawnTime = kolrSpawnTimeLevel2;
                break;
            case 3:
                this.kolrLevel = 3;
                this.kolrLevelValue = kolrLevelValue3;
                this.kolrSpawnTime = kolrSpawnTimeLevel3;
                break;
        }
        if (mOnGameStateChangeListener != null) {
            mOnGameStateChangeListener.onGameStateChange(KOLR_CODE_GAME_LEVEL_CHANGE);
        }
    }

    class spawnThread extends Thread {
        Activity mActivity = (Activity)context;
        long prevNoOfSteps = 0;
        double SDofCells = 0;
        double[] SDvalues = new double[KOLR_STEPS_FOR_AI_ANALYSIS];
        double cumulatedCellSD = 0;

        @Override
        public void run() {
            while (gameRunning) {
                if (kolrManager.getActivatedCellsCount() > 0) {
                    //==================
                    if(prevNoOfSteps != kolrSteps) {
                        SDofCells = getStandardDeviationofKolrCells();
                        SDvalues[(int) (kolrSteps % KOLR_STEPS_FOR_AI_ANALYSIS)] = SDofCells;
                        if (kolrSteps > (KOLR_STEPS_FOR_AI_ANALYSIS - kolrManager.getActivatedCellsCount())) {
                            for (int i = 0; i < KOLR_STEPS_FOR_AI_ANALYSIS; i++) {
                                cumulatedCellSD = cumulatedCellSD + SDvalues[i];
                            }
                            cumulatedCellSD = cumulatedCellSD / KOLR_STEPS_FOR_AI_ANALYSIS;

                            if((cumulatedCellSD < kolrLevel2SwitchSD) && (kolrLevel == 1))
                            {
                                setLevel(kolrLevel2);
                                if(playMiscSound){
                                    kolrSoundPool.play(kolrSoundPoolMap.get(SOUND_LEVEL_CHANGE), 1, 1, 1, 0, 1);}
                            }
                            else if((cumulatedCellSD < kolrLevel3SwitchSD) && (kolrLevel == 2))
                            {
                                setLevel(kolrLevel3);
                                if(playMiscSound)
                                {
                                    kolrSoundPool.play(kolrSoundPoolMap.get(SOUND_LEVEL_CHANGE), 1, 1, 1, 0, 1);}
                            }
                            prevNoOfSteps = 0;
                            kolrSteps = 0;
                            SDofCells = 0;
                            SDvalues = new double[KOLR_STEPS_FOR_AI_ANALYSIS];
                            cumulatedCellSD = 0;
                        }

                    prevNoOfSteps = kolrSteps;
                }
                //========================================
                    Random r1 = new Random();
                    final int cellRandom1 = r1.nextInt(kolrManager.getActivatedCellsCount());
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                kolrManager.getActivatedCells().get(cellRandom1).incrementState();
                            } catch (Exception e) {
                            }
                        }
                    });

                    try {
                        Thread.sleep(kolrSpawnTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    gameRunning = false;
                    gameStop();
                }
            }
        }

        @Override
        public void interrupt() {
            super.interrupt();
            gameRunning = false;
        }
    }
}
