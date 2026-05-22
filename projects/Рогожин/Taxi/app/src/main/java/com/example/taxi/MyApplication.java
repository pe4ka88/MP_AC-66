package com.example.taxi;

import android.app.Application;
import com.yandex.mapkit.MapKitFactory;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // 1. Установим API ключ
        MapKitFactory.setApiKey("d6b24d4f-f294-4af1-a452-c8f09fbaffdd");

        // 2. Инициализация MapKit
        MapKitFactory.initialize(this);
    }
}
