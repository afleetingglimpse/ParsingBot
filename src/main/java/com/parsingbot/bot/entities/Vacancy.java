package com.parsingbot.bot.entities;
import lombok.Data;

import java.util.Map;

@Data
public class Vacancy {
    private Long id;
    private String name;
    private String link;
    private Map<String, String> attributes;
    private String companyName;
    private String requirements;
    private String additional;
}
