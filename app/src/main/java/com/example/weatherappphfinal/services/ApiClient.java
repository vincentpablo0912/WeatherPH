package com.example.weatherappphfinal.services;

import org.json.JSONObject;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class ApiClient {

    public static JSONObject get(String urlString) {
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(urlString).openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            if (conn.getResponseCode() != 200) {
                conn.disconnect();
                return null;
            }

            StringBuilder resultJson = new StringBuilder();
            Scanner scanner = new Scanner(conn.getInputStream());
            while (scanner.hasNext()) {
                resultJson.append(scanner.nextLine());
            }
            scanner.close();
            conn.disconnect();

            return new JSONObject(resultJson.toString());

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
