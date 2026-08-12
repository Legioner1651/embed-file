package ru.ruslan.service.impl;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
@Service
@RequiredArgsConstructor
public class JsoupService {

    private final FileService fileServiceInstance;
    private static FileService fileService;

    @PostConstruct
    private void init() {
        fileService = this.fileServiceInstance;
    }

    public static void logStructure(Document doc) {
        StringBuilder sb = new StringBuilder();
        if (doc.body() != null) {
            buildTagTree(doc.body(), sb, 0);
        }
        log.trace("Структура документа HTML:\n{}", sb.toString());
    }

    private static void buildTagTree(Element element, StringBuilder sb, int depth) {
        sb.append("  ".repeat(depth))
                .append("<")
                .append(element.tagName())
                .append(">")
                .append("\n");
        for (Element child : element.children()) {
            buildTagTree(child, sb, depth + 1);
        }
    }

    /**
     * Reads a resource file from the classpath (typically src/main/resources)
     * and returns its content as an org.jsoup.nodes.Document.
     *
     * @param html the name of the resource file (e.g., "config.txt" or "data/file.json")
     * @return the content of the resource file as a String
     * @throws IllegalArgumentException if the resource file is not found
     * @throws RuntimeException         if an I/O error occurs while reading the file
     */
    public static Document getJsoupNodesDocumentFromString(String html) {
        if (html == null) {
            throw new IllegalArgumentException("Файл не найден в resources!");
        }
        // Парсим InputStream напрямую
        Document jsoupNodesDocument = Jsoup.parse(html, StandardCharsets.UTF_8.name());
        // Настраиваем компактный вывод
        jsoupNodesDocument.outputSettings().prettyPrint(false).indentAmount(0);

        return jsoupNodesDocument;
    }
}
