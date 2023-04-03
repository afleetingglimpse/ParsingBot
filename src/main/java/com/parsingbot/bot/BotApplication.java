package com.parsingbot.bot;

import com.parsingbot.bot.config.BotConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BotApplication {

	public static void main(String[] args) {
		BotConfig a = new BotConfig();
		SpringApplication.run(BotApplication.class, args);
	}
}
