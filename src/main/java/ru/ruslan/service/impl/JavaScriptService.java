package ru.ruslan.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

@Service
public class JavaScriptService {
    private static final Logger logger = LoggerFactory.getLogger(JavaScriptService.class);

    public String processEpcParams(String epcParams) {
        logger.info("Processing EPC params with JavaScript engine");

        Context cx = Context.enter();
        try {
            Scriptable scope = cx.initStandardObjects();

            // Добавляем параметры в JavaScript контекст
            scope.put("epcParams", scope, epcParams);

            // JavaScript код для обработки параметров
            String script =
                    "function processParams(params) {" +
                            "    try {" +
                            "        var parsed = JSON.parse(params);" +
                            "        // Обработка параметров" +
                            "        return JSON.stringify(parsed);" +
                            "    } catch(e) {" +
                            "        return '{}';" +
                            "    }" +
                            "}" +
                            "processParams(epcParams);";

            Object result = cx.evaluateString(scope, script, "EPCProcessor", 1, null);
            return Context.toString(result);
        } catch (Exception e) {
            logger.error("Error processing EPC params with JavaScript", e);
            return "{}";
        } finally {
            Context.exit();
        }
    }
}