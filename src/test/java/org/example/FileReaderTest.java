package org.example;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class FileReaderTest {

    @TempDir
    Path tempDir;

    @Test
    void testReadTokenFromValidFile() throws IOException {
        // Тестируем чтение токена из корректного файла
        File tokenFile = tempDir.resolve("test_token.txt").toFile();
        String expectedToken = "12345:test-token-here-abcdef";

        try (FileWriter writer = new FileWriter(tokenFile)) {
            writer.write(expectedToken);
        }

        String actualToken = TestFileReader.readTokenFromFile(tokenFile.getAbsolutePath());
        assertEquals(expectedToken, actualToken);
    }

    @Test
    void testReadTokenFromNonExistentFile() {
        // Тестируем чтение из несуществующего файла
        String token = TestFileReader.readTokenFromFile("non_existent_file_12345.txt");
        assertEquals("", token);
    }

    @Test
    void testReadTokenWithWhitespace() throws IOException {
        // Тестируем чтение токена с пробелами
        File tokenFile = tempDir.resolve("token_with_space.txt").toFile();
        String tokenWithSpaces = "   token_with_spaces_123   ";
        String expectedToken = "token_with_spaces_123";

        try (FileWriter writer = new FileWriter(tokenFile)) {
            writer.write(tokenWithSpaces + "\n");
        }

        String actualToken = TestFileReader.readTokenFromFile(tokenFile.getAbsolutePath());
        assertEquals(expectedToken, actualToken);
    }

    @Test
    void testReadTokenFromEmptyFile() throws IOException {
        // Тестируем чтение из пустого файла
        File tokenFile = tempDir.resolve("empty_token.txt").toFile();
        tokenFile.createNewFile();

        String token = TestFileReader.readTokenFromFile(tokenFile.getAbsolutePath());
        assertEquals("", token);
    }

    @Test
    void testReadTokenWithMultipleLines() throws IOException {
        // Тестируем чтение токена когда в файле несколько строк
        File tokenFile = tempDir.resolve("multi_line_token.txt").toFile();
        String firstLineToken = "first_line_token";
        String secondLine = "second_line_ignored";

        try (FileWriter writer = new FileWriter(tokenFile)) {
            writer.write(firstLineToken + "\n");
            writer.write(secondLine + "\n");
        }

        String actualToken = TestFileReader.readTokenFromFile(tokenFile.getAbsolutePath());
        assertEquals(firstLineToken, actualToken);
    }

    @Test
    void testReadTokenWithSpecialCharacters() throws IOException {
        // Тестируем чтение токена со специальными символами
        File tokenFile = tempDir.resolve("special_token.txt").toFile();
        String specialToken = "12345:ABC-def_GHI@jkl%mno";

        try (FileWriter writer = new FileWriter(tokenFile)) {
            writer.write(specialToken);
        }

        String actualToken = TestFileReader.readTokenFromFile(tokenFile.getAbsolutePath());
        assertEquals(specialToken, actualToken);
    }

    @Test
    void testFilePermissions() throws IOException {
        // Тестируем базовую функциональность
        File tokenFile = tempDir.resolve("normal_token.txt").toFile();
        String expectedToken = "normal_token_123";

        try (FileWriter writer = new FileWriter(tokenFile)) {
            writer.write(expectedToken);
        }

        // Проверяем что файл существует и читается
        assertTrue(tokenFile.exists());
        assertTrue(tokenFile.canRead());

        String actualToken = TestFileReader.readTokenFromFile(tokenFile.getAbsolutePath());
        assertEquals(expectedToken, actualToken);
    }

    // Вспомогательный класс для тестирования FileReader
    private static class TestFileReader {
        public static String readTokenFromFile(String filePath) {
            try (java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.FileReader(filePath))) {
                String token = reader.readLine();
                return token != null ? token.trim() : "";
            } catch (IOException e) {
                System.err.println("Ошибка чтения файла: " + e.getMessage());
                return "";
            }
        }
    }
}