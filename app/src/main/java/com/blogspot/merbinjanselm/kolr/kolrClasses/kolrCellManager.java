package com.blogspot.merbinjanselm.kolr.kolrClasses;

import android.content.Context;
import android.widget.Button;

import com.blogspot.merbinjanselm.kolr.kolrClasses.kolrCell;
import com.blogspot.merbinjanselm.kolr.kolrInterfaces.onClickListener;
import com.blogspot.merbinjanselm.kolr.kolrInterfaces.onStateChangeListener;

import java.util.ArrayList;

/**
 * Created by anselm94 on 22/12/14.
 */
public class kolrCellManager {

    static int KOLR_CELLS_COUNT = 9;

    int cellShape;

    Context context;

    Button btn11;
    Button btn12;
    Button btn13;
    Button btn21;
    Button btn22;
    Button btn23;
    Button btn31;
    Button btn32;
    Button btn33;

    kolrCell cell11;
    kolrCell cell12;
    kolrCell cell13;
    kolrCell cell21;
    kolrCell cell22;
    kolrCell cell23;
    kolrCell cell31;
    kolrCell cell32;
    kolrCell cell33;

    int state_cell11;
    int state_cell12;
    int state_cell13;
    int state_cell21;
    int state_cell22;
    int state_cell23;
    int state_cell31;
    int state_cell32;
    int state_cell33;

    int[] cellColors;

    int[] cellStates;

    ArrayList<kolrCell> activatedCells;

    private onStateChangeListener mOnStateChangeListener;

    public kolrCellManager(Context mContext, Button[] btnArray) {
        this.context = mContext;

        this.btn11 = btnArray[0];
        this.btn12 = btnArray[1];
        this.btn13 = btnArray[2];
        this.btn21 = btnArray[3];
        this.btn22 = btnArray[4];
        this.btn23 = btnArray[5];
        this.btn31 = btnArray[6];
        this.btn32 = btnArray[7];
        this.btn33 = btnArray[8];

        cell11 = new kolrCell(context, btn11,11);
        cell12 = new kolrCell(context, btn12,12);
        cell13 = new kolrCell(context, btn13,13);
        cell21 = new kolrCell(context, btn21,21);
        cell22 = new kolrCell(context, btn22,22);
        cell23 = new kolrCell(context, btn23,23);
        cell31 = new kolrCell(context, btn31,31);
        cell32 = new kolrCell(context, btn32,32);
        cell33 = new kolrCell(context, btn33,33);

        cell11.setOnClickListener(new onClickListener() {
            @Override
            public void onClick(kolrCell cell, int cellState,int cellId) {
                state_cell11 = cellState;
                if (mOnStateChangeListener != null)
                    mOnStateChangeListener.onStateChange(cellId,cellState);
            }
        });
        cell12.setOnClickListener(new onClickListener() {
            @Override
            public void onClick(kolrCell cell, int cellState,int cellId) {
                state_cell12 = cellState;
                if (mOnStateChangeListener != null)
                    mOnStateChangeListener.onStateChange(cellId,cellState);
            }
        });
        cell13.setOnClickListener(new onClickListener() {
            @Override
            public void onClick(kolrCell cell, int cellState,int cellId) {
                state_cell13 = cellState;
                if (mOnStateChangeListener != null)
                    mOnStateChangeListener.onStateChange(cellId,cellState);
            }
        });
        cell21.setOnClickListener(new onClickListener() {
            @Override
            public void onClick(kolrCell cell, int cellState,int cellId) {
                state_cell21 = cellState;
                if (mOnStateChangeListener != null)
                    mOnStateChangeListener.onStateChange(cellId,cellState);
            }
        });
        cell22.setOnClickListener(new onClickListener() {
            @Override
            public void onClick(kolrCell cell, int cellState,int cellId) {
                state_cell22 = cellState;
                if (mOnStateChangeListener != null)
                    mOnStateChangeListener.onStateChange(cellId,cellState);
            }
        });
        cell23.setOnClickListener(new onClickListener() {
            @Override
            public void onClick(kolrCell cell, int cellState,int cellId) {
                state_cell23 = cellState;
                if (mOnStateChangeListener != null)
                    mOnStateChangeListener.onStateChange(cellId,cellState);
            }
        });
        cell31.setOnClickListener(new onClickListener() {
            @Override
            public void onClick(kolrCell cell, int cellState,int cellId) {
                state_cell31 = cellState;
                if (mOnStateChangeListener != null)
                    mOnStateChangeListener.onStateChange(cellId,cellState);
            }
        });
        cell32.setOnClickListener(new onClickListener() {
            @Override
            public void onClick(kolrCell cell, int cellState,int cellId) {
                state_cell32 = cellState;
                if (mOnStateChangeListener != null)
                    mOnStateChangeListener.onStateChange(cellId,cellState);
            }
        });
        cell33.setOnClickListener(new onClickListener() {
            @Override
            public void onClick(kolrCell cell, int cellState,int cellId) {
                state_cell33 = cellState;
                if (mOnStateChangeListener != null)
                    mOnStateChangeListener.onStateChange(cellId,cellState);
            }
        });

        cellStates = new int[9];
        activatedCells = new ArrayList<kolrCell>();
    }

    public void setOnStateChangeListener(onStateChangeListener listener) {
        mOnStateChangeListener = listener;
    }

    public void setCellsColor(int[] colors) {
        this.cellColors = colors;

        cell11.setColorPalatte(cellColors);
        cell12.setColorPalatte(cellColors);
        cell13.setColorPalatte(cellColors);
        cell21.setColorPalatte(cellColors);
        cell22.setColorPalatte(cellColors);
        cell23.setColorPalatte(cellColors);
        cell31.setColorPalatte(cellColors);
        cell32.setColorPalatte(cellColors);
        cell33.setColorPalatte(cellColors);
    }

    public void setCellStates(int[] mCellStates) {
        cellStates = mCellStates;
        cell11.setState(mCellStates[0]);
        cell12.setState(mCellStates[1]);
        cell13.setState(mCellStates[2]);
        cell21.setState(mCellStates[3]);
        cell22.setState(mCellStates[4]);
        cell23.setState(mCellStates[5]);
        cell31.setState(mCellStates[6]);
        cell32.setState(mCellStates[7]);
        cell33.setState(mCellStates[8]);
    }

    public void setCellShapes(int mCellshape) {
        this.cellShape = mCellshape;
        cell11.setCellShape(cellShape);
        cell12.setCellShape(cellShape);
        cell13.setCellShape(cellShape);
        cell21.setCellShape(cellShape);
        cell22.setCellShape(cellShape);
        cell23.setCellShape(cellShape);
        cell31.setCellShape(cellShape);
        cell32.setCellShape(cellShape);
        cell33.setCellShape(cellShape);
    }

    public int[] getCellStates() {
        cellStates[0] = cell11.getState();
        cellStates[1] = cell12.getState();
        cellStates[2] = cell13.getState();
        cellStates[3] = cell21.getState();
        cellStates[4] = cell22.getState();
        cellStates[5] = cell23.getState();
        cellStates[6] = cell31.getState();
        cellStates[7] = cell32.getState();
        cellStates[8] = cell33.getState();
        return cellStates;
    }



    public void reset() {
        activatedCells.clear();
        cell11.setBlank();
        cell12.setBlank();
        cell13.setBlank();
        cell21.setBlank();
        cell22.setBlank();
        cell23.setBlank();
        cell31.setBlank();
        cell32.setBlank();
        cell33.setBlank();
    }

    public void paused() {
        activatedCells.clear();
        cell11.setState(-1);
        cell12.setState(-1);
        cell13.setState(-1);
        cell21.setState(-1);
        cell22.setState(-1);
        cell23.setState(-1);
        cell31.setState(-1);
        cell32.setState(-1);
        cell33.setState(-1);
    }

    public ArrayList<kolrCell> getActivatedCells() {
        activatedCells.clear();
        kolrCell mCell;
        for (int i = 1; i <= 9; i++) {
            mCell = getCell(i);
            if (mCell.getActivated()) {
                activatedCells.add(mCell);
            }
        }
        return activatedCells;
    }

    public int getActivatedCellsCount() {
        return this.getActivatedCells().size();
    }

    public kolrCell getCell(int cellNo) {
        kolrCell mCell = new kolrCell();

        switch (cellNo) {
            case 1:
                mCell = cell11;
                break;
            case 2:
                mCell = cell12;
                break;
            case 3:
                mCell = cell13;
                break;
            case 4:
                mCell = cell21;
                break;
            case 5:
                mCell = cell22;
                break;
            case 6:
                mCell = cell23;
                break;
            case 7:
                mCell = cell31;
                break;
            case 8:
                mCell = cell32;
                break;
            case 9:
                mCell = cell33;
                break;
            default:
                break;
        }

        return mCell;
    }
}
