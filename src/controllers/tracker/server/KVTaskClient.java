package controllers.tracker.server;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;


public class KVTaskClient {
    private HttpClient httpClient;
    private String apiToken;
    private String url;
    public KVTaskClient(String url) {
        httpClient = HttpClient.newHttpClient();
        this.url = url;
        URI address = URI.create(url + "/register");
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(address)
                .GET()
                .build();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                apiToken = response.body();
            } else {
                System.out.println("Что-то пошло не так. Сервер вернул код состояния: " + response.statusCode());
            }
        } catch (IOException | InterruptedException e) {
            System.out.println("Во время выполнения запроса возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
        }
    }

    public void put(String key, String json){
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(URI.create(url+"/save/"+key+"?"+"API_TOKEN="+apiToken))
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        try {
            HttpResponse<String> response = httpClient.send(request,
                    HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        }
        catch (IOException | InterruptedException e) {
            System.out.println("Во время выполнения запроса возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
        }
    }

    public String load(String key){
        String condition = "";
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(URI.create(url+"/load/"+key+"?"+"API_TOKEN="+apiToken))
                .GET()
                .build();
        try {
            HttpResponse <String> response = httpClient.send(request,HttpResponse.BodyHandlers.
                    ofString(StandardCharsets.UTF_8));
            if (response.statusCode() == 200) {
                condition = response.body();
            } else {
                System.out.println("Что-то пошло не так. Сервер вернул код состояния: " + response.statusCode());
            }
        }
        catch (IOException | InterruptedException e) {
            System.out.println("Во время выполнения запроса возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
        }
        return condition;
    }

}
