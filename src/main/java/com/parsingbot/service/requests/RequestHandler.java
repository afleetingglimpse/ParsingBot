package com.parsingbot.service.requests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.parsingbot.entities.Vacancy;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import static java.net.URI.create;

/** Обращение к API HttpDbAccess */
@Component
public class RequestHandler {

    private static final Logger LOGGER = Logger.getLogger(RequestHandler.class.getName());

    public List<Vacancy> getAllVacanciesDB(String getAllVacanciesURI) {
        HttpClient client = HttpClient.newHttpClient();
        List<Vacancy> vacancies = new ArrayList<>();
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .setHeader("Content-Type","application/json")
                .uri(create(getAllVacanciesURI))
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            ObjectMapper objectMapper = new ObjectMapper();
            vacancies = objectMapper.readValue(response.body(), new TypeReference<>() {});
            LOGGER.info("GetAllVacancies request sent successfully");
        } catch (IOException | InterruptedException e) {
            LOGGER.warning("Error while sending GetAllVacancies request");
            LOGGER.warning(Arrays.toString(e.getStackTrace()));
        }
        return vacancies;
    }


    public void saveVacancy(Vacancy vacancy, String saveVacancyURI) {
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
                .uri(create(saveVacancyURI))
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            LOGGER.info("SaveVacancy for vacancy: %s request sent successfully".formatted(vacancy.toString()));
        } catch (IOException | InterruptedException e) {
            LOGGER.warning("Error while sending SaveVacancy request");
            LOGGER.warning(Arrays.toString(e.getStackTrace()));
        }
    }
}
