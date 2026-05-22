package com.example.lab8;

import android.content.Context;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TestDataGenerator {

    private static final String TAG = "TestDataGenerator";
    private static final int MIN_POINTS_REQUIRED = 30;

    // Координаты Бреста и интересных мест
    private static final double[][] BREST_LOCATIONS = {
            {52.0976, 23.7341}, // Центр города (ул. Советская)
            {52.1119, 23.6847}, // Крепость
            {52.0899, 23.6897}, // Железнодорожный вокзал
            {52.1144, 23.6978}, // Областная больница
            {52.1086, 23.6797}, // Парк 1 Мая
            {52.1203, 23.7403}, // Северный городок
            {52.0936, 23.7461}, // Южный городок
            {52.1003, 23.6914}, // Гостиница "Беларусь"
            {52.1047, 23.7108}, // ТЦ "Корона"
            {52.1064, 23.7211}, // Стадион "Брестский"
            {52.1192, 23.6561}, // Авторынок
            {52.0881, 23.6803}, // ЖД вокзал (новая платформа)
            {52.1167, 23.7592}, // Аэропорт
            {52.0914, 23.7497}, // ТЭЦ
            {52.1136, 23.7264}, // Поликлиника №1
            {52.0992, 23.7042}, // ЦУМ
            {52.1025, 23.7158}, // Драмтеатр
            {52.0989, 23.6997}, // Педагогический университет
            {52.1031, 23.6903}, // Технический университет
            {52.1069, 23.6947}  // Гребной канал
    };

    // Названия мест для подписей
    private static final String[] LOCATION_NAMES = {
            "ул. Советская (центр)",
            "Брестская крепость",
            "Железнодорожный вокзал",
            "Областная больница",
            "Парк 1 Мая",
            "Северный городок",
            "Южный городок",
            "Гостиница Беларусь",
            "ТЦ Корона",
            "Стадион Брестский",
            "Авторынок",
            "ЖД вокзал (новая платформа)",
            "Аэропорт",
            "ТЭЦ",
            "Поликлиника №1",
            "ЦУМ",
            "Драмтеатр",
            "Педагогический университет",
            "Технический университет",
            "Гребной канал"
    };

    private AppDatabase database;
    private ExecutorService executorService;
    private Random random;

    public TestDataGenerator(Context context) {
        database = AppDatabase.getInstance(context);
        executorService = Executors.newSingleThreadExecutor();
        random = new Random();
    }

    /**
     * Генерирует тестовые точки за последние 30 дней
     * @param pointsPerDay количество точек в день (минимум 1 для 30 дней)
     */
    public void generateTestData(int pointsPerDay) {
        executorService.execute(() -> {
            try {
                // Очищаем старые данные (опционально)
                // clearOldData();

                List<LocationPoint> generatedPoints = new ArrayList<>();
                Calendar calendar = Calendar.getInstance();

                // Генерируем точки за последние 30 дней
                for (int day = 0; day < 30; day++) {
                    calendar.setTime(new Date());
                    calendar.add(Calendar.DAY_OF_YEAR, -day);
                    calendar.set(Calendar.HOUR_OF_DAY, 8); // Начинаем с 8 утра
                    calendar.set(Calendar.MINUTE, 0);
                    calendar.set(Calendar.SECOND, 0);

                    // Генерируем несколько точек в день
                    for (int pointNum = 0; pointNum < pointsPerDay; pointNum++) {
                        // Выбираем случайное место из списка
                        int locationIndex = random.nextInt(BREST_LOCATIONS.length);
                        double[] loc = BREST_LOCATIONS[locationIndex];

                        // Добавляем небольшую случайную вариацию координат
                        double lat = loc[0] + (random.nextDouble() - 0.5) * 0.005;
                        double lon = loc[1] + (random.nextDouble() - 0.5) * 0.005;

                        // Смещаем время на несколько часов
                        calendar.add(Calendar.HOUR_OF_DAY, 2 + random.nextInt(4));
                        calendar.add(Calendar.MINUTE, random.nextInt(60));

                        long timestamp = calendar.getTimeInMillis();

                        // Создаем точку
                        LocationPoint point = new LocationPoint(
                                lat,
                                lon,
                                5 + random.nextFloat() * 10, // accuracy 5-15м
                                140 + random.nextDouble() * 20, // altitude 140-160м
                                0.5f + random.nextFloat() * 3, // speed 0.5-3.5 м/с
                                timestamp
                        );

                        // Устанавливаем адрес (для информации)
                        String address = LOCATION_NAMES[locationIndex] +
                                " (день " + (30 - day) + ")";

                        // Сохраняем в базу
                        database.locationDao().insert(point);
                        generatedPoints.add(point);

                        Log.d(TAG, "Сгенерирована точка: " + address +
                                " в " + new SimpleDateFormat("dd.MM HH:mm", Locale.getDefault())
                                .format(new Date(timestamp)));
                    }
                }

                Log.d(TAG, "✅ Сгенерировано " + generatedPoints.size() + " тестовых точек");

            } catch (Exception e) {
                Log.e(TAG, "Ошибка генерации данных: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    /**
     * Генерирует реалистичный маршрут по городу
     */
    public void generateRealisticRoute() {
        executorService.execute(() -> {
            try {
                Calendar calendar = Calendar.getInstance();

                // Маршрут на сегодня (утро -> день -> вечер)
                int[][] route = {
                        {0, 8},   // 8:00 - Центр
                        {1, 9},   // 9:00 - Крепость
                        {4, 11},  // 11:00 - Парк
                        {7, 13},  // 13:00 - Гостиница
                        {8, 15},  // 15:00 - ТЦ Корона
                        {9, 17},  // 17:00 - Стадион
                        {3, 19},  // 19:00 - Больница
                        {2, 21}   // 21:00 - Вокзал
                };

                for (int[] point : route) {
                    int locIndex = point[0];
                    int hour = point[1];

                    calendar.setTime(new Date());
                    calendar.set(Calendar.HOUR_OF_DAY, hour);
                    calendar.set(Calendar.MINUTE, random.nextInt(60));
                    calendar.set(Calendar.SECOND, 0);

                    double[] loc = BREST_LOCATIONS[locIndex];

                    LocationPoint routePoint = new LocationPoint(
                            loc[0] + (random.nextDouble() - 0.5) * 0.002,
                            loc[1] + (random.nextDouble() - 0.5) * 0.002,
                            8 + random.nextFloat() * 5,
                            145 + random.nextDouble() * 10,
                            1.2f + random.nextFloat() * 2,
                            calendar.getTimeInMillis()
                    );

                    database.locationDao().insert(routePoint);

                    Log.d(TAG, "Маршрутная точка: " + LOCATION_NAMES[locIndex] +
                            " в " + hour + ":00");
                }

            } catch (Exception e) {
                Log.e(TAG, "Ошибка генерации маршрута: " + e.getMessage());
            }
        });
    }

    /**
     * Очищает старые тестовые данные (если нужно начать заново)
     */
    public void clearOldData() {
        executorService.execute(() -> {
            try {
                // Удаляем все точки (осторожно!)
                // Для реального использования лучше удалять только тестовые
                List<LocationPoint> allPoints = database.locationDao().getAllLocations();
                for (LocationPoint point : allPoints) {
                    database.locationDao().delete(point);
                }
                Log.d(TAG, "🗑️ Все старые данные удалены");
            } catch (Exception e) {
                Log.e(TAG, "Ошибка очистки: " + e.getMessage());
            }
        });
    }

    /**
     * Удаляет N самых новых точек (по timestamp DESC)
     */
    public void deleteLatestPoints(int pointsToDelete) {
        if (pointsToDelete <= 0) {
            return;
        }

        executorService.execute(() -> {
            try {
                List<LocationPoint> allPoints = database.locationDao().getAllLocations();
                int deleteCount = Math.min(pointsToDelete, allPoints.size());

                for (int i = 0; i < deleteCount; i++) {
                    database.locationDao().delete(allPoints.get(i));
                }

                Log.d(TAG, "🧹 Удалено последних точек: " + deleteCount);
            } catch (Exception e) {
                Log.e(TAG, "Ошибка удаления последних точек: " + e.getMessage());
            }
        });
    }

    /**
     * Оставляет в базе не более targetCount точек (сохраняет самые новые)
     */
    public void trimToTargetCount(int targetCount) {
        if (targetCount < 0) {
            return;
        }

        executorService.execute(() -> {
            try {
                List<LocationPoint> allPoints = database.locationDao().getAllLocations();
                int currentCount = allPoints.size();

                if (currentCount <= targetCount) {
                    Log.d(TAG, "Точек и так достаточно мало: " + currentCount);
                    return;
                }

                int deleteCount = currentCount - targetCount;

                // getAllLocations() возвращает точки по timestamp DESC,
                // поэтому удаляем с конца списка (самые старые точки).
                for (int i = currentCount - 1; i >= targetCount; i--) {
                    database.locationDao().delete(allPoints.get(i));
                }

                Log.d(TAG, "🧹 Удалено старых точек: " + deleteCount + ", осталось: " + targetCount);
            } catch (Exception e) {
                Log.e(TAG, "Ошибка trimToTargetCount: " + e.getMessage());
            }
        });
    }

    /**
     * Проверяет количество точек и предлагает сгенерировать, если меньше 30
     */
    public void checkAndGenerateIfNeeded(Context context) {
        executorService.execute(() -> {
            int count = database.locationDao().getCountSince(0);

            if (count < MIN_POINTS_REQUIRED) {
                Log.d(TAG, "⚠️ Недостаточно точек. Текущее: " + count +
                        ", нужно: " + MIN_POINTS_REQUIRED);

                // Генерируем недостающие точки
                int pointsNeeded = MIN_POINTS_REQUIRED - count + 5; // +5 для запаса
                generateTestData(2); // Генерируем по 2 точки в день

                // Добавляем один маршрут
                generateRealisticRoute();

                Log.d(TAG, "✅ Сгенерировано дополнительных точек");
            } else {
                Log.d(TAG, "✅ Достаточно точек: " + count);
            }
        });
    }
}