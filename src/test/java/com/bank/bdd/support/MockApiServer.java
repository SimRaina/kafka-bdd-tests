package com.bank.bdd.support;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;

@Component
public class MockApiServer {

    private HttpServer server;
    private int port;

    public synchronized void start() throws IOException {
        if (server != null) {
            return;
        }

        server = HttpServer.create(new InetSocketAddress(0), 0);
        port = server.getAddress().getPort();
        server.createContext("/customer/status", this::handleStatus);
        server.setExecutor(Executors.newSingleThreadExecutor());
        server.start();
        System.out.println("[API-TEST] MockApiServer listening on: " + getEndpointUrl());
    }

    public synchronized void stop() {
        if (server != null) {
            server.stop(0);
            server = null;
            port = 0;
        }
    }

    public String getEndpointUrl() {
        return "http://localhost:" + port + "/customer/status";
    }

    private void handleStatus(HttpExchange exchange) throws IOException {
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(405, -1);
            exchange.close();
            return;
        }

        String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        System.out.println("[API-TEST] MockApiServer received request: " + requestBody);
        String customerId = extractField(requestBody, "customerId");
        String responseBody = "{\"customerId\":\"" + customerId + "\",\"status\":\"ACTIVE\",\"source\":\"MOCK-API\"}";
        System.out.println("[API-TEST] MockApiServer response: " + responseBody);

        exchange.getResponseHeaders().add("Content-Type", "application/json");
        byte[] responseBytes = responseBody.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(200, responseBytes.length);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseBytes);
        }
    }

    private String extractField(String json, String fieldName) {
        String marker = "\"" + fieldName + "\":\"";
        int start = json.indexOf(marker);
        if (start < 0) {
            return "UNKNOWN";
        }

        int valueStart = start + marker.length();
        int valueEnd = json.indexOf('"', valueStart);
        if (valueEnd < 0) {
            return "UNKNOWN";
        }

        return json.substring(valueStart, valueEnd);
    }
}
