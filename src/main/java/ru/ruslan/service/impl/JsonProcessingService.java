package ru.ruslan.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class JsonProcessingService {
    private static final Logger logger = LoggerFactory.getLogger(JsonProcessingService.class);

    private final ObjectMapper objectMapper;

    public JsonProcessingService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public JsonNode parseAndProcessJson(String jsonData) {
        logger.info("Parsing and processing JSON data");

        try {
            JsonNode jsonNode = objectMapper.readTree(jsonData);

            // Обработка JSON данных
            if (jsonNode.isObject()) {
                ObjectNode objectNode = (ObjectNode) jsonNode;
                // Добавляем дополнительные поля при необходимости
                objectNode.put("processedTimestamp", System.currentTimeMillis());
            }

            return jsonNode;
        } catch (Exception e) {
            logger.error("Error processing JSON data", e);
            return objectMapper.createObjectNode();
        }
    }
}