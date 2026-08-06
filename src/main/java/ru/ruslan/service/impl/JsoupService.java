package ru.ruslan.service.impl;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

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
        log.info("Структура документа HTML:\n{}", sb.toString());
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
}
