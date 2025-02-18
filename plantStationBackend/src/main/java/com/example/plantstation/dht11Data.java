package com.example.plantstation;

import java.time.LocalDateTime;

public class dht11Data {

    private float temperature;
    private float humidity;
    private LocalDateTime date;

    public dht11Data(float temperature, float humidity, String date) {
        this.temperature = temperature;
        this.humidity = humidity;
        this.date = LocalDateTime.parse(date);
    }

    public float getTemperature() {
        return temperature;
    }

    public float getAirHumidity() {
        return humidity;
    }

    public LocalDateTime getDate() {
        return date;
    }
}
