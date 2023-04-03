package com.parsingbot.bot.service;

import com.parsingbot.bot.config.BotConfig;
import lombok.extern.slf4j.Slf4j;
import org.parser.Parser;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.io.IOException;
import java.util.Map;


@Slf4j // logging
@Component
public class TelegramBot extends TelegramLongPollingBot {

    private final BotConfig config;

    public TelegramBot(BotConfig config) {
        this.config = config;
    }


    /**
     *
     * @param update объект из пакета org.telegram.telegrambots.meta.api.objects. Попадает в метод при получении
     * сообщения от юзера в телеге.
     */
    @Override
    public void onUpdateReceived(Update update) {
        handleCommand(update);
    }

    private void handleCommand(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            log.info(String.format("Received message %s from user %s %s %s",
                    messageText,
                    update.getMessage().getChat().getFirstName(),
                    update.getMessage().getChat().getLastName(),
                    update.getMessage().getChat().getUserName()));
            long chatId = update.getMessage().getChatId();

            switch (messageText) {
                case "/start": // убрать константы во внешний файл
                    handleStartCommand(update);
                    sendMessageTest(chatId, "No respond to /start command yet");
                    break;

                case "/hh":
                    handleHHcommand(update);

            }
        }
    }

    // Сделать норм обработку команды
    private void handleStartCommand(Update update) {
    }

    // Сделать норм обработку команды
    private void handleHHcommand(Update update) {
        long chatId = update.getMessage().getChatId();
        try {
            Parser parser = new Parser();
            Map<String, Map<String, String>> result = parser.parse();
            parser.saveResult(result, new File("D:/Study/Programming/Java/ParsingBot/target/temp.txt"));

            for (String vacancy : result.keySet()) {
                //sendMessageTest(chatId, vacancy);
                Map<String, String> subMap = result.get(vacancy);
                for (String attr : subMap.keySet()) {
                    //sendMessageTest(chatId, attr);
                    if (attr.equals("link"))
                        sendMessageTest(chatId, subMap.get(attr));
                }
            }
        } catch (IOException e) {
            log.error("Failed to initialise parser. Process aborted");
        }
    }


    @Deprecated
    private void sendMessageTest(long chatId, String message) {
        SendMessage msg = new SendMessage(String.valueOf(chatId), message);
        try {
            execute(msg);
            log.info(String.format("Message %s sent to user", msg.getText()));
        }
        catch (TelegramApiException e) {
            log.error("Error sending message" + e.getMessage());
        }
    }


    @Override
    public String getBotUsername() {
        return config.getBotName();
    }


    @Override
    public String getBotToken() {
        return config.getToken();
    }
}
