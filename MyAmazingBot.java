import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.Objects;

public class MyAmazingBot implements LongPollingSingleThreadUpdateConsumer {
    private final TelegramClient telegramClient;

    public MyAmazingBot(String botToken) {
        telegramClient = new OkHttpTelegramClient(botToken);
    }

    @Override
    public void consume(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            long chat_id = update.getMessage().getChatId();

            String receviedMessage = update.getMessage().getText();
            Boolean correctCommand = false;
            String message_text = "";
            if (Objects.equals(receviedMessage, "/start")) {
                message_text = """
                Бот выводит расписание занятий по выбранной академической группе
                Поддерживаемые команды можно посмотреть в /help
                """;
                correctCommand = true;
            } else if (Objects.equals(receviedMessage, "/about")) {
                message_text = "Бот выводит расписание занятий по выбранной академической группе";
                correctCommand = true;
            } else if (Objects.equals(receviedMessage, "/help")) {
                message_text = """
                Информация о командах
                - /about - выводит краткую информацию про бота
                - /authors - выводит информацию об авторах
                - /help - выводит информацию обо всех доступных командах (можно запускать с аргументом: /help <command>
                """;
                correctCommand = true;
            } else if (Objects.equals(receviedMessage, "/authors")) {
                message_text = """
                Разработкой бота занимаются:
                Ваганов Константин КБ-202
                Устименко Никита КБ-202
                """;
                correctCommand = true;
            }

            SendMessage message = SendMessage
                    .builder()
                    .chatId(chat_id)
                    .text(message_text)
                    .build();
            if (correctCommand) {
                try {
                    telegramClient.execute(message);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}