//package ru.ruslan.service;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.stereotype.Component;
//import ru.ruslan.model.CreateFileVKRequest;
//import ru.ruslan.service.impl.*;
//import com.fasterxml.jackson.databind.JsonNode;
//
//@Component
//public class VkEpcApp {
//    private static final Logger logger = LoggerFactory.getLogger(VkEpcApp.class);
//
//    private final RestClientService restClientService;
//    private final OracleDbService oracleDbService;
//    private final PostgreSqlDbService postgreSqlDbService;
//    private final JavaScriptService javaScriptService;
//    private final JsonNodeService jsonProcessingService;
//    private final JsoupService htmlProcessingService;
//
//    public VkEpcApp(RestClientService restClientService,
//                    OracleDbService oracleDbService,
//                    PostgreSqlDbService postgreSqlDbService,
//                    JavaScriptService javaScriptService,
//                    JsonNodeService jsonProcessingService,
//                    JsoupService htmlProcessingService) {
//        this.restClientService = restClientService;
//        this.oracleDbService = oracleDbService;
//        this.postgreSqlDbService = postgreSqlDbService;
//        this.javaScriptService = javaScriptService;
//        this.jsonProcessingService = jsonProcessingService;
//        this.htmlProcessingService = htmlProcessingService;
//    }
//
//    public String processRestRequests(String kns) {
//        logger.info("Processing REST requests for KNS: {}", kns);
//        return restClientService.fetchExternalData(kns);
//    }
//
//    public String processOracleQueries(String orderId) {
//        logger.info("Processing Oracle queries for orderId: {}", orderId);
//        return oracleDbService.getExerciseData(orderId);
//    }
//
//    public String processPostgreSqlQueries(String billingAccount) {
//        logger.info("Processing PostgreSQL queries for billingAccount: {}", billingAccount);
//        return postgreSqlDbService.getBillingData(billingAccount);
//    }
//
//    public String processJavaScript(String epcParams) {
//        logger.info("Processing JavaScript for EPC params");
//        return javaScriptService.processEpcParams(epcParams);
//    }
//
//    public JsonNode processJson(String jsonData) {
//        logger.info("Processing JSON data");
//        return jsonProcessingService.parseAndProcessJson(jsonData);
//    }
//
//    public String processHtml(CreateFileVKRequest request, String oracleData,
//                              String postgresData, JsonNode jsonNode,
//                              String processedEpcParams, String fileContent) {
//        logger.info("Processing HTML document");
//        return htmlProcessingService.createHtmlDocument(
//                request, oracleData, postgresData, jsonNode, processedEpcParams, fileContent);
//    }
//}