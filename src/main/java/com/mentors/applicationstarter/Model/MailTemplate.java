package com.mentors.applicationstarter.Model;

import lombok.Builder;

import java.util.HashMap;
import java.util.Map;

@Builder
public class MailTemplate {
    private final Map<String, String> templateVariables = new HashMap<>();

    public MailTemplate add(String key, String value) {
        templateVariables.put(key, value);
        return this;
    }

    public Map<String, String> getVariables() {
        return templateVariables;
    }
}
