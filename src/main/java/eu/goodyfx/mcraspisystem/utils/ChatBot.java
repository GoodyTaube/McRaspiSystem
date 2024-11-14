package eu.goodyfx.mcraspisystem.utils;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ChatBot {
    private final String apiUrl = "http://localhost:8008/chat/b868199b-0ba8-4d18-9291-77b7d2cbb95a";  // Passe dies an den API-Endpunkt an

    // Methode, um eine Anfrage an die Serge-API zu senden
    public String querySerge(String userInput) throws IOException {
        // Erstelle eine HTTP-Verbindung zur Serge-API
        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        // Baue das JSON-Datenpaket
        JSONObject json = new JSONObject();
        json.put("query", userInput);

        // Sende die Anfrage
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = json.toString().getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        // Antwort lesen
        try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            return response.toString();
        }
    }
}
