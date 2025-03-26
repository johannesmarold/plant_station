package com.example.plantstation;

import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDate;

@Document(collection = "data")
public class ClusterItem {

    /*
    @Id
    private String id;
    */

    private String name;

    private Stats temperature;

    private Stats airHumidity;

    private LocalDate date;

    public ClusterItem (String name, Stats temperature, Stats airHumidity, LocalDate date) {
        super();
        //this.id = id;
        this.name = name;
        this.temperature = temperature;
        this.airHumidity = airHumidity;
        this.date = date;
    }

    public String getName() {
        return this.name;
    }

    public Stats getTemperature() {
        return this.temperature;
    }

    public Stats getAirHumidity() {
        return this.airHumidity;
    }

    public LocalDate getDate() {
        return this.date;
    }

}
