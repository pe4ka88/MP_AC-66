package com.example.myapplication;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameEngine {
    public boolean isMatch(List<Card> opened) {

        if (opened == null || opened.isEmpty())
            return false;

        int expectedSize = opened.get(0).getGroupSize();

        if (opened.size() != expectedSize)
            return false;

        int imageId = opened.get(0).getImageResId();

        // Проверка на одинаковые экземпляры
        for (int i = 0; i < opened.size(); i++) {
            for (int j = i + 1; j < opened.size(); j++) {
                if (opened.get(i) == opened.get(j)) {
                    return false; // одна и та же карта
                }
            }
        }

        // Проверка совпадения картинки
        for (Card card : opened) {
            if (card.getImageResId() != imageId)
                return false;
        }

        return true;
    }
    public static class MoveResult {

        public enum Type {
            OPENED,
            WAIT_FOR_THIRD,
            MATCH,
            MISMATCH,
            JOKER
        }

        public final Type type;
        public final List<Card> affectedCards;

        public MoveResult(Type type, List<Card> affectedCards) {
            this.type = type;
            this.affectedCards = affectedCards;
        }
    }
    private final List<Card> openedCards = new ArrayList<>();

    public MoveResult onCardSelected(Card card) {

        if (card.isMatched()) return null;
        if (openedCards.contains(card)) return null;

        openedCards.add(card);

        // ===== ДЖОКЕР =====
        if (card.isJoker()) {

            List<Card> resultCards = new ArrayList<>(openedCards);
            openedCards.clear();

            if (resultCards.size() == 1) {
                return new MoveResult(MoveResult.Type.JOKER, resultCards);
            } else {
                return new MoveResult(MoveResult.Type.MISMATCH, resultCards);
            }
        }

        if (openedCards.size() == 1) {
            return new MoveResult(MoveResult.Type.OPENED, List.of(card));
        }

        Card first = openedCards.get(0);
        Card second = openedCards.get(1);

        if (openedCards.size() == 2 &&
                first.getImageResId() != second.getImageResId()) {

            List<Card> resultCards = new ArrayList<>(openedCards);
            openedCards.clear();
            return new MoveResult(MoveResult.Type.MISMATCH, resultCards);
        }

        if (openedCards.size() == 2 && first.getGroupSize() == 2) {

            markMatched(openedCards);
            List<Card> resultCards = new ArrayList<>(openedCards);
            openedCards.clear();
            return new MoveResult(MoveResult.Type.MATCH, resultCards);
        }

        if (openedCards.size() == 2 && first.getGroupSize() == 3) {
            return new MoveResult(MoveResult.Type.WAIT_FOR_THIRD,
                    new ArrayList<>(openedCards));
        }

        if (openedCards.size() == 3) {

            boolean match = isMatch(openedCards);
            List<Card> resultCards = new ArrayList<>(openedCards);

            if (match) {
                markMatched(openedCards);
                openedCards.clear();
                return new MoveResult(MoveResult.Type.MATCH, resultCards);
            } else {
                openedCards.clear();
                return new MoveResult(MoveResult.Type.MISMATCH, resultCards);
            }
        }

        return null;
    }
    public void markMatched(List<Card> cards) {
        for (Card card : cards) {
            card.setMatched(true);
        }
    }
    private List<Card> cards = new ArrayList<>();
    private int totalGroups = 0;

    public List<Card> startNewGame(
            int rows,
            int cols,
            int[] images,
            boolean includeJoker,
            boolean hardMode
    ) {
        cards.clear();

        int totalCells = rows * cols;
        boolean hasJoker = includeJoker && totalCells % 2 != 0;

        int usableCells = hasJoker ? totalCells - 1 : totalCells;
        int tripleCount = (hardMode && rows == 6 && cols == 6) ? 4 : 0;
        int tripleCards = tripleCount * 3;

        int remainingCells = usableCells - tripleCards;
        int pairCount = remainingCells / 2;

        totalGroups = pairCount + tripleCount;

        if (tripleCount + pairCount > images.length) {
            throw new IllegalStateException("Недостаточно изображений");
        }
        int imageIndex = 0;

        // обычные пары
        for (int i = 0; i < pairCount; i++) {
            cards.add(new Card(images[imageIndex], false, 2));
            cards.add(new Card(images[imageIndex], false, 2));
            imageIndex++;
        }

        // одиночный джокер
        if (hasJoker) {
            cards.add(new Card(R.drawable.joker, true, 1));
        }

        // тройки
        for (int i = 0; i < tripleCount; i++) {
            for (int j = 0; j < 3; j++) {
                cards.add(new Card(images[imageIndex], false, 3));
            }
            imageIndex++;
        }
        Collections.shuffle(cards);
        return cards;
    }

    public int getTotalGroups() {
        return totalGroups;
    }
}