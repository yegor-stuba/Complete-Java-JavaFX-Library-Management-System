package com.studyshare.client.service;

import com.studyshare.client.config.ClientConfig;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class ConnectionMonitor {
    private final String serverUrl = ClientConfig.BASE_URL + "/api/health";
    private final HttpClient httpClient;
    private final BooleanProperty connected = new SimpleBooleanProperty(false);
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public ConnectionMonitor() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
        startMonitoring();
    }

    private void startMonitoring() {
        scheduler.scheduleAtFixedRate(this::checkConnection, 0, 10, TimeUnit.SECONDS);
    }

    private void checkConnection() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(serverUrl))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            boolean isConnected = response.statusCode() == 200;
            connected.set(isConnected);
            log.info("Server connection status: {}", isConnected);
        } catch (Exception e) {
            connected.set(false);
            log.error("Connection error: ", e);
        }
    }

    public BooleanProperty connectedProperty() {
        return connected;
    }

    public boolean isConnected() {
        return connected.get();
    }

    public void shutdown() {
        scheduler.shutdown();
    }
}


