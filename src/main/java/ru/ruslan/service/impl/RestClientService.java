package ru.ruslan.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

@Service
public class RestClientService {
    private static final Logger logger = LoggerFactory.getLogger(RestClientService.class);

    private final RestTemplate restTemplate;

    public RestClientService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String fetchExternalData(String kns) {
        logger.info("Fetching external data for KNS: {}", kns);

        // Пример REST запроса к внешнему сервису
        String url = "http://external-service/api/data?kns=" + kns;
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        return response.getBody();
    }

    public String readFilesFromPath(String path) {
        logger.info("Reading files from path: {}", path);

        try {
            Path filePath = Paths.get(path);
            if (Files.exists(filePath) && Files.isDirectory(filePath)) {
                return Files.list(filePath)
                        .filter(Files::isRegularFile)
                        .map(this::readFileContent)
                        .collect(Collectors.joining("\n"));
            } else if (Files.exists(filePath) && Files.isRegularFile(filePath)) {
                return readFileContent(filePath);
            }
        } catch (IOException e) {
            logger.error("Error reading files from path: {}", path, e);
        }

        return "";
    }

    private String readFileContent(Path filePath) {
        try {
            return Files.readString(filePath);
        } catch (IOException e) {
            logger.error("Error reading file: {}", filePath, e);
            return "";
        }
    }
}