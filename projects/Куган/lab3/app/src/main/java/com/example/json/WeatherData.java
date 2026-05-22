package com.example.json;

public class WeatherData {
    private double temperature;
    private int weatherCode;
    private double windSpeed;
    private int humidity;
    private String weatherDescription;

    public WeatherData(double temperature, int weatherCode, double windSpeed, int humidity) {
        this.temperature = temperature;
        this.weatherCode = weatherCode;
        this.windSpeed = windSpeed;
        this.humidity = humidity;
        this.weatherDescription = getWeatherDescription(weatherCode);
    }

    public double getTemperature() { return temperature; }
    public int getWeatherCode() { return weatherCode; }
    public double getWindSpeed() { return windSpeed; }
    public int getHumidity() { return humidity; }
    public String getWeatherDescription() { return weatherDescription; }

    private String getWeatherDescription(int code) {
        switch (code) {
            case 0: return "☀️ Ясно";
            case 1: return "🌤 Преимущественно ясно";
            case 2: return "⛅ Переменная облачность";
            case 3: return "☁️ Пасмурно";
            case 45: case 48: return "🌫 Туман";
            case 51: case 53: case 55: return "🌧 Морось";
            case 61: case 63: case 65: return "🌧 Дождь";
            case 71: case 73: case 75: return "🌨 Снегопад";
            case 80: case 81: case 82: return "🌦 Ливень";
            case 95: return "⛈ Гроза";
            case 96: case 99: return "⛈ Гроза с градом";
            default: return "🌡 " + code;
        }
    }

    public String getWeatherIcon() {
        switch (weatherCode) {
            case 0: return "☀️";
            case 1: return "🌤";
            case 2: return "⛅";
            case 3: return "☁️";
            case 45: case 48: return "🌫";
            case 51: case 53: case 55: return "🌧";
            case 61: case 63: case 65: return "🌧";
            case 71: case 73: case 75: return "🌨";
            case 80: case 81: case 82: return "🌦";
            case 95: return "⛈";
            case 96: case 99: return "⛈";
            default: return "🌡";
        }
    }
}