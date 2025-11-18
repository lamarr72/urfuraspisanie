package org.example;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TokenReader {
    public static String readToken() {
        String botToken = "";
        try {
            //Path path = Paths.get("src\\main\\java\\org\\example\\token.txt");
            Path path = Paths.get("token.txt");
            botToken = Files.readString(path);
            //System.out.println("Bot Token: " + botToken);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return botToken;
    }
}
