package org.example;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.ArrayList;
import java.util.List;

public class ScheduleFormatter {
    private final List<ScheduleEvent> events;
    private final JsonNode groupInfo;

    public ScheduleFormatter(JsonNode scheduleJson) {
        this.groupInfo = scheduleJson.get("group");
        this.events = parseEvents(scheduleJson.get("events"));
    }

    private List<ScheduleEvent> parseEvents(JsonNode eventsNode) {
        List<ScheduleEvent> eventList = new ArrayList<>();

        if (eventsNode != null && eventsNode.isArray()) {
            for (JsonNode eventNode : eventsNode) {
                eventList.add(new ScheduleEvent(eventNode));
            }
        }

        return eventList;
    }

    // Получить все события
    public List<ScheduleEvent> getEvents() {
        return new ArrayList<>(events);
    }

    // Получить события по конкретной дате
    public List<ScheduleEvent> getEventsByDate(String date) {
        return events.stream()
                .filter(event -> event.getDate() != null && event.getDate().toString().equals(date))
                .toList();
    }

    // Получить события по типу занятия
    public List<ScheduleEvent> getEventsByType(String loadType) {
        return events.stream()
                .filter(event -> loadType.equals(event.getLoadType()))
                .toList();
    }

    // Получить информацию о группе
    public String getGroupInfo() {
        if (groupInfo != null) {
            return String.format("%s (ID: %d, Курс: %d)",
                    groupInfo.get("title").asText(),
                    groupInfo.get("id").asInt(),
                    groupInfo.get("course").asInt());
        }
        return "Информация о группе не найдена";
    }

    // Получить расписание в читаемом формате
    public String getFormattedSchedule() {
        StringBuilder sb = new StringBuilder();
        sb.append(getGroupInfo()).append("\n\n");

        if (events.isEmpty()) {
            sb.append("На выбранный период занятий нет");
        } else {
            // Группируем по датам
            events.stream()
                    .collect(java.util.stream.Collectors.groupingBy(ScheduleEvent::getDate))
                    .entrySet().stream()
                    .sorted(java.util.Map.Entry.comparingByKey())
                    .forEach(entry -> {
                        sb.append("📅 ").append(entry.getKey()).append(":\n");
                        entry.getValue().stream()
                                .sorted((e1, e2) -> Integer.compare(e1.getPairNumber(), e2.getPairNumber()))
                                .forEach(event -> sb.append("  • ").append(event.toString().replace("\n", "\n    ")).append("\n\n"));
                    });
        }

        return sb.toString();
    }
}