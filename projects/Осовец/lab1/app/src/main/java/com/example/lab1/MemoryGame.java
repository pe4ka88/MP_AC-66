package com.example.lab1;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MemoryGame {
    private List<Card> cards;
    private List<Card> revealedCards;
    private int pairsFound;
    private int totalGroups;
    private int moves;
    private int matchCount;
    private boolean mixedMode;

    public MemoryGame(int[] imageResources, int matchCount) {
        this.matchCount = matchCount;
        this.mixedMode = false;
        initializeCards(imageResources);
        this.revealedCards = new ArrayList<>();
        this.pairsFound = 0;
        this.totalGroups = imageResources.length;
        this.moves = 0;
    }

    public MemoryGame(int[] imageResources, int matchCount, boolean mixedMode) {
        this.matchCount = matchCount;
        this.mixedMode = mixedMode;
        if (mixedMode) {
            initializeMixedCards(imageResources);
        } else {
            initializeCards(imageResources);
        }
        this.revealedCards = new ArrayList<>();
        this.pairsFound = 0;
        this.moves = 0;
    }

    private void initializeCards(int[] imageResources) {
        cards = new ArrayList<>();
        
        // Создаем группы карт (пары или тройки)
        for (int i = 0; i < imageResources.length; i++) {
            for (int j = 0; j < matchCount; j++) {
                cards.add(new Card(imageResources[i], imageResources[i], matchCount));
            }
        }
        
        totalGroups = imageResources.length;
        
        // Перемешиваем карты
        Collections.shuffle(cards);
    }

    private void initializeMixedCards(int[] imageResources) {
        cards = new ArrayList<>();
        
        // В смешанном режиме чередуем пары и тройки
        // Для поля с N картами нужно подобрать сочетание пар и троек
        int totalCards = 0;
        int pairsCount = 0;
        int tripletsCount = 0;
        
        // Вычисляем количество пар и троек для заданного количества изображений
        // Общее количество карт = pairsCount * 2 + tripletsCount * 3
        // Стараемся сделать примерно поровну
        int imageCount = imageResources.length;
        
        // Половина изображений для пар, половина для троек
        pairsCount = imageCount / 2;
        tripletsCount = imageCount - pairsCount;
        
        // Если тройки не делятся ровно, корректируем
        totalCards = pairsCount * 2 + tripletsCount * 3;
        
        int imageIndex = 0;
        
        // Создаем пары
        for (int i = 0; i < pairsCount && imageIndex < imageResources.length; i++) {
            int imageRes = imageResources[imageIndex++];
            for (int j = 0; j < 2; j++) {
                cards.add(new Card(imageRes, imageRes, 2));
            }
        }
        
        // Создаем тройки
        for (int i = 0; i < tripletsCount && imageIndex < imageResources.length; i++) {
            int imageRes = imageResources[imageIndex++];
            for (int j = 0; j < 3; j++) {
                cards.add(new Card(imageRes, imageRes, 3));
            }
        }
        
        totalGroups = pairsCount + tripletsCount;
        
        // Перемешиваем карты
        Collections.shuffle(cards);
    }

    public void addRevealedCard(Card card) {
        revealedCards.add(card);
    }
    
    public List<Card> getRevealedCards() {
        return revealedCards;
    }
    
    public void clearRevealedCards() {
        revealedCards.clear();
    }
    
    public int getMatchCount() {
        return matchCount;
    }

    public boolean isMixedMode() {
        return mixedMode;
    }

    // Получить требуемое количество карт для совпадения (зависит от первой открытой карты в смешанном режиме)
    public int getCurrentRequiredMatchCount() {
        if (mixedMode && !revealedCards.isEmpty()) {
            return revealedCards.get(0).getGroupSize();
        }
        return matchCount;
    }

    public void setFirstCard(Card card) {
        revealedCards.clear();
        revealedCards.add(card);
    }
    
    public void clearFirstCard() {
        revealedCards.clear();
    }
    
    public void incrementMoves() {
        this.moves++;
    }
    
    public void incrementPairsFound() {
        this.pairsFound++;
    }

    public List<Card> getCards() {
        return cards;
    }

    public boolean isGameWon() {
        return pairsFound == totalGroups;
    }

    public int getMoves() {
        return moves;
    }

    public int getPairsFound() {
        return pairsFound;
    }

    public int getTotalPairs() {
        return totalGroups;
    }

    public void reset(int[] imageResources) {
        initializeCards(imageResources);
        revealedCards.clear();
        pairsFound = 0;
        moves = 0;
        totalGroups = imageResources.length;
    }

    public Card getPreviousCard() {
        if (revealedCards.isEmpty()) {
            return null;
        }
        return revealedCards.get(0);
    }
    
    public int getRevealedCount() {
        return revealedCards.size();
    }
    
    public boolean checkMatch() {
        int requiredCount = getCurrentRequiredMatchCount();
        if (revealedCards.size() < requiredCount) {
            return false;
        }
        
        int firstId = revealedCards.get(0).getId();
        for (Card card : revealedCards) {
            if (card.getId() != firstId) {
                return false;
            }
        }
        return true;
    }
}
