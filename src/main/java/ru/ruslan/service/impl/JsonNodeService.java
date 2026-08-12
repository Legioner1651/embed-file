package ru.ruslan.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class JsonNodeService {

    private final FileService fileService;
    private final ObjectMapper mapper;

    public JsonNode parseJsonNode(String jsonCode) {
        try {
            return mapper.readTree(jsonCode);
        } catch (Exception e) {
            log.error("Ошибка парсинга JSON: {}", e.getMessage());
            throw new RuntimeException("Не удалось распарсить JSON строку", e);
        }
    }

    public String jsonNodeToString(JsonNode jsonNode, boolean isPretty) {
        try {
            return isPretty ? mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonNode)
                    : mapper.writeValueAsString(jsonNode);
        } catch (Exception e) {
            log.error("Ошибка конвертации JsonNode в строку: {}", e.getMessage());
            return "";
        }
    }

    public void printTemplateForEPC(JsonNode jsonNode) {
        log.info("Шаблон EPC данные: {}", jsonNodeToString(jsonNode, false));
    }

    /** Лицевые счета */
    public static List<String> getAccounts(JsonNode jsonNode) {

        // Инициализируем список для хранения всех IDS
        List<String> ids = new ArrayList<>();

        for (JsonNode node : jsonNode.path("clientProperties")) {
            if ("account".equals(node.path("typeId").asText()) && "EIP".equals(node.path("systemId").asText())) {
                // Перебираем все элементы внутри найденного массива "ids"
                for (JsonNode idNode : node.path("ids")) {
                    ids.add(idNode.asText());
                }
            }
        }

        return ids;
    }

    /** ОРПОН код адреса абонента */
    public static String getOrponCode(JsonNode jsonNode) {

        String globalId = jsonNode.path("address").path("id").asText();

        return globalId;
    }

    /** Адрес абонента */
    public static String getOrponAddress(JsonNode jsonNode) {
        String globalId = jsonNode.path("address").path("name").asText();

        return globalId;
    }

    /** Бизнес процесс */
    public static String getTypeOperation(JsonNode jsonNode) {
        for (JsonNode node : jsonNode.path("properties")) {
            if ("AVAILABILITY_FOR_CUSTOMERS_VK".equals(node.path("codeName").asText())) {
                String typeOperation = node.path("value").path(0).asText();
                if (typeOperation.equals("NEW_CUSTOMERS")) {
                    return "продажа";
                } else if (typeOperation.equals("EXISTING_CUSTOMERS")) {
                    return "УУ";
                }
            }
        }

        return null;
    }

    /** Используемая технология */
    public static String getTechnology(JsonNode jsonNode) {

        // Итерируемся по элементам массива techAvInfo
        for (JsonNode node : jsonNode.path("techAvInfo")) {
            // Ищем нужный productType
            if ("SHPD".equals(node.path("productType").asText())) {
                // Берем первый элемент из массива techAv и достаем technology
                return node.path("techAv").path(0).path("technology").asText();
            }
        }

        return null;
    }

    public JsonNode getResultsFromResponse(JsonNode jsonNode) {
        return jsonNode.path("results");
    }

    public void changeValue(JsonNode jsonNode, String nodeName, JsonNode newValue) {
        if (jsonNode instanceof ObjectNode) {
            ((ObjectNode) jsonNode).set(nodeName, newValue);
        }
    }

    public void changeValue2EmptyArray(JsonNode jsonNode, String nodeName) {
        if (jsonNode instanceof ObjectNode) {
            ((ObjectNode) jsonNode).set(nodeName, mapper.createArrayNode());
        }
    }

    public void deleteProperties(JsonNode resultsArray) {
        if (resultsArray.isArray()) {
            for (JsonNode item : resultsArray) {
                if (item instanceof ObjectNode) {
                    ((ObjectNode) item).remove("properties");
                }
            }
        } else if (resultsArray instanceof ObjectNode) {
            ((ObjectNode) resultsArray).remove("properties");
        }
    }
}
