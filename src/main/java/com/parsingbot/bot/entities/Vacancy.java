package com.parsingbot.bot.entities;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Data
public class Vacancy {
    private Long id;
    private String name;
    private String link;

    public Map<String, String> getAttributes() {
        Map<String, String> attributes = new HashMap<>();
        attributes.put("name", name);
        attributes.put("link", link);
        return attributes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vacancy vacancy = (Vacancy) o;
        return Objects.equals(name, vacancy.name) && Objects.equals(link, vacancy.link);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, link);
    }
}
