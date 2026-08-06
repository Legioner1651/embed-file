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

    public List<String> getAccounts(JsonNode jsonNode) {
        List<String> accounts = new ArrayList<>();
        JsonNode accNode = jsonNode.path("accounts");
        if (accNode.isArray()) {
            for (JsonNode node : accNode) {
                accounts.add(node.asText());
            }
        }
        return accounts;
    }

    public String getOrponCode(JsonNode jsonNode) {
        return jsonNode.path("orponCode").asText("");
    }

    public String getOrponAddress(JsonNode jsonNode) {
        return jsonNode.path("orponAddress").asText("");
    }

    public String getTypeOperation(JsonNode jsonNode) {
        return jsonNode.path("typeOperation").asText("");
    }

    public String getTechnology(JsonNode jsonNode) {
        return jsonNode.path("technology").asText("");
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
