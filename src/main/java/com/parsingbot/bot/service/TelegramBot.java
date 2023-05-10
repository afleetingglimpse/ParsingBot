package com.parsingbot.bot.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.parsingbot.bot.config.BotConfig;
import com.parsingbot.bot.entities.Vacancy;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import static java.net.URI.create;

@Component
public class TelegramBot extends TelegramLongPollingBot {

    private final BotConfig config;
    private static final Logger LOGGER = Logger.getLogger(TelegramBot.class.getName());

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
            LOGGER.info(String.format("Message %s sent to user", msg.getText()));
        }
        catch (TelegramApiException e) {
            LOGGER.warning("Error sending message" + e.getMessage());
        }
    }

    private void handleCommand(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            LOGGER.info(String.format("Received message %s from user %s %s %s",
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
            LOGGER.warning(e.getMessage());
        }

        // parsing and sending
        try {
            Parser parser = new Parser();
            List<Vacancy> vacancies = parser.parse(URL, numberOfVacancies);
            vacancies = VacanciesFilter.filterByKeywords(vacancies, params, "name");

            List<Vacancy> vacanciesDB = getAllVacanciesDB("http://localhost:8000/");

            vacancies.forEach(vacancy -> {
                if (!vacanciesDB.contains(vacancy)) {
                    saveVacancy(vacancy, "http://localhost:8000/");
                    sendMessage(chatId, vacancy.getLink());
                }
            });
        } catch (IOException e) {
            LOGGER.warning("Failed to initialise parser. Process aborted");
        }
    }

    private List<Vacancy> getAllVacanciesDB(String URI) {
        HttpClient client = HttpClient.newHttpClient();
        List<Vacancy> vacancies = null;
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .setHeader("Content-Type","application/json")
                .uri(create(URI))
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            ObjectMapper objectMapper = new ObjectMapper();
            vacancies = objectMapper.readValue(response.body(), new TypeReference<List<Vacancy>>() {});
            LOGGER.warning("Request sent");
        } catch (IOException e) {
            LOGGER.warning(Arrays.toString(e.getStackTrace()));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return vacancies;
    }


    private void saveVacancy(Vacancy vacancy, String URI) {
        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody;
        try {
            requestBody = objectMapper.writeValueAsString(vacancy);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .setHeader("Content-Type","application/json")
                .uri(create(URI))
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            LOGGER.warning("Request sent");
        } catch (IOException e) {
            LOGGER.warning(Arrays.toString(e.getStackTrace()));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
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
