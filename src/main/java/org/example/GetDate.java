package org.example;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class GetDate {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * Получает даты для текущей недели (с понедельника по воскресенье)
     * @return массив с двумя датами: начало недели и конец недели
     */
    public static String[] getCurrentWeekDates() {
        LocalDate today = LocalDate.now();
        LocalDate monday = today.with(java.time.DayOfWeek.MONDAY);
        LocalDate sunday = today.with(java.time.DayOfWeek.SUNDAY);

        return new String[]{
                monday.format(DATE_FORMATTER),
                sunday.format(DATE_FORMATTER)
        };
    }

    public static String[] getTodayDates() {
        LocalDate today = LocalDate.now();

        return new String[]{
                today.format(DATE_FORMATTER),
        };
    }
    /*
    public static void main(String[] args) {
        // Пример использования
        String[] currentWeek = getCurrentWeekDates();
        System.out.println("Текущая неделя: " + currentWeek[0] + " - " + currentWeek[1]);
    }
     */
}