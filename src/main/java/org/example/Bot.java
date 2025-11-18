package org.example;

import com.fasterxml.jackson.databind.JsonNode;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.HashMap;
import java.util.Map;

public class Bot extends TelegramLongPollingBot {
    String TOKEN = "";
    private boolean tokenReceived = false;

    private final Map<String, BotCommand> commands = new HashMap<>();
    private final NumberGroupParser groupParser;

    public static void main(String[] args) {
        try {
            Bot bot = new Bot();
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(bot);
            System.out.println("Bot successfully started!");
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public Bot() {
        this.groupParser = new NumberGroupParser();
        initializeCommands();
    }

    private void initializeCommands() {
        registerCommand(new BotCommand(
                "/start",
                "начать работу с ботом",
                """
                        Бот выводит расписание занятий по выбранной академической группе
                        Поддерживаемые команды можно посмотреть в /help
                        """
        ));

        registerCommand(new BotCommand(
                "/about",
                "выводит краткую информацию про бота",
                "Бот выводит расписание занятий по выбранной академической группе"
        ));

        registerCommand(new BotCommand(
                "/authors",
                "выводит информацию об авторах",
                """
                        Разработкой бота занимаются:
                        Ваганов Константин КБ-202
                        Устименко Никита КБ-202
                        """
        ));

        registerCommand(new BotCommand(
                "/help",
                "выводит информацию обо всех доступных командах",
                generateHelpResponse()
        ));

        registerCommand(new BotCommand(
                "/schedule",
                "получить расписание группы по её названию",
                """
                        Использование: /schedule <номер группы>
                        Пример: /schedule МЕН-241001
                        
                        Эта команда получает расписание группы из системы УрФУ
                        """
        ));

    }

    private void registerCommand(BotCommand command) {
        commands.put(command.getName(), command);
    }

    String generateHelpResponse() {
        StringBuilder helpText = new StringBuilder("Информация о командах:\n");

        for (BotCommand command : commands.values()) {
            helpText.append("• ")
                    .append(command.getName())
                    .append(" - ")
                    .append(command.getDescription())
                    .append("\n");
        }
        helpText.append("\nДля получения подробной информации о конкретной команде используйте: /help <command>");

        return helpText.toString();
    }

    String generateCommandHelp(String commandName) {
        BotCommand command = commands.get(commandName);
        if  (command != null) {
            return String.format("Команда: %s\n\nОписание: %s\n\nОтвет:\n%s",
            command.getName(), command.getDescription(), command.getFullResponse());
        } else {
            return "Команда '" + commandName + "' не найдена. Используйте /help для просмотра всех команд.";
        }
    }

    private String handleScheduleCommand(String groupNumber) {
        if (groupNumber == null || groupNumber.trim().isEmpty()) {
            return "Пожалуйста, укажите номер группы после команды.\nПример: /schedule МЕН-241001";
        }

        try {
            int groupId = groupParser.getGroupId(groupNumber.trim());

            if (groupId != -1) {
                ScheduleParser scheduleParser = new ScheduleParser();
                JsonNode scheduleJson = scheduleParser.parseGroupSchedule(groupId);

                ScheduleFormatter formattedParser = new ScheduleFormatter(scheduleJson);
                String formattedSchedule = formattedParser.getFormattedSchedule();

                System.out.println(scheduleJson);
                System.out.println(formattedSchedule);

                return formattedSchedule;
            } else {
                return String.format(
                        "Группа '%s' не найдена.\nПроверьте правильность написания номера группы.",
                        groupNumber.trim()
                );
            }
        } catch (Exception e) {
            return String.format(
                    "Ошибка при получении данных для группы '%s': %s",
                    groupNumber.trim(), e.getMessage()
            );
        }
    }

    @Override
    public String getBotUsername() {
        return "raspisanieurfubot";
    }
    @Override
    public String getBotToken() {
        if (!tokenReceived) {
            TOKEN = TokenReader.readToken();
            tokenReceived = true;
        }
        return TOKEN;
    }
    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            long chatId = update.getMessage().getChatId();
            String receviedMessage = update.getMessage().getText();
            String messageText = "";

            if (receviedMessage.startsWith("/help")) {
                String[] parts = receviedMessage.split(" ", 2);
                if (parts.length > 1) {
                    String commandArg = parts[1].trim();
                    if (!commandArg.startsWith("/")) {
                        commandArg = "/" + commandArg;
                    }
                    messageText = generateCommandHelp(commandArg);
                } else {
                    messageText = generateHelpResponse();
                }
            }
            else if (receviedMessage.startsWith("/schedule")) {
                String[] parts = receviedMessage.split(" ", 2);
                if (parts.length > 1) {
                    String groupNumber = parts[1].trim();
                    messageText = handleScheduleCommand(groupNumber);
                } else {
                    messageText = commands.get("/schedule").getFullResponse();
                }
            }
            else if (commands.containsKey(receviedMessage)) {
                messageText = commands.get(receviedMessage).getFullResponse();
            } else {
                messageText = "Команда не найдена. Используйте /help для просмотра доступных команд.";
            }

            SendMessage sendMessage = SendMessage
                    .builder()
                    .chatId(chatId)
                    .text(messageText)
                    .build();
            try {
                executeAsync(sendMessage);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }
}