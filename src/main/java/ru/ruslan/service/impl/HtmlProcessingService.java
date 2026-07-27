package ru.ruslan.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.ruslan.model.CreateFileVKRequest;

@Service
public class HtmlProcessingService {
    private static final Logger logger = LoggerFactory.getLogger(HtmlProcessingService.class);

    public String createHtmlDocument(CreateFileVKRequest request,
                                     String oracleData,
                                     String postgresData,
                                     JsonNode jsonNode,
                                     String processedEpcParams,
                                     String fileContent) {
        logger.info("Creating HTML document");

        Document doc = Jsoup.parse("<!DOCTYPE html><html><head><title>VK EPC File</title></head><body></body></html>");

        // Добавляем стили
        Element head = doc.head();
        head.appendElement("style")
                .text("body { font-family: Arial, sans-serif; margin: 20px; } " +
                        ".section { margin: 10px 0; padding: 10px; border: 1px solid #ccc; } " +
                        "h2 { color: #333; } " +
                        "table { width: 100%; border-collapse: collapse; } " +
                        "th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }");

        Element body = doc.body();

        // Заголовок
        body.appendElement("h1").text("VK EPC Document");

        // Секция с информацией из запроса
        Element requestSection = body.appendElement("div").addClass("section");
        requestSection.appendElement("h2").text("Request Information");
        requestSection.appendElement("p").text("Order ID: " + request.getOrderId());
        requestSection.appendElement("p").text("Exercise ID: " + request.getExerciseId());
        requestSection.appendElement("p").text("KNS: " + request.getKNS());
        requestSection.appendElement("p").text("Billing Account: " + request.getBillingAccount());
        requestSection.appendElement("p").text("Time: " + request.getTime());

        // Секция с данными из Oracle
        Element oracleSection = body.appendElement("div").addClass("section");
        oracleSection.appendElement("h2").text("Oracle Database Data");
        oracleSection.appendElement("pre").text(oracleData);

        // Секция с данными из PostgreSQL
        Element postgresSection = body.appendElement("div").addClass("section");
        postgresSection.appendElement("h2").text("PostgreSQL Database Data");
        postgresSection.appendElement("pre").text(postgresData);

        // Секция с JSON данными
        Element jsonSection = body.appendElement("div").addClass("section");
        jsonSection.appendElement("h2").text("JSON Data");
        jsonSection.appendElement("pre").text(jsonNode.toPrettyString());

        // Секция с обработанными EPC параметрами
        Element epcSection = body.appendElement("div").addClass("section");
        epcSection.appendElement("h2").text("Processed EPC Parameters");
        epcSection.appendElement("pre").text(processedEpcParams);

        // Секция с содержимым файлов
        Element filesSection = body.appendElement("div").addClass("section");
        filesSection.appendElement("h2").text("File Contents");
        filesSection.appendElement("pre").text(fileContent);

        return doc.html();
    }
}