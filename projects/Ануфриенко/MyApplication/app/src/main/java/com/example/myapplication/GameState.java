package com.example.myapplication;

import java.util.Random;

public class GameState {

    public enum FieldMode {
        FIXED_4,
        FIXED_5,
        FIXED_6,
        ORDER
    }

    private FieldMode fieldMode = FieldMode.FIXED_4;
    private boolean endlessMode = false;

    private int matchedGroups;
    private int totalGroups;

    private int currentOrderSize = 4;
    private boolean hardMode = false;

    private static final long BONUS_TIME_MS = 10_000L;

    public GameState() {
        reset();
    }

    public void reset() {
        matchedGroups = 0;
        totalGroups = 0;
        currentOrderSize = 4;
    }

    public int getNextFieldSize() {
        if (!endlessMode) {
            return resolveFixedSize();
        }

        if (fieldMode == FieldMode.ORDER) {
            int size = currentOrderSize;

            if (currentOrderSize < 6) {
                currentOrderSize++;
            } else {
                currentOrderSize = 4 + new Random().nextInt(3);
            }

            return size;
        }

        return resolveFixedSize();
    }

    private int resolveFixedSize() {
        switch (fieldMode) {
            case FIXED_4: return 4;
            case FIXED_5: return 5;
            case FIXED_6: return 6;
            case ORDER: return currentOrderSize;
            default: return 4;
        }
    }

    public void incrementMatchedGroups() {
        matchedGroups++;
    }

    public void setTotalGroups(int totalGroups) {
        this.totalGroups = totalGroups;
    }

    public boolean isLevelComplete() {
        return matchedGroups >= totalGroups;
    }

    public long getBonusTimeMillis() {
        return BONUS_TIME_MS;
    }

    public void setHardMode(boolean hardMode) {
        this.hardMode = hardMode;
    }

    public boolean isHardMode() {
        return hardMode;
    }

    public void setFieldMode(FieldMode mode) {
        this.fieldMode = mode;
        currentOrderSize = 4;
    }

    public void setEndlessMode(boolean endless) {
        this.endlessMode = endless;
    }

    public boolean isEndlessMode() {
        return endlessMode;
    }
    public void resetProgress() {
        matchedGroups = 0;
        totalGroups = 0;
    }
    public String getModeDescription() {

        StringBuilder sb = new StringBuilder();

        if (endlessMode) {
            sb.append("Бесконечный, ");
        }

        if (hardMode) {
            sb.append("Сложный, ");
        }

        switch (fieldMode) {
            case FIXED_4: sb.append("4x4"); break;
            case FIXED_5: sb.append("5x5"); break;
            case FIXED_6: sb.append("6x6"); break;
            case ORDER: sb.append("По порядку"); break;
        }

        return sb.toString();
    }
}