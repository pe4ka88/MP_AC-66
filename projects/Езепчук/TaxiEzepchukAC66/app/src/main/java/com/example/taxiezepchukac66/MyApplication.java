package com.example.taxiezepchukac66;

import android.app.Application;
import com.yandex.mapkit.MapKitFactory;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // 1. Установим API ключ
        MapKitFactory.setApiKey("77ddec3f-15fb-4319-8a30-3bceead4280e");

        // 2. Инициализация MapKit
        MapKitFactory.initialize(this);
    }
}
