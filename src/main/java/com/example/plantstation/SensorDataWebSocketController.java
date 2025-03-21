package com.example.plantstation;

import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.mongodb.client.MongoChangeStreamCursor;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.model.changestream.ChangeStreamDocument;

import java.util.Locale;

@Controller
public class SensorDataWebSocketController {

    private final MongoTemplate mongoTemplate;
    private final SimpMessagingTemplate messagingTemplate;  // ✅ STOMP message template

    public SensorDataWebSocketController(MongoTemplate mongoTemplate, SimpMessagingTemplate messagingTemplate) {
        this.mongoTemplate = mongoTemplate;
        this.messagingTemplate = messagingTemplate;
        startListeningForChanges(); // ✅ Listen for real-time updates
    }

    @MessageMapping("/latestData")  // ✅ When frontend requests latest data
    @SendTo("/topic/sensorData")
    public String sendLatestData() {
        System.out.println("send latest data on request");
        MongoDatabase database = mongoTemplate.getDb();
        MongoCollection<Document> collection = database.getCollection("data");

        // ✅ Get the most recent sensor data
        Document latestData = collection.find()
                .sort(Sorts.descending("date"))
                .first();

        if (latestData != null) {
            return formatJson(latestData);
        }
        return "{}";  // If no data found, send empty JSON
    }

    private void startListeningForChanges() {
        new Thread(() -> {
            MongoDatabase database = mongoTemplate.getDb();
            MongoCollection<Document> collection = database.getCollection("data");

            try (MongoChangeStreamCursor<ChangeStreamDocument<Document>> cursor = (MongoChangeStreamCursor<ChangeStreamDocument<Document>>) collection.watch().iterator()) {
                while (cursor.hasNext()) {
                    ChangeStreamDocument<Document> change = cursor.next();
                    Document updatedDocument = change.getFullDocument();
                    if (updatedDocument != null) {
                        System.out.println("new Data incoming");
                        messagingTemplate.convertAndSend("/topic/sensorData", formatJson(updatedDocument));
                    }
                }
            }
        }).start();
        System.out.println("✅ Listening for MongoDB updates...");
    }

    private String formatJson(Document document) {
        Document tempAll = (Document) document.get("temperature");
        double temp = tempAll.getDouble("mean");
        Document airHAll = (Document) document.get("airHumidity");
        double airH = airHAll.getDouble("mean");
        return String.format(
                Locale.US, "{\"temperature\": %.2f, \"humidity\": %.2f}",
                temp,
                airH
        );
    }
}
