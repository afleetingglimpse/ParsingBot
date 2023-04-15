package com.parsingbot.bot.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import com.parsingbot.bot.entities.Vacancy;

import java.io.*;
import java.util.*;
import java.util.regex.PatternSyntaxException;

public class Parser {

    // константы
    public static final String defaultURL = "https://hh.ru/search/vacancy?text=junior+java&area=1&page=";
    public static final String defaultLinkKey = "href";
    public static final String defaultVacancyBodyClass = "vacancy-serp-item-body";
    public static final String defaultVacancyTitleClass = "serp-item__title";
    public static final int defaultNumberOfVacancies = 20;

    // атрибуты поиска
    private final Map<String, String> attributes;


    //HH принимает запросы в виде /vacancy?text=YOUR+TEXT+HERE&area=...
    public static String getURL(String arg) {
        String urlBody = defaultURL;
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
     * @param vacancyBodyClass название класса, содержащего тело (основную инфу) вакансии
     * @param vacancyTitleClass название класса, содержащего название вакансии
     * @param attributes мапа, содержащая название атрибута (как он будет выведен пользователю)
     *                   и название его класса (как он задан на сайте). Например для
     *                   атрибута "companyName" на hh это класс "bloko-link bloko-link_kind-tertiary"
     *
     * @return список объектов вакансий
     * Например: <"Java developer", <"link", "http://link.com">>
     * */
    private static List<Vacancy> getVacanciesAttributes(String URL, int numberOfVacancies, String vacancyBodyClass,
                                                        String vacancyTitleClass, Map<String, String> attributes)
                                                        throws IOException {

        int page = 0;
        List<Vacancy> vacancies = new ArrayList<>(20);

        while (vacancies.size() < numberOfVacancies) {
            Document doc = Jsoup.connect(URL + page).get();
            Elements vacanciesElements = doc.getElementsByClass(vacancyBodyClass);
            for (Element vacancy : vacanciesElements) {

                if (vacancies.size() < numberOfVacancies) {
                    // получение основной информации о вакансии
                    Elements mainElements = vacancy.getElementsByClass(vacancyTitleClass);
                    String vacancyName = mainElements.text();
                    String link = mainElements.attr(defaultLinkKey);

                    // создание объекта вакансии и заполнение основных полей
                    Vacancy temp = new Vacancy();
                    temp.setName(vacancyName);
                    temp.setLink(link);

                    vacancies.add(temp);
                }
            }
            page++;
        }
        return vacancies;
    }

    /** Функция сохраняет результат поиска в файл в следующем формате:
     *  <p>"название вакансии1"</p>
     *      <p>"атрибут1"  "значение атрибута1"</p>
     *      <p>"атрибут2"  "значение атрибута2"</p>
     * <p>
     *  <p>"название вакансии2"</p>
     *      <p>"атрибут1"  "значение атрибута1"</p>
     *      <p>"атрибут2"  "значение атрибута2"</p>
     * */

    public List<Vacancy> parse() throws IOException {
        return getVacanciesAttributes(defaultURL, defaultNumberOfVacancies, defaultVacancyBodyClass,
                                      defaultVacancyTitleClass, attributes);
    }


    public List<Vacancy> parse(String URL, int numberOfVacancies, String vacancyBodyClass, String vacancyTitleClass,
                               Map<String, String> attributes) throws IOException {

        return getVacanciesAttributes(URL, numberOfVacancies, vacancyBodyClass, vacancyTitleClass, attributes);
    }

    public List<com.parsingbot.bot.entities.Vacancy> parse(String URL, int numberOfVacancies) throws IOException {

        return getVacanciesAttributes(URL, numberOfVacancies, defaultVacancyBodyClass,
                                      defaultVacancyTitleClass, attributes);
    }


    public List<Vacancy> parse(Map<String, String> attributes) throws IOException {
        return getVacanciesAttributes(defaultURL, defaultNumberOfVacancies, defaultVacancyBodyClass,
                                      defaultVacancyTitleClass, attributes);
    }



    public Parser(Map<String, String> attributes) throws IOException {
        this.attributes = attributes;
    }


    public Parser() throws IOException {
        attributes = new HashMap<>();
        attributes.put("companyName" , "bloko-link bloko-link_kind-tertiary");
        attributes.put("link", defaultVacancyTitleClass);
    }
}