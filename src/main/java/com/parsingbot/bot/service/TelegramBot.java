package com.parsingbot.bot.service;

import com.parsingbot.bot.config.BotConfig;
import com.parsingbot.bot.entities.Vacancy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
@Slf4j // logging
@Component
public class TelegramBot extends TelegramLongPollingBot {

    private final BotConfig config;

    private URL url;

    @Autowired
    public void setUrl(BotConfig config) throws MalformedURLException {
        this.url = new URL(config.getDbServerUrl());
    }

    public TelegramBot(BotConfig config) {
        this.config = config;
    }

    @Override
    public void onUpdateReceived(Update update) {
        handleCommand(update);
    }

    private void sendMessage(long chatId, String message) {
        SendMessage msg = new SendMessage(String.valueOf(chatId), message);
        try {
            execute(msg);
            log.info(String.format("Message %s sent to user", msg.getText()));
        }
        catch (TelegramApiException e) {
            log.error("Error sending message" + e.getMessage());
        }
    }

    private void handleCommand(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            log.info(String.format("Received message %s from user %s %s %s",
                    messageText,
                    update.getMessage().getChat().getFirstName(),
                    update.getMessage().getChat().getLastName(),
                    update.getMessage().getChat().getUserName()));

            if (messageText.startsWith("/hh"))
                handleHHcommand(update);
            else {
                switch (messageText) {
                    case "/start" -> handleStartCommand(update);
                    default -> handleDefaultCommand(update);
                }
            }
        }
    }

    // Сделать норм обработку команды
    private void handleStartCommand(Update update) {
        long chatId = update.getMessage().getChatId();
        sendMessage(chatId, "No respond to /start command yet");
    }

    // Сделать норм обработку команды
    private void handleHHcommand(Update update) {
        long chatId = update.getMessage().getChatId();
        String[] messageWords = update.getMessage().getText().split(" ");
        String URL = Parser.defaultURL;
        int numberOfVacancies = Parser.defaultNumberOfVacancies;
        List<String> params = new ArrayList<>();
        try {
            URL = Parser.getURL(messageWords[1]);
            numberOfVacancies = Integer.parseInt(messageWords[2]);
            for (int i = 3; i < messageWords.length; i++)
                params.add(messageWords[i].toLowerCase());
        }
        catch (IndexOutOfBoundsException e) {
            log.error(e.getMessage());
        }

        // parsing and sending
        try {
            Parser parser = new Parser();
            List<Vacancy> vacancies = parser.parse(URL, numberOfVacancies);
            vacancies = VacanciesFilter.filterByKeywords(vacancies, params, "name");

            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Content-Type", "application/json");

            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            //InputStream responseStream = con.getInputStream();
            wr.close();




            for (Vacancy vacancy : vacancies)
                sendMessage(chatId, vacancy.getLink());
        } catch (IOException e) {
            log.error("Failed to initialise parser. Process aborted");
        }
    }

    private void handleDefaultCommand(Update update) {
        long chatId = update.getMessage().getChatId();
        sendMessage(chatId, config.commandNotDefinedAnswer);
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
