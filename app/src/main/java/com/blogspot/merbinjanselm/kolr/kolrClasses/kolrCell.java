package com.blogspot.merbinjanselm.kolr.kolrClasses;

import android.app.Activity;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.blogspot.merbinjanselm.kolr.R;
import com.blogspot.merbinjanselm.kolr.kolrInterfaces.onClickListener;

/**
 * Created by anselm94 on 19/12/14.
 */
public class kolrCell {

    Activity mActivity;

    static int CELL_SHAPE_ROUNDEDSQUARE = 1;
    static int CELL_SHAPE_SQUARE = 2;
    static int CELL_SHAPE_ROUND = 3;
    static int CELL_SHAPE_FUNNY = 4;

    Context context;
    Button cell;
    Drawable drawable;

    int mCellId;
    int cellState;//state of cell out of 5 states of colors
    boolean cellActivated;
    int cellColor;
    int cellShape;
    long noOfStateChanges;

    private onClickListener mOnClickListener;

    private int color_1;
    private int color_2;
    private int color_3;
    private int color_4;
    private int color_5;
    private int color_deactivated;
    private int color_blank;
    private int color_paused;

    public kolrCell() {

    }

    public kolrCell(Context mContext, Button mCell, int cellId) {

        this.context = mContext;
        this.cell = mCell;
        this.mCellId = cellId;

        color_deactivated = context.getResources().getColor(R.color.deactivated);
        color_blank = context.getResources().getColor(R.color.blank);
        color_paused = context.getResources().getColor(R.color.paused);

        this.cellShape = CELL_SHAPE_FUNNY;
        setCellShape(cellShape);

        drawable = this.cell.getBackground();
        drawable.mutate();

        this.cellState = 0;//Initialize the Cell state
        setColorState(cellState);//Initialize the cellColor as blank
        noOfStateChanges = 0;

        cell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cellState >= 1 && cellState <= 5) {
                    decrementState();
                }
            }
        });
    }

    public void setOnClickListener(onClickListener listener) {
        mOnClickListener = listener;
    }

    public void setColorPalatte(int[] colors) {
        this.color_1 = colors[0];
        this.color_2 = colors[1];
        this.color_3 = colors[2];
        this.color_4 = colors[3];
        this.color_5 = colors[4];
    }

    public void setColorState(int level) {
        Log.e("TEST3", String.valueOf(mCellId) + " , " + String.valueOf(level));
        switch (level) {
            case -1:
                this.cellColor = color_paused;
                this.cellActivated = false;
                drawable.setColorFilter(this.cellColor, PorterDuff.Mode.MULTIPLY);
                this.cell.setBackgroundDrawable(drawable);
                this.cell.setAlpha(1);
                break;
            case 0:
                this.cellColor = color_blank;
                this.cellActivated = true;
                drawable.setColorFilter(this.cellColor, PorterDuff.Mode.MULTIPLY);
                this.cell.setBackgroundDrawable(drawable);
                this.cell.setAlpha(1);
                break;
            case 1:
                this.cellColor = color_1;
                this.cellActivated = true;
                drawable.setColorFilter(this.cellColor, PorterDuff.Mode.MULTIPLY);
                this.cell.setBackgroundDrawable(drawable);
                this.cell.setAlpha(1);
                break;
            case 2:
                this.cellColor = color_2;
                this.cellActivated = true;
                drawable.setColorFilter(this.cellColor, PorterDuff.Mode.MULTIPLY);
                this.cell.setBackgroundDrawable(drawable);
                this.cell.setAlpha(1);
                break;
            case 3:
                this.cellColor = color_3;
                this.cellActivated = true;
                drawable.setColorFilter(this.cellColor, PorterDuff.Mode.MULTIPLY);
                this.cell.setBackgroundDrawable(drawable);
                this.cell.setAlpha(1);
                break;
            case 4:
                this.cellColor = color_4;
                this.cellActivated = true;
                drawable.setColorFilter(this.cellColor, PorterDuff.Mode.MULTIPLY);
                this.cell.setBackgroundDrawable(drawable);
                this.cell.setAlpha(1);
                break;
            case 5:
                this.cellColor = color_5;
                this.cellActivated = true;
                drawable.setColorFilter(this.cellColor, PorterDuff.Mode.MULTIPLY);
                this.cell.setBackgroundDrawable(drawable);
                this.cell.setAlpha(1);
                break;
            default:
                this.cellActivated = false;
                this.cell.setAlpha(0);
                break;
        }
    }

    public void setState(int level) {
        this.cellState = level;
        setColorState(level);
    }

    public void setCellShape(int shapeCode) {//TODO set shape
        if (shapeCode == CELL_SHAPE_ROUNDEDSQUARE) {
            cell.setBackgroundDrawable(this.context.getResources().getDrawable(R.drawable.shape_roundedsquare));
        } else if (shapeCode == CELL_SHAPE_SQUARE) {
            cell.setBackgroundDrawable(this.context.getResources().getDrawable(R.drawable.shape_square));
        } else if (shapeCode == CELL_SHAPE_ROUND) {
            cell.setBackgroundDrawable(this.context.getResources().getDrawable(R.drawable.shape_round));
        } else if (shapeCode == CELL_SHAPE_FUNNY) {
            cell.setBackgroundDrawable(this.context.getResources().getDrawable(R.drawable.shape_funny));
        }
    }

    public boolean getActivated() {
        return cellActivated;
    }

    public int getState() {
        return this.cellState;
    }

    public void incrementState() {
        if ((cellState <= 5) && (cellState >= 0)) {
            cellState++;
            setColorState(cellState);
        }
        if (cellState == 6) {
            if (mOnClickListener != null) {
                mOnClickListener.onClick(this, cellState, mCellId);
            }
        }
    }

    public void decrementState() {
        if (cellState <= 5 && cellState > 1) {
            cellState--;
            setColorState(cellState);
            this.cellActivated = true;
            this.noOfStateChanges++;

        } else if (cellState == 1) {
            cellState = 6;
            setColorState(cellState);
        }

        if (mOnClickListener != null) {
            mOnClickListener.onClick(this, cellState, mCellId);
        }

    }

    public void setBlank() {
        this.cellState = 0;
        setColorState(cellState);
    }


}
