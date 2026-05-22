package com.example.lab1memorygame;

/**
 * Класс GameSettings - хранит настройки игры
 * Управляет размером поля и режимом совпадений (пары/тройки/гибрид)
 */
public class GameSettings {
    // Количество строк в сетке
    private int gridRows;
    
    // Количество столбцов в сетке
    private int gridColumns;
    
    // Общее количество пар/троек карточек
    private int totalPairs;
    
    // Режим игры: "pairs" (пары), "triples" (тройки), "hybrid" (смешанный)
    private String matchMode;
    
    // Количество карточек для совпадения (2 или 3)
    private int cardsToMatch;

    /**
     * Конструктор с настройками по умолчанию
     * Устанавливает поле 4x4 и режим пар
     */
    public GameSettings() {
        this.gridRows = 4;
        this.gridColumns = 4;
        this.totalPairs = 8;
        this.matchMode = "pairs";
        this.cardsToMatch = 2;
    }

    /**
     * Устанавливает размер поля 4x4 (16 карточек)
     */
    public void setGrid4x4() {
        this.gridRows = 4;
        this.gridColumns = 4;
        updateTotalPairs();
    }

    /**
     * Устанавливает размер поля 4x5 (20 карточек)
     */
    public void setGrid4x5() {
        this.gridRows = 5;
        this.gridColumns = 4;
        updateTotalPairs();
    }

    /**
     * Устанавливает размер поля 6x6 (36 карточек)
     */
    public void setGrid6x6() {
        this.gridRows = 6;
        this.gridColumns = 6;
        updateTotalPairs();
    }

    /**
     * Устанавливает режим совпадений
     * @param mode - "pairs" (пары по 2), "triples" (тройки по 3), "hybrid" (микс)
     */
    public void setMatchMode(String mode) {
        this.matchMode = mode;
        if ("triples".equals(mode)) {
            this.cardsToMatch = 3;
        } else if ("hybrid".equals(mode)) {
            this.cardsToMatch = 0; // Определяется динамически
        } else {
            this.cardsToMatch = 2;
        }
        updateTotalPairs();
    }

    /**
     * Пересчитывает количество пар/троек в зависимости от размера поля и режима
     * Для гибридного режима создаёт смесь пар и троек
     */
    private void updateTotalPairs() {
        int totalCells = gridRows * gridColumns;
        
        if ("hybrid".equals(matchMode)) {
            // Гибридный режим: создаем смесь пар и троек
            // Формула: pairsCount * 2 + triplesCount * 3 = totalCells
            
            int triplesCount = 0;
            int pairsCount = 0;
            
            // Подбираем комбинацию троек и пар
            int maxTriples = totalCells / 3;
            
            // Пробуем разные комбинации, начиная с середины
            for (int t = maxTriples / 2; t <= maxTriples; t++) {
                int remaining = totalCells - (t * 3);
                if (remaining >= 0 && remaining % 2 == 0) {
                    triplesCount = t;
                    pairsCount = remaining / 2;
                    break;
                }
            }
            
            // Если не нашли решение, пробуем с меньшим количеством троек
            if (triplesCount == 0 && pairsCount == 0) {
                for (int t = 0; t <= maxTriples; t++) {
                    int remaining = totalCells - (t * 3);
                    if (remaining >= 0 && remaining % 2 == 0) {
                        triplesCount = t;
                        pairsCount = remaining / 2;
                        break;
                    }
                }
            }
            
            this.totalPairs = pairsCount + triplesCount;
        } else if ("triples".equals(matchMode)) {
            // Режим троек: totalCells должно делиться на 3
            this.totalPairs = totalCells / 3;
        } else {
            // Режим пар: totalCells должно делиться на 2
            this.totalPairs = totalCells / 2;
        }
    }

    // Геттер: возвращает количество строк
    public int getGridRows() {
        return gridRows;
    }

    // Геттер: возвращает количество столбцов
    public int getGridColumns() {
        return gridColumns;
    }

    // Геттер: возвращает общее количество пар/троек
    public int getTotalPairs() {
        return totalPairs;
    }

    // Геттер: возвращает режим игры
    public String getMatchMode() {
        return matchMode;
    }

    // Геттер: возвращает количество карточек для совпадения
    public int getCardsToMatch() {
        return cardsToMatch;
    }

    /**
     * Возвращает общее количество карточек на поле
     * @return количество карточек в зависимости от режима
     */
    public int getTotalCards() {
        if ("triples".equals(matchMode)) {
            return totalPairs * 3;
        } else if ("hybrid".equals(matchMode)) {
            return gridRows * gridColumns;
        } else {
            return totalPairs * 2;
        }
    }
}
