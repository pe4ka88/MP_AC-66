package com.example.lab1memorygame;

/**
 * Класс Card - модель данных для игровой карточки
 * Хранит состояние отдельной карточки в игре
 */
public class Card {
    // Уникальный идентификатор карточки (одинаковый для парных/тройных карточек)
    private int id;
    
    // ID ресурса изображения карточки
    private int imageResId;
    
    // Флаг: перевёрнута ли карточка лицом вверх
    private boolean isFaceUp;
    
    // Флаг: найдена ли пара/тройка для этой карточки
    private boolean isMatched;

    /**
     * Конструктор карточки
     * @param id - уникальный идентификатор (одинаковый для совпадающих карточек)
     * @param imageResId - ресурс изображения
     */
    public Card(int id, int imageResId) {
        this.id = id;
        this.imageResId = imageResId;
        this.isFaceUp = false;
        this.isMatched = false;
    }

    // Геттер: возвращает ID карточки
    public int getId() {
        return id;
    }

    // Геттер: возвращает ресурс изображения
    public int getImageResId() {
        return imageResId;
    }

    // Геттер: проверяет, перевёрнута ли карточка
    public boolean isFaceUp() {
        return isFaceUp;
    }

    // Сеттер: устанавливает состояние переворота карточки
    public void setFaceUp(boolean faceUp) {
        isFaceUp = faceUp;
    }

    // Геттер: проверяет, найдена ли пара для карточки
    public boolean isMatched() {
        return isMatched;
    }

    // Сеттер: отмечает карточку как найденную
    public void setMatched(boolean matched) {
        isMatched = matched;
    }
}
