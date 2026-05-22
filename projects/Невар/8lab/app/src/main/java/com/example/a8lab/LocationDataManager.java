package com.example.a8lab;

import android.content.Context;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class LocationDataManager {
    private static final String FILE_NAME = "location_data.json";

    public static void saveLocations(Context context, List<LocationPoint> locations) {
        Gson gson = new Gson();
        String json = gson.toJson(locations);
        try (FileOutputStream fos = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE)) {
            fos.write(json.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<LocationPoint> loadLocations(Context context) {
        Gson gson = new Gson();
        File file = new File(context.getFilesDir(), FILE_NAME);
        if (!file.exists()) {
            return generateDefaultLocations();
        }
        try (FileInputStream fis = context.openFileInput(FILE_NAME)) {
            byte[] buffer = new byte[fis.available()];
            fis.read(buffer);
            String json = new String(buffer);
            Type listType = new TypeToken<ArrayList<LocationPoint>>(){}.getType();
            List<LocationPoint> loaded = gson.fromJson(json, listType);
            if (loaded == null || loaded.isEmpty()) {
                return generateDefaultLocations();
            }
            return loaded;
        } catch (IOException e) {
            e.printStackTrace();
            return generateDefaultLocations();
        }
    }

    private static List<LocationPoint> generateDefaultLocations() {
        List<LocationPoint> locations = new ArrayList<>();

        locations.add(new LocationPoint(52.0937, 23.6850, "Парк культуры и отдыха", "Парк", 1700000000000L));
        locations.add(new LocationPoint(52.0951, 23.6932, "Продуктовый магазин Копеечка", "Магазин", 1700086400000L));
        locations.add(new LocationPoint(52.0968, 23.7015, "Парк Воинов-Интернационалистов", "Парк", 1700172800000L));
        locations.add(new LocationPoint(52.0912, 23.7110, "Супермаркет Евроопт", "Магазин", 1700259200000L));
        locations.add(new LocationPoint(52.0985, 23.6887, "Сквер на ул. Советской", "Парк", 1700345600000L));
        locations.add(new LocationPoint(52.0943, 23.7078, "Продуктовый магазин Родны кут", "Магазин", 1700432000000L));
        locations.add(new LocationPoint(52.0972, 23.7156, "Сквер Машерова", "Парк", 1700518400000L));
        locations.add(new LocationPoint(52.0925, 23.7220, "Магазин Соседи", "Магазин", 1700604800000L));
        locations.add(new LocationPoint(52.1001, 23.6980, "Парк 1000-летия", "Парк", 1700691200000L));
        locations.add(new LocationPoint(52.0889, 23.7045, "Продуктовый магазин Грошык", "Магазин", 1700777600000L));
        locations.add(new LocationPoint(52.1023, 23.7123, "Сквер на ул. Гоголя", "Парк", 1700864000000L));
        locations.add(new LocationPoint(52.0905, 23.7189, "Супермаркет Алми", "Магазин", 1700950400000L));
        locations.add(new LocationPoint(52.1037, 23.6902, "Парк Дружбы", "Парк", 1701036800000L));
        locations.add(new LocationPoint(52.0958, 23.7260, "Магазин Виталюр", "Магазин", 1701123200000L));
        locations.add(new LocationPoint(52.1050, 23.7050, "Сквер на бульваре Космонавтов", "Парк", 1701209600000L));
        locations.add(new LocationPoint(52.0890, 23.6960, "Продуктовый магазин Белмаркет", "Магазин", 1701296000000L));
        locations.add(new LocationPoint(52.1015, 23.7200, "Парк Победы", "Парк", 1701382400000L));
        locations.add(new LocationPoint(52.0875, 23.7100, "Магазин Санта", "Магазин", 1701468800000L));
        locations.add(new LocationPoint(52.1065, 23.6985, "Сквер на ул. Московской", "Парк", 1701555200000L));
        locations.add(new LocationPoint(52.0930, 23.7320, "Продуктовый магазин Хит", "Магазин", 1701641600000L));
        locations.add(new LocationPoint(52.1080, 23.7128, "Парк Строителей", "Парк", 1701728000000L));
        locations.add(new LocationPoint(52.0860, 23.7005, "Супермаркет Квартал", "Магазин", 1701814400000L));
        locations.add(new LocationPoint(52.1045, 23.7250, "Сквер Защитников Отечества", "Парк", 1701900800000L));
        locations.add(new LocationPoint(52.0918, 23.7380, "Магазин Продтовары", "Магазин", 1701987200000L));
        locations.add(new LocationPoint(52.1100, 23.7060, "Парк Железнодорожников", "Парк", 1702073600000L));
        locations.add(new LocationPoint(52.0880, 23.7205, "Продуктовый магазин Дионис", "Магазин", 1702160000000L));
        locations.add(new LocationPoint(52.1075, 23.7160, "Сквер Энергетиков", "Парк", 1702246400000L));
        locations.add(new LocationPoint(52.0855, 23.7085, "Магазин Универсам", "Магазин", 1702332800000L));
        locations.add(new LocationPoint(52.1115, 23.7010, "Парк Заводской", "Парк", 1702419200000L));
        locations.add(new LocationPoint(52.1090, 23.7220, "Сквер Молодежный", "Парк", 1702505600000L));
        locations.add(new LocationPoint(52.0920, 23.7440, "Продуктовый магазин Лакомка", "Магазин", 1702592000000L));
        locations.add(new LocationPoint(52.1130, 23.7095, "Парк Южный", "Парк", 1702678400000L));

        return locations;
    }
}