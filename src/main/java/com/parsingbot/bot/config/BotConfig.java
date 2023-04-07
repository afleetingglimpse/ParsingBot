package com.parsingbot.bot.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@Data // auto getter, setter
@PropertySource("application.properties")
public class BotConfig {

    @Value("${bot.name}")
    String botName;

    @Value("${bot.token}")
    String token;

    @Value("${bot.commandNotDefinedAnswer}")
    public String commandNotDefinedAnswer;

    @Value("${bot.vacanciesSaveDir}")
    public String vacanciesSaveDir;

}
