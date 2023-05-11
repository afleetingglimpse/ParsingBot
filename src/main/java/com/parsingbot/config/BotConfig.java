package com.parsingbot.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("application.properties")
public class BotConfig {

    @Value("${bot.name}")
    private String botName;

    @Value("${bot.token}")
    private String token;

    @Value("${bot.commandNotDefinedAnswer}")
    private String commandNotDefinedAnswer;

    @Value("${bot.dbServerUrl}")
    private String dbServerUrl;

    @Value("${bot.getAllVacanciesURI}")
    private String getAllVacanciesURI;

    @Value("${bot.saveVacancyURI}")
    private String saveVacancyURI;

    public String getBotName() {
        return botName;
    }

    public String getToken() {
        return token;
    }

    public String getCommandNotDefinedAnswer() {
        return commandNotDefinedAnswer;
    }

    public String getDbServerUrl() {
        return dbServerUrl;
    }

    public String getGetAllVacanciesURI() {
        return getAllVacanciesURI;
    }

    public String getSaveVacancyURI() {
        return saveVacancyURI;
    }
}
