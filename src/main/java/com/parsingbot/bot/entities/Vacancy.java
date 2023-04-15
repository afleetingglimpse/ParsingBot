package com.parsingbot.bot.entities;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

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
}
