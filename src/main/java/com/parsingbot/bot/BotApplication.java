package com.parsingbot.bot;

import com.parsingbot.bot.config.BotInitializer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.util.logging.LogManager;
import java.util.logging.Logger;

@SpringBootApplication
public class BotApplication {
	private static final Logger LOGGER = Logger.getLogger(BotApplication.class.getName());
	public static void main(String[] args) {
		try {
			LogManager.getLogManager().readConfiguration(BotApplication.class.getResourceAsStream("/logging.properties"));
		}
		catch (IOException e) {
			System.err.println("Could not setup logger configuration: " + e.toString());
		}

		SpringApplication.run(BotApplication.class, args);
	}
}
