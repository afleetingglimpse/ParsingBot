package com.parsingbot.service.bot;

import com.parsingbot.config.BotConfig;
import com.parsingbot.service.parser.Parser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.logging.Logger;

@Component
public class TelegramBot extends TelegramLongPollingBot {

    private final BotConfig config;

    private static final Logger LOGGER = Logger.getLogger(TelegramBot.class.getName());

    @Autowired
    private CommandHandler commandHandler;
    @Autowired
    private MessageHandler messageHandler;

    public TelegramBot(BotConfig config) {
        this.config = config;
    }

    @Override
    public void onUpdateReceived(Update update) {
        commandHandler.handleCommand(update, this);
    }

    void sendMessage(long chatId, String message) {
        messageHandler.sendMessage(this, chatId, message);
    }

    public BotConfig getConfig() {
        return config;
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
