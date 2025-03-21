package com.example.plantstation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@EnableScheduling
@EnableAsync
@EnableMongoRepositories
@RestController
public class ResourceController {

    @Autowired
    private ItemRepository ItemListRepo;

    private List<dht11Data>  dht11DataList = new ArrayList<>();

    @GetMapping("/plant_station/latest")
    public ResponseEntity<?> getLatestDht11Data() {
        try {
            dht11Data latest = dht11DataList.getLast();
            return ResponseEntity.ok().body(latest);
        }
        catch (NoSuchElementException e) {
            return ResponseEntity.badRequest().body("no entry in dht11 DataList.");
        }

    }

    // get data base entry by name (which currently is currently date as string)
    @GetMapping("/plant_station/data")
    public ResponseEntity<?> getItemByName(@RequestParam(value = "name") String name) {
        System.out.println("Getting item by name: " + name);
        ClusterItem item = ItemListRepo.findItemByName(name);
        if (item != null) {
            System.out.println(item.getName());
            return ResponseEntity.ok().body(item);
        }
        else {
            return ResponseEntity.badRequest().body("Item " + name + " not found");
        }
    }

    // receives a new dht11 data entry from sensor (should be every hour) and stores it locally in list
    @PostMapping("/plant_station")
    public ResponseEntity<?> postItem(@Validated @RequestBody dht11Data newEntry) {
        if (newEntry != null) {
            if (newEntry.getTemperature() == 0.0) {
                return ResponseEntity.badRequest().body("Temperature not found.");
            }
            else if (newEntry.getAirHumidity() == 0.0) {
                return ResponseEntity.badRequest().body("AirHumidity not found.");
            }
            else if (newEntry.getDate() == null) {
                return ResponseEntity.badRequest().body("Date not found.");
            }
            else {
                dht11DataList.add(newEntry);
                return ResponseEntity.ok().body("new Entry successfully received");
            }
        }
        else {
            return ResponseEntity.badRequest().body("no data transferred");
        }
    }

    @DeleteMapping("/plant_station")
    public ResponseEntity<?> deleteItemById(@RequestParam(value = "name") String name) {
        long deleted = ItemListRepo.deleteClusterItemByName(name);
        if (deleted > 0) {
            System.out.println("Item(s) " + name + " successfully deleted. Quantity: " + deleted);
            return ResponseEntity.ok().body("Item(s) " + name + " successfully deleted. Quantity: " + deleted);
        }
        else {
            System.out.println("Item " + name + " not deleted");
            return ResponseEntity.badRequest().body("Item " + name + " not deleted");
        }
    }

    // once per day accumulated data of the day is sent to data base
    @Scheduled(cron = "0 00 18 * * *")
    public void postDailySummary() {
        ClusterItem newEntry = extractSummary();
        ItemListRepo.save(newEntry);
    }

    private ClusterItem extractSummary() {
        float maxTemp = Integer.MIN_VALUE;
        float minTemp = Integer.MAX_VALUE;
        float sumTemp = 0;
        float maxHum = Integer.MIN_VALUE;
        float minHum = Integer.MAX_VALUE;
        float sumHum = 0;
        for (dht11Data el : dht11DataList) {
            sumTemp += el.getTemperature();
            sumHum += el.getAirHumidity();
            if (el.getTemperature() > maxTemp) {
                maxTemp = el.getTemperature();
            }
            if (el.getTemperature() < minTemp) {
                minTemp = el.getTemperature();
            }
            if (el.getAirHumidity() > maxHum) {
                maxHum = el.getAirHumidity();
            }
            if (el.getAirHumidity() < minHum) {
                minHum = el.getAirHumidity();
            }
        }
        //float meanTemp = sumTemp / dht11DataList.size();
        //float meanHum = sumHum / dht11DataList.size();
        float meanTemp = 2.21F;
        float meanHum = 45.11F;


        Stats Temp = new Stats(minTemp, maxTemp, meanTemp);
        Stats Hum = new Stats(minHum, maxHum, meanHum);
        LocalDate date = LocalDate.now();
        String name = date.toString(); // TODO replace with useful alternative

        return new ClusterItem(name, Temp, Hum, date);
    }

}
