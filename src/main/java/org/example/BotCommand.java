package org.example;

public class BotCommand {
    private final String name;
    private final String description;
    private final String fullResponse;


    public BotCommand(String name, String description, String fullResponse) {
        this.name = name;
        this.description = description;
        this.fullResponse = fullResponse;
    }

    public String getName() {
        return name;
    }
    public String getDescription() {
        return description;
    }
    public String getFullResponse() {
        return fullResponse;
    }
}
