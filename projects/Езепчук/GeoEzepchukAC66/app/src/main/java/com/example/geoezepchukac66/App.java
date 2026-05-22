package com.example.geoezepchukac66;

import android.app.Application;
import com.yandex.mapkit.MapKitFactory;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        MapKitFactory.setApiKey("77ddec3f-15fb-4319-8a30-3bceead4280e");
        MapKitFactory.initialize(this);
    }
}