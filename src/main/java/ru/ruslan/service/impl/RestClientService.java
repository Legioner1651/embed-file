package ru.ruslan.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${rest.client.service.bankbookEIP-url}")
    private String bankbookEipUrl;

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

    public JsonNode getProdBackCalculateFindByBankbookEIP(String billAccounts, String orponCode) {

        // 2. Формируем JSON-тело запроса (экранированная строка из Postman)
        String jsonBody2 = """
                {
                    "method": "getProductOfferCfg",
                    "version": "2.0",
                    "methodParams": {
                        "marketSegments": [
                            {
                                "codeName": "3"
                            }
                        ],
                        "checkRequiredProps": true,
                        "properties": [
                            {
                                "codeName": "ADDRESS_ORPON",
                                "value": [
                                    "%s"
                                ]
                            }
                        ],
                        "identification": {
                            "ids": [
                                "%s"
                            ],
                            "systemId": "EIP",
                            "typeId": "account"
                        }
                    },
                    "configId": "snoop"
                }
                """.formatted(orponCode, billAccounts);

        jsonBody2 = jsonBody2.strip();
        jsonBody2 = jsonBody2.replace("\n", "").replace(" ", "");

        log.info("-".repeat(50));
        log.info("JSON-тело REST запроса к ОВ = \n" + jsonBody2 + "\n");
        log.info("-".repeat(50));

        try {
            log.info("Выполнение POST запроса");
            return restClient.post()
                    .uri(bankbookEipUrl)
                    .header("Content-Type", "application/json")
                    .header("X-StepRequest", "1")
                    .body(jsonBody2)
                    .retrieve()
                    .body(JsonNode.class);
        } catch (Exception e) {
            log.error("Ошибка POST запроса {}", e.getMessage());
            throw new RuntimeException("Трассировка стека ошибки выполнения POST запроса", e);
        }
    }
}
