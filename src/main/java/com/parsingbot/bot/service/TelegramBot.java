package com.parsingbot.bot.service;

import com.parsingbot.bot.config.BotConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

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
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            log.info(String.format("Received message %s from user %s %s %s",
                                    messageText,
                                    update.getMessage().getChat().getFirstName(),
                                    update.getMessage().getChat().getLastName(),
                                    update.getMessage().getChat().getUserName()));
            long chatId = update.getMessage().getChatId();
            sendMessageTest(chatId, "U said " + messageText);
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
