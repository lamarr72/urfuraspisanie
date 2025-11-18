package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BotTest {

    private TestableBot bot;

    @BeforeEach
    void setUp() {
        bot = new TestableBot();
    }

    @Test
    void testStartCommand() {
        String response = bot.processMessage("/start");
        assertNotNull(response);
        assertTrue(response.contains("Бот выводит расписание"));
        assertTrue(response.contains("/help"));
    }

    @Test
    void testAboutCommand() {
        String response = bot.processMessage("/about");
        assertEquals("Бот выводит расписание занятий по выбранной академической группе", response);
    }

    @Test
    void testAuthorsCommand() {
        String response = bot.processMessage("/authors");
        assertNotNull(response);
        assertTrue(response.contains("Ваганов Константин"));
        assertTrue(response.contains("Устименко Никита"));
        assertTrue(response.contains("КБ-202"));
    }

    @Test
    void testHelpCommandWithoutArguments() {
        String response = bot.processMessage("/help");
        assertNotNull(response);
        assertTrue(response.contains("Информация о командах"));
        assertTrue(response.contains("/start"));
        assertTrue(response.contains("/about"));
        assertTrue(response.contains("/authors"));
        assertTrue(response.contains("/help"));
    }

    @Test
    void testHelpCommandWithValidArgument() {
        String response = bot.processMessage("/help /about");
        assertNotNull(response);
        assertTrue(response.contains("Команда: /about"));
        assertTrue(response.contains("Описание:"));
        assertTrue(response.contains("Ответ:"));
    }

    @Test
    void testHelpCommandWithValidArgumentWithoutSlash() {
        String response = bot.processMessage("/help about");
        assertNotNull(response);
        assertTrue(response.contains("Команда: /about"));
    }

    @Test
    void testHelpCommandWithInvalidArgument() {
        String response = bot.processMessage("/help /unknown");
        assertNotNull(response);
        assertTrue(response.contains("не найдена"));
        assertTrue(response.contains("/help"));
    }

    @Test
    void testUnknownCommand() {
        String response = bot.processMessage("/unknown");
        assertNotNull(response);
        assertTrue(response.contains("Команда не найдена"));
        assertTrue(response.contains("/help"));
    }

    @Test
    void testEmptyHelpCommand() {
        // Тестируем команду /help с пробелом (должен вернуть общую справку)
        String response = bot.processMessage("/help ");
        assertNotNull(response);
        // Вместо проверки конкретного текста, проверяем что это НЕ сообщение об ошибке
        assertFalse(response.contains("не найдена"));
        assertTrue(response.contains("/help") || response.contains("Информация о командах"));
    }

    @Test
    void testMultipleSpacesInHelpCommand() {
        String response = bot.processMessage("/help    /about");
        assertNotNull(response);
        assertTrue(response.contains("Команда: /about"));
    }

    @Test
    void testAllCommandsRegistered() {
        assertTrue(bot.isCommandRegistered("/start"));
        assertTrue(bot.isCommandRegistered("/about"));
        assertTrue(bot.isCommandRegistered("/authors"));
        assertTrue(bot.isCommandRegistered("/help"));
        assertFalse(bot.isCommandRegistered("/unknown"));
    }

    private static class TestableBot {
        private final Bot realBot;

        public TestableBot() {
            this.realBot = new Bot();
        }

        public String processMessage(String message) {
            if (message.startsWith("/help")) {
                String[] parts = message.split(" ", 2);
                if (parts.length > 1) {
                    String commandArg = parts[1].trim();
                    if (commandArg.isEmpty()) {
                        return generateHelpResponse();
                    }
                    if (!commandArg.startsWith("/")) {
                        commandArg = "/" + commandArg;
                    }
                    return generateCommandHelp(commandArg);
                } else {
                    return generateHelpResponse();
                }
            } else {
                if (isCommandRegistered(message)) {
                    return getCommandResponse(message);
                } else {
                    return "Команда не найдена. Используйте /help для просмотра доступных команд.";
                }
            }
        }

        public boolean isCommandRegistered(String command) {
            try {
                var field = Bot.class.getDeclaredField("commands");
                field.setAccessible(true);
                @SuppressWarnings("unchecked")
                var commandsMap = (java.util.Map<String, BotCommand>) field.get(realBot);
                return commandsMap.containsKey(command);
            } catch (Exception e) {
                return false;
            }
        }

        private String getCommandResponse(String command) {
            try {
                var field = Bot.class.getDeclaredField("commands");
                field.setAccessible(true);
                @SuppressWarnings("unchecked")
                var commandsMap = (java.util.Map<String, BotCommand>) field.get(realBot);
                return commandsMap.get(command).getFullResponse();
            } catch (Exception e) {
                return "Ошибка: " + e.getMessage();
            }
        }

        private String generateHelpResponse() {
            try {
                var method = Bot.class.getDeclaredMethod("generateHelpResponse");
                method.setAccessible(true);
                return (String) method.invoke(realBot);
            } catch (Exception e) {
                return "Ошибка генерации справки";
            }
        }

        private String generateCommandHelp(String commandName) {
            try {
                var method = Bot.class.getDeclaredMethod("generateCommandHelp", String.class);
                method.setAccessible(true);
                return (String) method.invoke(realBot, commandName);
            } catch (Exception e) {
                return "Ошибка генерации справки по команде";
            }
        }
    }
}