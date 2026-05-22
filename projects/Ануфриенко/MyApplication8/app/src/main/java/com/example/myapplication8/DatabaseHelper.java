package com.example.myapplication8;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Вспомогательный класс для работы с SQLite.
 * Хранит историю геолокаций пользователя.
 *
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  ТЕСТОВЫЙ РЕЖИМ                                              ║
 * ║  Вызовите insertTestData(context) один раз, чтобы заполнить ║
 * ║  базу 35 заранее заданными точками без реального выхода      ║
 * ║  на улицу. Метод помечен @SuppressWarnings для защиты от    ║
 * ║  случайного вызова в Production-коде.                        ║
 * ╚══════════════════════════════════════════════════════════════╝
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper";

    // ── Схема БД ───────────────────────────────────────────────────────
    private static final String DB_NAME    = "locations.db";
    private static final int    DB_VERSION = 1;

    public static final String TABLE      = "location_history";
    public static final String COL_ID        = "_id";
    public static final String COL_LAT       = "latitude";
    public static final String COL_LON       = "longitude";
    public static final String COL_TIME      = "timestamp";
    public static final String COL_PLACE     = "place_name";
    public static final String COL_ACCURACY  = "accuracy";

    private static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE + " (" +
                    COL_ID       + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_LAT      + " REAL    NOT NULL, " +
                    COL_LON      + " REAL    NOT NULL, " +
                    COL_TIME     + " INTEGER NOT NULL, " +
                    COL_PLACE    + " TEXT, " +
                    COL_ACCURACY + " REAL DEFAULT 0" +
                    ");";

    // ── Singleton ──────────────────────────────────────────────────────
    private static DatabaseHelper instance;

    public static synchronized DatabaseHelper getInstance(Context ctx) {
        if (instance == null) {
            instance = new DatabaseHelper(ctx.getApplicationContext());
        }
        return instance;
    }

    private DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    // ── Lifecycle ──────────────────────────────────────────────────────

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
        Log.d(TAG, "Database created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE);
        onCreate(db);
    }

    // ── CRUD ───────────────────────────────────────────────────────────

    /** Добавить одну запись. Возвращает rowId или -1 при ошибке. */
    public long insertLocation(LocationEntry entry) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = toContentValues(entry);
        long id = db.insert(TABLE, null, cv);
        Log.d(TAG, "Inserted location id=" + id);
        return id;
    }

    /** Все записи, отсортированные по времени (возрастание). */
    public List<LocationEntry> getAllLocations() {
        return query(null, null, COL_TIME + " ASC");
    }

    /** Записи за указанный период (Unix ms). */
    public List<LocationEntry> getLocationsBetween(long fromMs, long toMs) {
        return query(
                COL_TIME + " >= ? AND " + COL_TIME + " <= ?",
                new String[]{String.valueOf(fromMs), String.valueOf(toMs)},
                COL_TIME + " ASC"
        );
    }

    /** Последние N записей (для маршрута сегодня). */
    public List<LocationEntry> getLastN(int n) {
        SQLiteDatabase db = getReadableDatabase();
        List<LocationEntry> list = new ArrayList<>();
        Cursor c = db.query(TABLE, null, null, null, null, null,
                COL_TIME + " DESC", String.valueOf(n));
        if (c != null) {
            while (c.moveToNext()) list.add(fromCursor(c));
            c.close();
        }
        // Возвращаем в хронологическом порядке
        java.util.Collections.reverse(list);
        return list;
    }

    /** Количество записей в БД. */
    public int getCount() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT COUNT(*) FROM " + TABLE, null);
        int count = 0;
        if (c != null) {
            if (c.moveToFirst()) count = c.getInt(0);
            c.close();
        }
        return count;
    }

    /** Удалить все записи. */
    public void clearAll() {
        getWritableDatabase().delete(TABLE, null, null);
    }

    // ── Helpers ────────────────────────────────────────────────────────

    private List<LocationEntry> query(String selection, String[] selArgs, String orderBy) {
        SQLiteDatabase db = getReadableDatabase();
        List<LocationEntry> list = new ArrayList<>();
        Cursor c = db.query(TABLE, null, selection, selArgs, null, null, orderBy);
        if (c != null) {
            while (c.moveToNext()) list.add(fromCursor(c));
            c.close();
        }
        return list;
    }

    private ContentValues toContentValues(LocationEntry e) {
        ContentValues cv = new ContentValues();
        cv.put(COL_LAT,      e.getLatitude());
        cv.put(COL_LON,      e.getLongitude());
        cv.put(COL_TIME,     e.getTimestamp());
        cv.put(COL_PLACE,    e.getPlaceName());
        cv.put(COL_ACCURACY, e.getAccuracy());
        return cv;
    }

    private LocationEntry fromCursor(Cursor c) {
        LocationEntry e = new LocationEntry();
        e.setId(       c.getLong  (c.getColumnIndexOrThrow(COL_ID)));
        e.setLatitude( c.getDouble(c.getColumnIndexOrThrow(COL_LAT)));
        e.setLongitude(c.getDouble(c.getColumnIndexOrThrow(COL_LON)));
        e.setTimestamp(c.getLong  (c.getColumnIndexOrThrow(COL_TIME)));
        e.setPlaceName(c.getString(c.getColumnIndexOrThrow(COL_PLACE)));
        e.setAccuracy( c.getFloat (c.getColumnIndexOrThrow(COL_ACCURACY)));
        return e;
    }
    public List<LocationEntry> getRouteForLatestDay() {

        List<LocationEntry> all = getAllLocations();

        if (all.isEmpty()) return new ArrayList<>();

        // Последняя запись
        LocationEntry latest = all.get(all.size() - 1);

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(latest.getTimestamp());

        // Начало дня
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        long dayStart = cal.getTimeInMillis();

        // Конец дня
        cal.add(Calendar.DAY_OF_MONTH, 1);
        long dayEnd = cal.getTimeInMillis() - 1;

        // Все точки за этот день
        List<LocationEntry> dayEntries =
                getLocationsBetween(dayStart, dayEnd);

        // Если только 1 точка — добавляем предыдущую
        if (dayEntries.size() == 1 && all.size() >= 2) {

            LocationEntry previous =
                    all.get(all.size() - 2);

            dayEntries.add(0, previous);
        }

        // Ограничение максимум 10
        if (dayEntries.size() > 10) {

            dayEntries =
                    dayEntries.subList(
                            dayEntries.size() - 10,
                            dayEntries.size());
        }

        return dayEntries;
    }

    // ══════════════════════════════════════════════════════════════════
    //  ██████╗ ███████╗███████╗████████╗    ██████╗  █████╗ ████████╗ █████╗
    //  ██╔══██╗██╔════╝██╔════╝╚══██╔══╝    ██╔══██╗██╔══██╗╚══██╔══╝██╔══██╗
    //  ██████╔╝█████╗  ███████╗   ██║       ██║  ██║███████║   ██║   ███████║
    //  ██╔══██╗██╔══╝  ╚════██║   ██║       ██║  ██║██╔══██║   ██║   ██╔══██║
    //  ██║  ██║███████╗███████║   ██║       ██████╔╝██║  ██║   ██║   ██║  ██║
    //  ╚═╝  ╚═╝╚══════╝╚══════╝   ╚═╝       ╚═════╝ ╚═╝  ╚═╝   ╚═╝   ╚═╝  ╚═╝
    //
    //  Метод для заполнения базы тестовыми данными БЕЗ реального GPS.
    //  Вызвать ОДИН РАЗ из MainActivity (см. комментарий там).
    //  35 точек за последние 7 дней — Брест, центральные районы.
    // ══════════════════════════════════════════════════════════════════

    @SuppressWarnings("unused") // Намеренно: вызывается вручную при тестировании
    public static void insertTestData(Context context) {
        DatabaseHelper db = getInstance(context);

        // Если данные уже есть — не дублируем
        if (db.getCount() >= 30) {
            Log.i(TAG, "Test data already present, skipping.");
            return;
        }

        Log.i(TAG, "Inserting 35 test location points...");

        // Базовое время — 7 дней назад
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 8);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.add(Calendar.DAY_OF_YEAR, -7);

        // ┌─────────────────────────────────────────────────────────────────┐
        // │  [lat, lon, deltaDays, deltaHours, "Название места"]            │
        // └─────────────────────────────────────────────────────────────────┘
        Object[][] points = {

                // День 1 — Центр Бреста
                {52.0976, 23.7341, 0, 0, "Дом"},
                {52.0988, 23.7365, 0, 1, "Магазин"},
                {52.1002, 23.7390, 0, 2, "Автобусная остановка"},
                {52.0970, 23.7285, 0, 3, "Площадь Ленина"},
                {52.0961, 23.7250, 0, 5, "Улица Советская"},

                // День 2 — Брестская крепость
                {52.0976, 23.7341, 1, 0, "Дом"},
                {52.0845, 23.6562, 1, 2, "Брестская крепость"},
                {52.0830, 23.6520, 1, 3, "Холмские ворота"},
                {52.0860, 23.6610, 1, 5, "Музей обороны"},
                {52.0976, 23.7341, 1, 8, "Дом"},

                // День 3 — Центр и вокзал
                {52.0976, 23.7341, 2, 1, "Дом"},
                {52.0930, 23.7240, 2, 2, "ЖД вокзал"},
                {52.0955, 23.7275, 2, 3, "ТЦ Дидас Персия"},
                {52.0985, 23.7310, 2, 4, "Кафе в центре"},
                {52.1000, 23.7355, 2, 6, "Набережная"},
                {52.0976, 23.7341, 2, 9, "Дом"},

                // День 4 — Восток Бреста
                {52.0976, 23.7341, 3, 0, "Дом"},
                {52.1080, 23.7600, 3, 1, "Восток район"},
                {52.1120, 23.7700, 3, 2, "Школа"},
                {52.1150, 23.7750, 3, 3, "Парк Восток"},
                {52.1090, 23.7650, 3, 5, "Торговый центр"},
                {52.0976, 23.7341, 3, 8, "Дом"},

                // День 5 — Южный район
                {52.0976, 23.7341, 4, 0, "Дом"},
                {52.0880, 23.7420, 4, 1, "Южный рынок"},
                {52.0845, 23.7480, 4, 2, "Поликлиника"},
                {52.0820, 23.7520, 4, 3, "Сквер"},
                {52.0870, 23.7450, 4, 5, "Кафе Южный"},
                {52.0976, 23.7341, 4, 7, "Дом"},

                // День 6 — Граевка
                {52.0976, 23.7341, 5, 1, "Дом"},
                {52.1040, 23.7000, 5, 2, "Граевка"},
                {52.1060, 23.6950, 5, 3, "Стадион"},
                {52.1085, 23.6900, 5, 4, "Магазин"},
                {52.1050, 23.6980, 5, 6, "Парк Граевка"},

                // День 7 — вчера
                {52.0976, 23.7341, 7, 1, "Дом"},
                {52.0996, 23.7401, 7, 2, "Магазин"},
                {52.1015, 23.7405, 7, 3, "Работа"},
                {52.0976, 23.7341, 7, 4, "Дом"},
        };

        long baseTime = cal.getTimeInMillis();
        long ONE_HOUR = 3600_000L;
        long ONE_DAY  = 86400_000L;

        for (Object[] p : points) {
            double lat    = (double) p[0];
            double lon    = (double) p[1];
            int    day    = (int)    p[2];
            int    hour   = (int)    p[3];
            String place  = (String) p[4];

            long ts = baseTime + day * ONE_DAY + hour * ONE_HOUR;

            // Добавляем небольшой случайный "шум" к координатам (±50 м)
            double noise = 0.0004;
            lat += (Math.random() - 0.5) * noise;
            lon += (Math.random() - 0.5) * noise;

            LocationEntry entry = new LocationEntry(lat, lon, ts, place, 10.0f);
            db.insertLocation(entry);
        }

        Log.i(TAG, "Test data inserted: " + db.getCount() + " records total.");
    }
}