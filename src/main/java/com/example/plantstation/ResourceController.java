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
        dht11Data d = new dht11Data(10, 20, "2025-02-20<T10:12");
        System.out.println("hier");
        dht11DataList.add(d);
        dht11Data latest = dht11DataList.getLast();
        if (latest != null) {
            return ResponseEntity.ok().body(latest);
        }
        else {
            return ResponseEntity.noContent().build();
        }
    }

    @GetMapping("/plant_station/data")
    public ResponseEntity<?> getItemByName(@RequestParam(value = "name") String name) {
        System.out.println("Getting item by name: " + name);
        ClusterItem item = ItemListRepo.findItemByName(name);
        if (item != null) {
            System.out.println(item.getName());
            return ResponseEntity.ok().body(item);
        }
        else {
            return ResponseEntity.badRequest().body("Item " + name + "not found");
        }
    }

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

    @Scheduled(cron = "0 33 18 * * *")
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
        float meanTemp = sumTemp / dht11DataList.size();
        float meanHum = sumHum / dht11DataList.size();

        Stats Temp = new Stats(minTemp, maxTemp, meanTemp);
        Stats Hum = new Stats(minHum, maxHum, meanHum);
        LocalDate date = LocalDate.now();
        String name = date.toString();

        return new ClusterItem(name, Temp, Hum, date);
    }

}
