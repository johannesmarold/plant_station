package com.example.plantstation;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Locale;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class SensorDataWebSocketHandler extends TextWebSocketHandler {

    private final Random random = new Random();
    private Timer timer;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        startSensorUpdates(session);
    }

    private void startSensorUpdates(WebSocketSession session) {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    // TODO update to use latest stored Data of server or data base
                    double temperature = 20 + random.nextDouble() * 10;
                    double humidity = 40 + random.nextDouble() * 20;
                    String jsonData = String.format(Locale.US, "{\"temperature\": %.2f, \"humidity\": %.2f}", temperature, humidity);
                    System.out.println(jsonData);
                    session.sendMessage(new TextMessage(jsonData));
                } catch (IOException e) {
                    e.printStackTrace();
                    stopSensorUpdates();
                }
            }
        }, 0, 5000); // Update every 5 seconds
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, org.springframework.web.socket.CloseStatus status) {
        stopSensorUpdates();
    }

    private void stopSensorUpdates() {
        if (timer != null) {
            timer.cancel();
        }
    }
}
