package ru.ruslan.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.ruslan.model.CreateFileVKRequest;
import ru.ruslan.model.CreateFileVKResponse;
import ru.ruslan.service.impl.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class VkEpcService {
    private static final Logger logger = LoggerFactory.getLogger(VkEpcService.class);

    private final RestClientService restClientService;
    private final JavaScriptService javaScriptService;
    private final JsonProcessingService jsonProcessingService;
    private final HtmlProcessingService htmlProcessingService;
    private final ObjectMapper objectMapper;

    public VkEpcService(RestClientService restClientService,
                        JavaScriptService javaScriptService,
                        JsonProcessingService jsonProcessingService,
                        HtmlProcessingService htmlProcessingService,
                        ObjectMapper objectMapper) {
        this.restClientService = restClientService;
        this.javaScriptService = javaScriptService;
        this.jsonProcessingService = jsonProcessingService;
        this.htmlProcessingService = htmlProcessingService;
        this.objectMapper = objectMapper;
    }

    public CreateFileVKResponse processRequest(CreateFileVKRequest request) {
        logger.info("Processing VK EPC request for orderId: {}, exerciseId: {}",
                request.getOrderId(), request.getExerciseId());

        try {
            // 1. Получаем данные из Oracle

            // 2. Получаем данные из PostgreSQL

            // 3. Делаем REST запросы для получения дополнительных данных
            String externalData = restClientService.fetchExternalData(request.getKns());

            // 4. Обрабатываем EPC параметры с помощью JavaScript
            String processedEpcParams = javaScriptService.processEpcParams(request.getEpcParams());

            // 5. Обрабатываем JSON данные
            JsonNode jsonNode = jsonProcessingService.parseAndProcessJson(externalData);

            // 6. Читаем файлы из указанного пути
            String fileContent = restClientService.readFilesFromPath(request.getPathFileVK());

            // 7. Создаем HTML документ
            String htmlContent = htmlProcessingService.createHtmlDocument(
                    request, jsonNode, processedEpcParams, fileContent);

            return new CreateFileVKResponse(
                    "SUCCESS",
                    "File created successfully",
                    htmlContent,
                    "vk_epc_" + request.getOrderId() + ".html"
            );

        } catch (Exception e) {
            logger.error("Error processing VK EPC request", e);
            return new CreateFileVKResponse(
                    "ERROR",
                    "Failed to process request: " + e.getMessage(),
                    null,
                    null
            );
        }
    }
}