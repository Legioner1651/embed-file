package ru.ruslan.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@Service
public class FileService {

    public String readFromResources(String resourcePath) {
        try {
            ClassPathResource resource = new ClassPathResource(resourcePath);
            return Files.readString(Paths.get(resource.getURI()), StandardCharsets.UTF_8);
        } catch (IOException | IllegalArgumentException e) {
            log.error("Ошибка чтения ресурса из classpath {}: {}", resourcePath, e.getMessage());
            throw new RuntimeException("Не удалось прочитать ресурс: " + resourcePath, e);
        }
    }

    public String readFromFileSystem(String filePath) {
        try {
            return Files.readString(Path.of(filePath), StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error("Ошибка чтения файла из ФС по пути {}: {}", filePath, e.getMessage());
            throw new RuntimeException("Не удалось прочитать файл по адресу : " + filePath, e);
        }
    }

    public void writeToFileSystem(String filePath, String content) {
        try {
            Path path = Path.of(filePath);
            if (path.getParent() != null) {
                Files.createDirectories(path.getParent());
            }
            Files.writeString(path, content, StandardCharsets.UTF_8);
            log.info("Файл успешно создан/обновлен по адресу: {}", filePath);
        } catch (IOException e) {
            log.error("Ошибка записи файла в ФС по пути {}: {}", filePath, e.getMessage());
            throw new RuntimeException("Не удалось записать файл в ФС: " + filePath, e);
        }
    }

    public boolean isPathValid(String pathStr) {

        // Отсекаем null, пустые строки и пробелы
        if (pathStr == null || pathStr.trim().isEmpty()) { return false; }

        try {
            Path path = Paths.get(pathStr);
            // Проверяет физическое наличие файла или папки на диске
            return Files.exists(path);
        } catch (InvalidPathException e) {
            return false;
        }
    }
}
