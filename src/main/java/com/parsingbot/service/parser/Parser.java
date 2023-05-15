package com.parsingbot.service.parser;

import com.parsingbot.config.BotConfig;
import com.parsingbot.entities.Vacancy;
import com.parsingbot.service.requests.RequestHandler;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.*;
import java.util.regex.PatternSyntaxException;
@Component
public class Parser {
    // константы
    public static final String DEFAULT_SEARCH_URL = "https://hh.ru/search/vacancy?text=junior+java&area=1&page=";
    public static final String DEFAULT_LINK_KEY = "href";
    public static final String DEFAULT_VACANCY_BODY_CLASS = "vacancy-serp-item-body";
    public static final String DEFAULT_VACANCY_TITLE_CLASS = "serp-item__title";
    public static final int DEFAULT_NUMBER_OF_VACANCIES = 20;

    @Autowired
    private RequestHandler requestHandler;

    @Autowired
    private BotConfig botConfig;

    //HH принимает запросы в виде /vacancy?text=YOUR+TEXT+HERE&area=...
    public static String getUrlWithKeywords(String arg) {
        String urlBody = DEFAULT_SEARCH_URL;
        try {
            String[] urlSplit = arg.split(" ");
            urlBody = String.join("+", urlSplit);
        }
        catch (PatternSyntaxException | NullPointerException e) {
            System.out.println("Cannot split input line, URL is set to default");
        }

        return String.format("https://hh.ru/search/vacancy?text=%s&area=1&page=", urlBody);
    }


    /** Функция парсит сайт по заданным классам
     * @param URL ссылка на сайт
     * @param numberOfVacancies количество возвращаемых вакансий
     *
     * @return список объектов вакансий
     * */
    private List<Vacancy> getVacanciesAttributes(String URL, int numberOfVacancies) throws IOException {

        int page = 0;
        List<Vacancy> vacancies = new ArrayList<>();

        List<Vacancy> vacanciesDB = requestHandler.getAllVacanciesDB(botConfig.getGetAllVacanciesURI());

        while (vacancies.size() < numberOfVacancies) {
            Document doc = Jsoup.connect(URL + page).get();
            Elements vacanciesElements = doc.getElementsByClass(DEFAULT_VACANCY_BODY_CLASS);
            for (Element vacancy : vacanciesElements) {

                if (vacancies.size() < numberOfVacancies) {
                    // получение основной информации о вакансии
                    Elements mainElements = vacancy.getElementsByClass(DEFAULT_VACANCY_TITLE_CLASS);
                    String vacancyName = mainElements.text();
                    String link = mainElements.attr(DEFAULT_LINK_KEY);

                    // создание объекта вакансии и заполнение основных полей
                    Vacancy temp = new Vacancy();
                    temp.setName(vacancyName);
                    temp.setLink(link);

                    if (!vacanciesDB.contains(temp))
                        vacancies.add(temp);
                }
            }
            page++;
        }
        return vacancies;
    }

    public List<Vacancy> parse(String URL, int numberOfVacancies) throws IOException {
        return getVacanciesAttributes(URL, numberOfVacancies);
    }
}