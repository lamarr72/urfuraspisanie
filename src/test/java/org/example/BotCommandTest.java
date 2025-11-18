package org.example;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BotCommandTest {

    @Test
    void testBotCommandCreationAndGetters() {
        // Тестируем создание команды и геттеры
        BotCommand command = new BotCommand("/test", "test description", "test response");

        assertEquals("/test", command.getName());
        assertEquals("test description", command.getDescription());
        assertEquals("test response", command.getFullResponse());
    }

    @Test
    void testRealCommandsContent() {
        // Тестируем реальные команды на содержание правильного текста
        BotCommand startCmd = new BotCommand("/start", "начать работу с ботом",
                "Бот выводит расписание занятий по выбранной академической группе\nПоддерживаемые команды можно посмотреть в /help");

        assertTrue(startCmd.getFullResponse().contains("расписание"));
        assertTrue(startCmd.getDescription().contains("начать работу"));

        BotCommand aboutCmd = new BotCommand("/about", "выводит краткую информацию про бота",
                "Бот выводит расписание занятий по выбранной академической группе");

        assertEquals("Бот выводит расписание занятий по выбранной академической группе", aboutCmd.getFullResponse());
    }

    @Test
    void testMultipleCommandsDifferentContent() {
        // Тестируем что разные команды имеют разное содержание
        BotCommand aboutCmd = new BotCommand("/about", "about desc", "about response");
        BotCommand authorsCmd = new BotCommand("/authors", "authors desc", "authors response");

        assertAll("Multiple commands should have different content",
                () -> assertEquals("/about", aboutCmd.getName()),
                () -> assertEquals("/authors", authorsCmd.getName()),
                () -> assertNotEquals(aboutCmd.getDescription(), authorsCmd.getDescription()),
                () -> assertNotEquals(aboutCmd.getFullResponse(), authorsCmd.getFullResponse())
        );
    }

    @Test
    void testCommandWithMultilineResponse() {
        // Тестируем команду с многострочным ответом
        String multilineResponse = """
            Разработкой бота занимаются:
            Ваганов Константин КБ-202
            Устименко Никита КБ-202
            """;

        BotCommand authorsCmd = new BotCommand("/authors", "authors desc", multilineResponse);

        assertTrue(authorsCmd.getFullResponse().contains("Ваганов"));
        assertTrue(authorsCmd.getFullResponse().contains("Устименко"));
        assertTrue(authorsCmd.getFullResponse().contains("КБ-202"));
    }

    @Test
    void testCommandEquality() {
        // Тестируем что команды с одинаковыми данными работают корректно
        BotCommand cmd1 = new BotCommand("/help", "help command", "help response");
        BotCommand cmd2 = new BotCommand("/help", "help command", "help response");

        assertEquals(cmd1.getName(), cmd2.getName());
        assertEquals(cmd1.getDescription(), cmd2.getDescription());
        assertEquals(cmd1.getFullResponse(), cmd2.getFullResponse());
    }

    @Test
    void testCommandWithSpecialCharacters() {
        // Тестируем команды со специальными символами
        BotCommand command = new BotCommand("/command-with-dash", "description with: colon", "response\nwith\nnewlines");

        assertEquals("/command-with-dash", command.getName());
        assertEquals("description with: colon", command.getDescription());
        assertEquals("response\nwith\nnewlines", command.getFullResponse());
    }
}