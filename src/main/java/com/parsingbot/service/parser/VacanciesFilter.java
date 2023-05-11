package com.parsingbot.service.parser;

import com.parsingbot.entities.Vacancy;

import java.util.*;

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

                }
            }
            else {
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
