package org.example;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class NumberGroupParser {

    private static final String BASE_URL = "https://urfu.ru/api/v2/schedule/groups?search=";
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public NumberGroupParser() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Получает JSON с информацией о группе по её номеру
     * @param groupNumber номер группы (например, "МЕН-243201")
     * @return JSON строка с данными о группе
     * @throws IOException если произошла ошибка при выполнении запроса
     * @throws InterruptedException если запрос был прерван
     */
    public String getGroupJson(String groupNumber) throws IOException, InterruptedException {
        String url = BASE_URL + groupNumber;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Accept", "application/json")
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return response.body();
        } else {
            throw new IOException("HTTP ошибка: " + response.statusCode() + " - " + response.body());
        }
    }

    public int getGroupId(String groupNumber) throws IOException, InterruptedException {
        String receivedJson = getGroupJson(groupNumber);
        JsonNode jsonArray = objectMapper.readTree(receivedJson);

        if (jsonArray.isArray() && jsonArray.size() > 0) {
            JsonNode jsonNode = jsonArray.get(0);
            return jsonNode.get("id").asInt();
        }
        return -1;
    }
}