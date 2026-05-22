package com.example.lab_1;

import android.content.Context;
import android.content.res.Resources;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class GridAdapter extends BaseAdapter {
    private final Context mContext;
    private final int mCols;
    private final int mRows;
    private final int mMode;
    private final int mActiveCells;
    private final ArrayList<String> arrPict = new ArrayList<>();
    private final String picturePrefix = "ani";
    private final Resources mRes;

    public enum Status {CELL_OPEN, CELL_CLOSE, CELL_DELETE}
    private final ArrayList<Status> arrStatus = new ArrayList<>();
    private final List<Integer> currentlyOpenIndices = new ArrayList<>();

    public GridAdapter(Context context, int cols, int rows, int mode, int activeCells) {
        mContext = context;
        mCols = cols;
        mRows = rows;
        mMode = mode;
        mActiveCells = activeCells;
        mRes = mContext.getResources();

        makePictArray();
    }

    private void makePictArray() {
        arrPict.clear();
        arrStatus.clear();

        List<String> activePicts = new ArrayList<>();
        int numUniqueImages = mActiveCells / mMode;

        for (int i = 1; i <= numUniqueImages; i++) {
            int imgNum = ((i - 1) % 12) + 1;
            for (int j = 0; j < mMode; j++) {
                activePicts.add(picturePrefix + imgNum);
            }
        }
        Collections.shuffle(activePicts);

        int totalCells = mCols * mRows;
        int emptyCells = totalCells - mActiveCells;

        for (int i = 0; i < mActiveCells; i++) {
            arrPict.add(activePicts.get(i));
            arrStatus.add(Status.CELL_CLOSE);
        }
        for (int i = 0; i < emptyCells; i++) {
            arrPict.add(null);
            arrStatus.add(Status.CELL_DELETE);
        }

        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < totalCells; i++) indices.add(i);
        Collections.shuffle(indices);

        ArrayList<String> shuffledPict = new ArrayList<>(Collections.nCopies(totalCells, (String)null));
        ArrayList<Status> shuffledStatus = new ArrayList<>(Collections.nCopies(totalCells, Status.CELL_DELETE));

        for (int i = 0; i < totalCells; i++) {
            shuffledPict.set(i, arrPict.get(indices.get(i)));
            shuffledStatus.set(i, arrStatus.get(indices.get(i)));
        }

        arrPict.clear();
        arrPict.addAll(shuffledPict);
        arrStatus.clear();
        arrStatus.addAll(shuffledStatus);
    }

    @Override
    public int getCount() {
        return mCols * mRows;
    }

    @Override
    public Object getItem(int position) {
        return arrPict.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView view;
        if (convertView == null) {
            view = new ImageView(mContext);
            int gridWidth = parent.getWidth();
            if (gridWidth == 0) gridWidth = mContext.getResources().getDisplayMetrics().widthPixels - 100;
            int size = gridWidth / mCols;
            view.setLayoutParams(new GridView.LayoutParams(size, size));
            view.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            view.setPadding(4, 4, 4, 4);
        } else {
            view = (ImageView) convertView;
        }

        Status status = arrStatus.get(position);
        switch (status) {
            case CELL_OPEN:
                String pictName = arrPict.get(position);
                int drawableId = mRes.getIdentifier(pictName, "drawable", mContext.getPackageName());
                view.setImageResource(drawableId != 0 ? drawableId : android.R.drawable.ic_menu_report_image);
                view.setBackgroundColor(mContext.getResources().getColor(R.color.card_background_flipped));
                break;
            case CELL_CLOSE:
                view.setImageResource(android.R.drawable.ic_menu_help);
                view.setBackgroundColor(mContext.getResources().getColor(R.color.card_background));
                break;
            case CELL_DELETE:
                view.setImageDrawable(null);
                view.setBackgroundColor(android.graphics.Color.TRANSPARENT);
                break;
        }

        return view;
    }

    public boolean handleCellClick(int position) {
        if (arrStatus.get(position) != Status.CELL_CLOSE) return false;

        if (currentlyOpenIndices.size() == mMode) {
            boolean allMatch = true;
            String firstPict = arrPict.get(currentlyOpenIndices.get(0));
            for (int idx : currentlyOpenIndices) {
                if (arrPict.get(idx) == null || !arrPict.get(idx).equals(firstPict)) {
                    allMatch = false;
                    break;
                }
            }

            for (int idx : currentlyOpenIndices) {
                arrStatus.set(idx, allMatch ? Status.CELL_DELETE : Status.CELL_CLOSE);
            }
            currentlyOpenIndices.clear();
        }

        arrStatus.set(position, Status.CELL_OPEN);
        currentlyOpenIndices.add(position);

        notifyDataSetChanged();
        return true;
    }

    public boolean isGameOver() {
        for (Status s : arrStatus) {
            if (s == Status.CELL_CLOSE) return false;
        }

        if (currentlyOpenIndices.size() == mMode) {
            boolean allMatch = true;
            String firstPict = arrPict.get(currentlyOpenIndices.get(0));
            for (int idx : currentlyOpenIndices) {
                if (arrPict.get(idx) == null || !arrPict.get(idx).equals(firstPict)) {
                    allMatch = false;
                    break;
                }
            }
            return allMatch;
        }
        return false;
    }

    public int getRemainingSets() {
        int count = 0;
        for (Status s : arrStatus) {
            if (s == Status.CELL_CLOSE || s == Status.CELL_OPEN) count++;
        }
        
        int sets = count / mMode;

        if (currentlyOpenIndices.size() == mMode) {
            boolean allMatch = true;
            String firstPict = arrPict.get(currentlyOpenIndices.get(0));
            for (int idx : currentlyOpenIndices) {
                if (arrPict.get(idx) == null || !arrPict.get(idx).equals(firstPict)) {
                    allMatch = false;
                    break;
                }
            }
            if (allMatch) sets--;
        }
        
        return Math.max(0, sets);
    }
}
