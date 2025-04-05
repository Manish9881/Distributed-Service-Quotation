package service.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import service.core.ClientInfo;
import service.core.Quotation;
import service.core.Application;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.concurrent.TimeUnit;

public class Client {

    private static final String BROKER_URL = "http://localhost:8083/applications";
    private static final OkHttpClient client = new OkHttpClient.Builder()
            .readTimeout(1000, TimeUnit.MILLISECONDS)
            .writeTimeout(1000, TimeUnit.MILLISECONDS)
            .build();
    private static final ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) {
        for (ClientInfo info : clients) {
            displayProfile(info);
            Application application = sendRequest(info);

            if (application != null) {
                for (Quotation quotation : application.quotations) {
                    displayQuotation(quotation);
                }
            }

            System.out.println("\n");
        }

        // Properly shut down the OkHttp client to avoid lingering threads
        shutdownClient();
    }

    private static Application sendRequest(ClientInfo info) {
        try {
            // Convert ClientInfo to JSON
            String jsonRequest = mapper.writeValueAsString(info);

            // Build the request body
            RequestBody body = RequestBody.create(jsonRequest, MediaType.parse("application/json"));

            // Create HTTP POST request
            Request request = new Request.Builder()
                    .url(BROKER_URL)
                    .post(body)
                    .build();

            // Execute the request synchronously
            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    System.out.println("Request failed: " + response);
                    return null;
                }

                // Parse response body to Application object
                String jsonResponse = response.body().string();
                return mapper.readValue(jsonResponse, Application.class);
            }

        } catch (JsonProcessingException e) {
            System.err.println("Error serializing ClientInfo: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Error executing request: " + e.getMessage());
        }
        return null;
    }

    private static void shutdownClient() {
        client.connectionPool().evictAll(); // Clear connection pool
        client.dispatcher().executorService().shutdown(); // Shutdown executor service
        //does not work
    }

    public static void displayProfile(ClientInfo info) {
        System.out.println("|=================================================================================================================|");
        System.out.println("|                                     |                                     |                                     |");
        System.out.println(
                "| Name: " + String.format("%1$-29s", info.name) +
                        " | Gender: " + String.format("%1$-27s", (info.gender == ClientInfo.MALE ? "Male" : "Female")) +
                        " | Age: " + String.format("%1$-30s", info.age) + " |");
        System.out.println(
                "| Weight/Height: " + String.format("%1$-20s", info.weight + "kg/" + info.height + "m") +
                        " | Smoker: " + String.format("%1$-27s", info.smoker ? "YES" : "NO") +
                        " | Medical Problems: " + String.format("%1$-17s", info.medicalIssues ? "YES" : "NO") + " |");
        System.out.println("|                                     |                                     |                                     |");
        System.out.println("|=================================================================================================================|");
    }

    public static void displayQuotation(Quotation quotation) {
        System.out.println(
                "| Company: " + String.format("%1$-26s", quotation.company) +
                        " | Reference: " + String.format("%1$-24s", quotation.reference) +
                        " | Price: " + String.format("%1$-28s", NumberFormat.getCurrencyInstance().format(quotation.price)) + " |");
        System.out.println("|=================================================================================================================|");
    }

    // Sample client data
    public static final ClientInfo[] clients = {
            new ClientInfo("Niki Collier", ClientInfo.FEMALE, 49, 1.5494, 80, false, false),
            new ClientInfo("Old Geeza", ClientInfo.MALE, 65, 1.6, 100, true, true),
            new ClientInfo("Hannah Montana", ClientInfo.FEMALE, 21, 1.78, 65, false, false),
            new ClientInfo("Rem Collier", ClientInfo.MALE, 49, 1.8, 120, false, true),
            new ClientInfo("Jim Quinn", ClientInfo.MALE, 55, 1.9, 75, true, false),
            new ClientInfo("Donald Duck", ClientInfo.MALE, 35, 0.45, 1.6, false, false)
    };
}
