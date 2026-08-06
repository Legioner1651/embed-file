package ru.ruslan.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Slf4j
@Service
@RequiredArgsConstructor
public class RestClientService {

    private final FileService fileServiceInstance;
    private final RestClient restClientInstance;
    private final ObjectMapper objectMapperInstance;

    // Статические поля для обеспечения работы статического метода
    private static FileService fileService;
    private static RestClient restClient;
    private static ObjectMapper mapper;

    @PostConstruct
    private void init() {
        fileService = this.fileServiceInstance;
        restClient = this.restClientInstance;
        mapper = this.objectMapperInstance;
    }

    public JsonNode executeGet(String uriPath) {
        try {
            log.info("Выполнение динамического GET запроса по пути: {}", uriPath);
            return restClientInstance.get()
                    .uri(uriPath)
                    .retrieve()
                    .body(JsonNode.class);
        } catch (Exception e) {
            log.error("Ошибка при выполнении GET запроса к {}: {}", uriPath, e.getMessage());
            throw new RuntimeException("Ошибка внешнего GET-запроса", e);
        }
    }

    public JsonNode executePost(String uriPath, Object body) {
        try {
            log.info("Выполнение динамического POST запроса по пути: {}", uriPath);
            return restClientInstance.post()
                    .uri(uriPath)
                    .body(body)
                    .retrieve()
                    .body(JsonNode.class);
        } catch (Exception e) {
            log.error("Ошибка при выполнении POST запроса к {}: {}", uriPath, e.getMessage());
            throw new RuntimeException("Ошибка внешнего POST-запроса", e);
        }
    }

    public static JsonNode getProdBackCalculateFindByBankbookEIP(String billAccounts, String orponCode) {
        String relativePath = "/prod-back/calculate";
        ObjectNode payload = mapper.createObjectNode();
        payload.put("billAccounts", billAccounts);
        payload.put("orponCode", orponCode);

        try {
            log.info("Выполнение статического POST для аккаунта: {} по пути: {}", billAccounts, relativePath);
            return restClient.post()
                    .uri(relativePath)
                    .body(payload)
                    .retrieve()
                    .body(JsonNode.class);
        } catch (Exception e) {
            log.error("Ошибка статического POST запроса к {}: {}", relativePath, e.getMessage());
            throw new RuntimeException("Ошибка выполнения статического POST расчета", e);
        }
    }
}
