package com.parsingbot.bot.service;

import lombok.extern.slf4j.Slf4j;
import com.parsingbot.bot.entities.Vacancy;
import java.util.*;

@Slf4j // logging
public class VacanciesFilter {


    /** Функция возвращает список вакансий, у которых в атрибуте attributeName встречается ключевое слово keyword */
    public static List<Vacancy> filterByKeyword(List<Vacancy> vacancies, String keyword, String attributeName) {
        List<Vacancy> vacanciesFiltered = new ArrayList<>();
        for (Vacancy vacancy : vacancies) {
            Map<String, String> attributes = vacancy.getAttributes();
            if (attributes.containsKey(attributeName)) {
                String attribute = attributes.get(attributeName).toLowerCase();
                if (attribute.contains(keyword.toLowerCase())) {
                    vacanciesFiltered.add(vacancy);
                }
                else {
                    log.info(String.format("Vacancy %s doesn't contain keyword %s",
                            vacancy.getLink(), keyword));
                }
            }
            else {
                log.info(String.format("Attribute %s not found for vacancy %s", attributeName, vacancy.getLink()));
            }
        }
        return vacanciesFiltered;
    }


    public static List<Vacancy> filterByKeywords(List<Vacancy> vacancies, List<String> keywords, String attributeName) {
        List<Vacancy> vacanciesFiltered = vacancies;
        for (String keyword : keywords)
            vacanciesFiltered = filterByKeyword(vacanciesFiltered, keyword, attributeName);
        return vacanciesFiltered;
    }
}
