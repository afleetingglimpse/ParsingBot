package com.parsingbot.service.bot;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Arrays;
import java.util.logging.Logger;
@Component
public class MessageHandler {
    private static final Logger LOGGER = Logger.getLogger(MessageHandler.class.getName());

    void sendMessage(TelegramBot bot, long chatId, String message) {
        SendMessage msg = new SendMessage(String.valueOf(chatId), message);
        try {
            bot.execute(msg);
            LOGGER.info(("Message \"%s\" sent to user".formatted(msg.getText())));
        }
        catch (TelegramApiException e) {
            LOGGER.warning("Error sending message \"%s\"".formatted(e.getMessage()));
            LOGGER.warning(Arrays.toString(e.getStackTrace()));
        }
    }
}
