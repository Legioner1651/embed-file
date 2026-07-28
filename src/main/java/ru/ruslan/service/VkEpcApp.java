package ru.ruslan.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.ruslan.model.CreateFileVKRequest;
import ru.ruslan.service.impl.*;
import com.fasterxml.jackson.databind.JsonNode;

@Component
public class VkEpcApp {
    private static final Logger logger = LoggerFactory.getLogger(VkEpcApp.class);

    private final RestClientService restClientService;
    private final JavaScriptService javaScriptService;
    private final JsonProcessingService jsonProcessingService;
    private final HtmlProcessingService htmlProcessingService;

    public VkEpcApp(RestClientService restClientService,
                    JavaScriptService javaScriptService,
                    JsonProcessingService jsonProcessingService,
                    HtmlProcessingService htmlProcessingService) {
        this.restClientService = restClientService;
        this.javaScriptService = javaScriptService;
        this.jsonProcessingService = jsonProcessingService;
        this.htmlProcessingService = htmlProcessingService;
    }

    public String processRestRequests(String kns) {
        logger.info("Processing REST requests for KNS: {}", kns);
        return restClientService.fetchExternalData(kns);
    }

    public String processJavaScript(String epcParams) {
        logger.info("Processing JavaScript for EPC params");
        return javaScriptService.processEpcParams(epcParams);
    }

    public JsonNode processJson(String jsonData) {
        logger.info("Processing JSON data");
        return jsonProcessingService.parseAndProcessJson(jsonData);
    }

    public String processHtml(CreateFileVKRequest request, String oracleData,
                              String postgresData, JsonNode jsonNode,
                              String processedEpcParams, String fileContent) {
        logger.info("Processing HTML document");
        return htmlProcessingService.createHtmlDocument(
                request, jsonNode, processedEpcParams, fileContent);
    }
}