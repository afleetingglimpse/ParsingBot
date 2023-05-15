package com.parsingbot.service.bot;

import com.parsingbot.config.BotConfig;
import com.parsingbot.entities.Vacancy;
import com.parsingbot.service.parser.Parser;
import com.parsingbot.service.parser.VacanciesFilter;
import com.parsingbot.service.requests.RequestHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;


@Component
public class CommandHandler {

    private static final Logger LOGGER = Logger.getLogger(CommandHandler.class.getName());

    @Autowired
    private RequestHandler requestHandler;

    @Autowired
    private Parser parser;

    @Autowired
    private BotConfig botConfig;

    public void handleCommand(Update update, TelegramBot bot) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText().toLowerCase();
            LOGGER.info(String.format("Received message %s from user %s %s %s",
                    messageText,
                    update.getMessage().getChat().getFirstName(),
                    update.getMessage().getChat().getLastName(),
                    update.getMessage().getChat().getUserName()));

            if (messageText.startsWith("/hh"))
                handleHHcommand(update, bot);

            else
                switch (messageText) {
                    case "/start" -> handleStartCommand(update, bot);
                    default -> handleDefaultCommand(update, bot);
                }
        }
    }

    private void handleStartCommand(Update update, TelegramBot bot) {
        long chatId = update.getMessage().getChatId();
        bot.sendMessage(chatId, "No respond to /start command yet");
    }

    private void handleHHcommand(Update update, TelegramBot bot) {
        long chatId = update.getMessage().getChatId();

        String[] messageWords = update.getMessage().getText().split(" ");

        String URL = Parser.DEFAULT_SEARCH_URL;
        int numberOfVacancies = Parser.DEFAULT_NUMBER_OF_VACANCIES;
        List<String> keywords = new ArrayList<>();

        if (messageWords.length > 1) {
            URL = Parser.getUrlWithKeywords(messageWords[1]);
            LOGGER.info("Parsing URL: %s".formatted(URL));
        }

        if (messageWords.length > 2) {
            numberOfVacancies = Integer.parseInt(messageWords[2]);
            LOGGER.info("Number of vacancies: %s".formatted(numberOfVacancies));
        }

        if (messageWords.length > 3) {
            keywords.addAll(Arrays.asList(messageWords).subList(3, messageWords.length));
            LOGGER.info("Parsing keywords are: %s".formatted(keywords));
        }

        // parsing and sending
        try {
            List<Vacancy> vacancies = parser.parse(URL, numberOfVacancies);
            vacancies = VacanciesFilter.filterByKeywords(vacancies, keywords, "name");

            vacancies.forEach(vacancy -> {
                requestHandler.saveVacancy(vacancy, botConfig.getSaveVacancyURI());
                bot.sendMessage(chatId, vacancy.getLink());
            });
        }
        catch (IOException e) {
            LOGGER.warning("Failed to initialise parser. Process aborted");
            LOGGER.warning(e.getMessage());
            LOGGER.warning(Arrays.toString(e.getStackTrace()));
        }
    }

    private void handleDefaultCommand(Update update, TelegramBot bot) {
        long chatId = update.getMessage().getChatId();
        bot.sendMessage(chatId, bot.getConfig().getCommandNotDefinedAnswer());
    }
}
