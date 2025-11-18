package org.example;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class ScheduleParser {

    private static final String BASE_SCHEDULE_URL = "https://urfu.ru/api/v2/schedule/groups/%d/schedule?date_gte=%s&date_lte=%s";
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public ScheduleParser() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Получает расписание для группы по ID и датам
     * @param groupId ID группы (например, 63847)
     * @param startDate начальная дата в формате yyyy-MM-dd
     * @param endDate конечная дата в формате yyyy-MM-dd
     * @return JSON строка с расписанием
     */
    public String getGroupSchedule(int groupId, String startDate, String endDate) throws IOException, InterruptedException {
        String url = String.format(BASE_SCHEDULE_URL, groupId, startDate, endDate);

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

    /**
     * Получает расписание для текущей недели
     * @param groupId ID группы
     * @return JSON строка с расписанием
     */
    public String getCurrentWeekSchedule(int groupId) throws IOException, InterruptedException {
        String[] dates = GetDate.getCurrentWeekDates();
        return getGroupSchedule(groupId, dates[0], dates[1]);
    }

    /**
     * Парсит JSON расписания и возвращает в виде JsonNode
     * @param groupId ID группы
     * @param startDate начальная дата
     * @param endDate конечная дата
     * @return JsonNode с расписанием
     */
    public JsonNode parseGroupSchedule(int groupId) throws IOException, InterruptedException {
        //String[] dates = GetDate.getCurrentWeekDates();
        String[] dates = GetDate.getTodayDates();
        //String json = getGroupSchedule(groupId, dates[0], dates[1]);
        String json = getGroupSchedule(groupId, dates[0], dates[0]);
        return objectMapper.readTree(json);
    }
    /*
    public JsonNode parseGroupSchedule(int groupId, String startDate, String endDate) throws IOException, InterruptedException {
        String json = getGroupSchedule(groupId, startDate, endDate);
        return objectMapper.readTree(json);
    }



    public static void main(String[] args) {
        ScheduleParser2 scheduleParser = new ScheduleParser2();
        NumberGroupParser groupParser = new NumberGroupParser();

        try {
            // Получаем ID группы
            String groupName = "МЕН-243201";
            int groupId = groupParser.getGroupId(groupName);
            System.out.println("ID группы " + groupName + ": " + groupId);

            if (groupId != -1) {
                // Получаем даты для текущей недели
                String[] dates = GetDate.getCurrentWeekDates();
                System.out.println("Период: " + dates[0] + " - " + dates[1]);

                // Получаем расписание
                String scheduleJson = scheduleParser.getGroupSchedule(groupId, dates[0], dates[1]);
                System.out.println("Полученное расписание:");
                System.out.println(scheduleJson);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    */
}
