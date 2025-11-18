package org.example;

import com.fasterxml.jackson.databind.JsonNode;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class ScheduleEvent {
    private String id;
    private int eventId;
    private String title;
    private String loadType;
    private List<String> loadKeys;
    private LocalDate date;
    private LocalTime timeBegin;
    private LocalTime timeEnd;
    private int pairNumber;
    private String auditoryTitle;
    private String auditoryLocation;
    private String teacherAuditoryTitle;
    private String teacherAuditoryLocation;
    private String comment;
    private String teacherName;
    private String teacherComment;
    private String teacherLink;

    // Конструктор из JsonNode
    public ScheduleEvent(JsonNode eventNode) {
        this.id = getStringValue(eventNode, "id");
        this.eventId = getIntValue(eventNode, "eventId");
        this.title = getStringValue(eventNode, "title");
        this.loadType = getStringValue(eventNode, "loadType");
        this.loadKeys = parseLoadKeys(eventNode.get("loadKeys"));
        this.date = parseDate(getStringValue(eventNode, "date"));
        this.timeBegin = parseTime(getStringValue(eventNode, "timeBegin"));
        this.timeEnd = parseTime(getStringValue(eventNode, "timeEnd"));
        this.pairNumber = getIntValue(eventNode, "pairNumber");
        this.auditoryTitle = getStringValue(eventNode, "auditoryTitle");
        this.auditoryLocation = getStringValue(eventNode, "auditoryLocation");
        this.teacherAuditoryTitle = getStringValue(eventNode, "teacherAuditoryTitle");
        this.teacherAuditoryLocation = getStringValue(eventNode, "teacherAuditoryLocation");
        this.comment = getStringValue(eventNode, "comment");
        this.teacherName = getStringValue(eventNode, "teacherName");
        this.teacherComment = getStringValue(eventNode, "teacherComment");
        this.teacherLink = getStringValue(eventNode, "teacherLink");
    }

    private String getStringValue(JsonNode node, String fieldName) {
        JsonNode fieldNode = node.get(fieldName);
        return (fieldNode != null && !fieldNode.isNull()) ? fieldNode.asText() : null;
    }

    private int getIntValue(JsonNode node, String fieldName) {
        JsonNode fieldNode = node.get(fieldName);
        return (fieldNode != null && !fieldNode.isNull()) ? fieldNode.asInt() : 0;
    }

    private List<String> parseLoadKeys(JsonNode loadKeysNode) {
        List<String> keys = new ArrayList<>();
        if (loadKeysNode != null && loadKeysNode.isArray()) {
            for (JsonNode keyNode : loadKeysNode) {
                keys.add(keyNode.asText());
            }
        }
        return keys;
    }

    private LocalDate parseDate(String dateStr) {
        return (dateStr != null && !dateStr.isEmpty()) ? LocalDate.parse(dateStr) : null;
    }

    private LocalTime parseTime(String timeStr) {
        return (timeStr != null && !timeStr.isEmpty() && !timeStr.equals("00:00:00"))
                ? LocalTime.parse(timeStr)
                : null;
    }

    // Геттеры
    public String getId() { return id; }
    public int getEventId() { return eventId; }
    public String getTitle() { return title; }
    public String getLoadType() { return loadType; }
    public List<String> getLoadKeys() { return loadKeys; }
    public LocalDate getDate() { return date; }
    public LocalTime getTimeBegin() { return timeBegin; }
    public LocalTime getTimeEnd() { return timeEnd; }
    public int getPairNumber() { return pairNumber; }
    public String getAuditoryTitle() { return auditoryTitle; }
    public String getAuditoryLocation() { return auditoryLocation; }
    public String getTeacherAuditoryTitle() { return teacherAuditoryTitle; }
    public String getTeacherAuditoryLocation() { return teacherAuditoryLocation; }
    public String getComment() { return comment; }
    public String getTeacherName() { return teacherName; }
    public String getTeacherComment() { return teacherComment; }
    public String getTeacherLink() { return teacherLink; }

    @Override
    public String toString() {
        return String.format(
                "Пара %d: %s (%s)\n" +
                        "Время: %s - %s\n" +
                        "Аудитория: %s%s\n" +
                        "Преподаватель: %s\n" +
                        "Тип: %s\n" +
                        "Примечание: %s",
                pairNumber, title, date,
                timeBegin != null ? timeBegin : "не указано",
                timeEnd != null ? timeEnd : "не указано",
                auditoryTitle != null ? auditoryTitle : "не указана",
                auditoryLocation != null ? " (" + auditoryLocation + ")" : "",
                teacherName != null ? teacherName : "не указан",
                loadType,
                comment != null ? comment : "нет"
        );
    }
}