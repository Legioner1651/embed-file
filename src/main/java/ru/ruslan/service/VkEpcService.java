package ru.ruslan.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

import ru.ruslan.controller.VkEpcController.VkEpcRequest;
import ru.ruslan.controller.VkEpcController.VkEpcResponse;
import ru.ruslan.exception.StopExecutionException;
import ru.ruslan.service.impl.JsonNodeService;

import ru.ruslan.service.impl.*;

import java.time.Instant;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class VkEpcService {

    // Внедряем зависимости через конструктор (благодаря Lombok @RequiredArgsConstructor)
    private final FileService fileService;
    private final JavaScriptService javaScriptService;
    private final JsonNodeService jsonNodeService;
    private final JsoupService jsoupService;
    private final RestClientService restClientService;

    public VkEpcResponse processEpcData(VkEpcRequest request) {

        log.info("***** Старт сценария создания файла встраивания *****");

        // Получение JSON-параметрами (передается в jsonNodeService для парсинга)
        String textEpcParams = request.getTextEpcParams();

        // Получение простых строк
        String nameFileVK = request.getKNS() + "VK.html";
        String pathNewFileVK = request.getPathNewFileVK() + nameFileVK;
        String orderId = request.getOrderId();
        String exerciseId = request.getExerciseId();
        String billingAccount = request.getBillingAccount();

        // Получение объекта даты/времени
        Instant requestTime = request.getTime();


        log.debug("Поступил запрос с параметрами:");
        log.trace("путь до нового файла встраивания: {}", pathNewFileVK);
        log.trace("epcParams:\n{}\n", textEpcParams);
        log.trace("номер задания:             {}", exerciseId);
        log.trace("номер заказа:              {}", orderId);
        log.trace("номер Лицевого счета:      {}", billingAccount);
        log.trace("Дата и время:              {}", requestTime);

        // 1.1. Загрузка файла Template.html в templateHTML (org.jsoup.nodes.Document).
        log.debug("-".repeat(86) + "1.1");
        log.info("1.1. Загрузка файла Template.html в templateHTML (org.jsoup.nodes.Document)");
        String textTemplate = FileService.readFromResources("Template.html");
        Document templateHTML = JsoupService.getJsoupNodesDocumentFromString(textTemplate);
        log.trace("templateHTML =\n{}\n", templateHTML);

        // 1.2. Получение absolutePath (путь к файлу встраивания)
        log.debug("-".repeat(86) + "1.2");
        log.info("1.2. Получение absolutePath (путь к файлу встраивания)");

        // Валидация пути
        if (pathNewFileVK.isEmpty() || pathNewFileVK == null || pathNewFileVK.equals("")) {
            throw new StopExecutionException("Путь не должен быть пустым");
        } else if (!pathNewFileVK.startsWith("/")) {
            throw new StopExecutionException("Путь к файлу не является абсолютным");
        } else if (!pathNewFileVK.toLowerCase().endsWith(".html")) {
            throw new StopExecutionException("Файл должен иметь расширение .html");
        }
        log.debug("absolutePath: {}", pathNewFileVK);

        // 1.3. Получение epcParams (String) - ответ запроса /epcParams
        log.debug("-".repeat(86) + "1.3");
        log.info("1.3. Получение конфигурации параметров встраивания ВК ЕПК - (ответ запроса /epcParams) -> epcParams (String) ");
        String epcParams = getStringEpcParams(textEpcParams);
        log.debug("epcParams (Данные ЕПК из Kibana):\n{}\n", epcParams);

        // 2.1. Получение scriptContent (String) - содержимое первого блока <script> из templateHTML
        log.debug("-".repeat(86) + "2.1");
        log.info("2.1. Получение scriptContent (String) - содержимое первого блока <script> из templateHTML");

        if (templateHTML == null) {
            System.err.println("Ошибка: HTML-документ не инициализирован.");
            throw new StopExecutionException("Документ равен null.");
        }

        // получаем <body> внутри templateHTML
        Element body = templateHTML.body();
        if (body == null) {
            System.err.println("Ошибка: Тело HTML-документа (body) отсутствует.");
            throw new StopExecutionException("Тело HTML не найдено.");
        }

        // получаем первый тег <script> внутри <body> templateHTML
        Element scriptFirst = body.selectFirst("script");

        if (scriptFirst == null) {
            System.err.println("Ошибка: Элемент <script> не найден. Приложение остановлено.");
            throw new StopExecutionException("Элемент <script> не найден.");
        }

        // получаем содержимое 1-го блока <script> в templateHTML
        String scriptContent = scriptFirst.data();
        log.debug("scriptContent (String) - содержимое первого блока <script> из файла Template.html):\n{}\n", scriptContent);

        // 2.2. Минифицирование scriptContent (String) (содержимое блока <script>) через создание rhino.Node jsAst
        log.debug("-".repeat(86) + "2.2");
        log.info("2.2. Минифицирование scriptContent (String) (содержимое блока <script>) через создание rhino.Node jsAst");
        com.google.javascript.rhino.Node jsAst = javaScriptService.parseJsCode(scriptContent);
        scriptContent = javaScriptService.toJsCode(jsAst, false);
        log.debug("scriptContent (minified):\n{}\n", scriptContent);

        // 2.3. Получение minified epcParamsJson (String) из epcParams после проверки и редактирования под структуру файла ВК
        log.debug("-".repeat(86) + "2.3");
        log.info("2.3. Получение minified epcParamsJson (String) из epcParams после проверки и редактирование под структуру файла ВК");

        // блок проверки на отсутствие в epcParams \\\ и замена \\ на \
        if (epcParams.contains("\\\\\\") || epcParams.contains("\\\\\\\\") || epcParams.contains("\\\\\\\\\\")) {
            throw new StopExecutionException("epcParams имеет недопустимое количество обратных слешей.");
        } else if (epcParams.contains("\\\\")) {
            epcParams = epcParams.replace("\\\\", "\\");
        }

        // Блок парсинга в объект JsonNode и помещение элемента "productOfferCfg" в массив если массив отсутствует
        JsonNode epcParamsJsonNode = jsonNodeService.parseJsonNode(epcParams);  // epcParamsJsonNode - JSON объект из epcParams

        // Блок проверки, что значение элемента "productOfferCfg" - массив, в epcParamsJsonNode
        if (epcParamsJsonNode instanceof ObjectNode objectNode) {
            if (objectNode.has("productOfferCfg")) {
                JsonNode oldNode = objectNode.get("productOfferCfg");
                // Если "productOfferCfg" = null, заменяем его на пустой массив
                if (oldNode.isNull()) {
                    objectNode.putArray("productOfferCfg");
                    // Если "productOfferCfg" уже массив, оставляем как есть
                } else if (!oldNode.isArray()) {
                    // Создаём новый пустой массив
                    ArrayNode arrayNode = objectNode.putArray("productOfferCfg");
                    // Добавляем в него старое значение
                    arrayNode.add(oldNode);
                }
            }
        }

        String epcParamsJson = jsonNodeService.jsonNodeToString(epcParamsJsonNode, false);
        log.debug("epcParamsJson (minified) в значении элемента \"productOfferCfg\" - массив:\n{}\n", epcParamsJson);

        // 2.4. Получение параметров из epcParamsJsonNode - распарсенной конфигурации параметров встраивания ВК ЕПК
        log.debug("-".repeat(86) + "2.4");
        log.info("2.4. Получение параметров из epcParamsJsonNode - распарсенной конфигурации параметров встраивания ВК ЕПК");

        /** Лицевые счета */
        List<String> billAccounts = jsonNodeService.getAccounts(epcParamsJsonNode);
        log.debug("billAccounts = {}", billAccounts);
        /** ОРПОН код адреса абонента */
        String orponCode = jsonNodeService.getOrponCode(epcParamsJsonNode);
        log.debug("orponCode = {}", orponCode);
        /** Адрес абонента */
        String address = jsonNodeService.getOrponAddress(epcParamsJsonNode);
        log.debug("address = {}", address);
        /** Бизнес процесс */
        String typeOperation = jsonNodeService.getTypeOperation(epcParamsJsonNode);
        log.debug("typeOperation = {}", typeOperation);
        /** Используемая технология */
        String technology = jsonNodeService.getTechnology(epcParamsJsonNode);
        log.debug("technology = {}", technology);

        // 2.5. Редактирование epcParamsJsonNode: создание и заполнение "clientProfile", очистка массива "clientProperties".
        log.debug("-".repeat(86) + "2.5");
        log.info("2.5. Создание и заполнение \"clientProfile\", очистка массива \"clientProperties\"");

        // Проверка возможности выполнения запроса PROD_ОВ Find by bankbook EIP
        if (billAccounts != null && !billAccounts.isEmpty() && orponCode != null && typeOperation.equals("УУ")) {
            log.debug("Приступаем к созданию и заполнению clientProfile");

            /** Выполняем запрос PROD_ОВ Find by bankbook EIP */
            // Для отладки
//            String pathResponse = "/home/GD/ruslan.petrov/Документы/Embed_files/Template/Zenin_response.json";
//            String bodyResponseFromOB = JsonNodeUtils.readFileToString(pathResponse);
//            JsonNode bodyResponseOBJsonNode = JsonNodeUtils.parseJsonNode(bodyResponseFromOB);
//            if (DEBUG_LEVEL >= 2) { System.out.println("-".repeat(60)); }
//            if (DEBUG_LEVEL >= 2) { System.out.println("bodyResponseOB = \n" + bodyResponseOBJsonNode); }
//            if (DEBUG_LEVEL >= 2) { System.out.println("-".repeat(60)); }

            // Берем 1-ый элемент, что делать если элементов более 1-го в инструкции не сказано.
            JsonNode bodyResponseOBJsonNode = restClientService.getProdBackCalculateFindByBankbookEIP(billAccounts.get(0), orponCode);

            if (bodyResponseOBJsonNode != null) {
                JsonNode resultsOB = jsonNodeService.getResultsFromResponse(bodyResponseOBJsonNode);

                log.trace("-".repeat(60));
                log.debug("resultsOB (значение элемента results полученного из response запроса PROD_ОВ) = \n{}\n", jsonNodeService.jsonNodeToString(resultsOB, true));
                log.trace("-".repeat(60));

                /**  Удалить параметр "properties"  */
//                log.trace("-".repeat(60));
//                log.trace("results after delete \"properties\" (before) = \n{}\n", jsonNodeService.jsonNodeToString(resultsOB, true));
                jsonNodeService.deleteProperties(resultsOB);
                log.trace("-".repeat(60));
                log.trace("results after delete \"properties\" = \n{}\n", jsonNodeService.jsonNodeToString(resultsOB, true));
                log.trace("-".repeat(60));

                /** Вставить профиль абонента в параметр "clientProfile"  */
                jsonNodeService.changeValue(epcParamsJsonNode, "clientProfile", resultsOB);
                log.trace("-".repeat(60));
                log.trace("epcParamsJsonNode after insertion value of \"clientProfile\" = \n{}\n", jsonNodeService.jsonNodeToString(epcParamsJsonNode, true));
                log.trace("-".repeat(60));

                /**  Удалить значение из параметра "clientProperties"  */
                jsonNodeService.changeValue2EmptyArray(epcParamsJsonNode, "clientProperties");
                log.trace("-".repeat(60));
                log.trace("epcParamsJsonNode after delete \"clientProperties\" = \n{}\n", jsonNodeService.jsonNodeToString(epcParamsJsonNode, true));
                log.trace("-".repeat(60));

                epcParamsJson = jsonNodeService.jsonNodeToString(epcParamsJsonNode, false);
                log.trace("-".repeat(60));
                log.debug("epcParamsJson = (minified) epcParamsJsonNode = \n{}\n", epcParamsJson);
                log.trace("-".repeat(60));
            } else {
                log.debug("Результат запроса PROD_ОВ = null => объект resultsOB не обрабатываем");
            }
        } else {
            log.debug("Запрос \"PROD_ОВ Find by bankbook EIP\" не выполнялся, отсутствует либо ЛС, либо код ОРПОН");
        }

        // 2.6. Вставка minified элементов epcParamsJson в minified scriptContent (String) => pretty scriptContent.
        log.debug("-".repeat(86) + "2.6");
        log.info("2.6. Вставка minified элементов epcParamsJson в minified scriptContent (String) => pretty scriptContent");

        scriptContent = scriptContent.replace("\"ready\":true", epcParamsJson.substring(1, epcParamsJson.length() - 1));
        log.debug("scriptContent (minified):\n{}\n", scriptContent);

        // прогоняем через объект rhino.Node scriptContentAst, чтобы получить minified код JS
        com.google.javascript.rhino.Node scriptContentAst = javaScriptService.parseJsCode(scriptContent);
        scriptContent = javaScriptService.toJsCode(scriptContentAst, true);
        log.debug("scriptContent (pretty):\n" + scriptContent);

        // 3.1. Замена блока <script> содержимым переменной pretty scriptContent (String)
        log.debug("-".repeat(86) + "3.1");
        log.info("3.1. Замена блока <script> содержимым переменной pretty scriptContent (String)");
        scriptFirst.empty();
        scriptFirst.appendChild(new DataNode(scriptContent));

        // 3.2. Сохранение содержимого templateHTML в файл absolutePath
//        absolutePath = "/home/GD/ruslan.petrov/Документы/Embed_files/Template/Results.html";    // Test
        log.debug("-".repeat(86) + "3.2");
        log.info("3.2. Сохранение содержимого templateHTML в файл absolutePath");
        // document.outerHtml() возвращает строковое представление HTML-кода
        FileService.writeToFileSystem(pathNewFileVK, templateHTML.outerHtml());

        log.trace("Структура DOM:");
        jsoupService.logStructure(templateHTML);


        // 3.3. Подготовка информации для обращения в ЕПК
        log.debug("-".repeat(86) + "3.3");
        log.info("3.3. Подготовка информации для обращения в ЕПК");

        log.info("------------------------------------------------");
        log.info("Бизнес процесс: " + typeOperation);
        log.info("Адрес абонента: GlobalID = " + orponCode + ", " + address);
        log.info("Используемая технология: " + technology);
        log.info("Ожидаемое ПП: ");
        log.info("Лицевые счета: " + billAccounts);
        log.info("------------------------------------------------");


//        // Исправлено: Вызов через экземпляр jsonNodeService (с маленькой буквы)
//        JsonNode inputParams = jsonNodeService.parseJsonNode(request.getTextEpcParams());
//
//        jsonNodeService.printTemplateForEPC(inputParams);
//
//        // Исправлено: Вызов через экземпляр restClientService и передача параметров из инстанса jsonNodeService
//        JsonNode calcResult = restClientService.getProdBackCalculateFindByBankbookEIP(
//                request.getBillingAccount(),
//                jsonNodeService.getOrponCode(inputParams)
//        );
//
//        // Исправлено: Модифицируем JSON, используя методы экземпляра jsonNodeService
//        JsonNode resultsNode = jsonNodeService.getResultsFromResponse(calcResult);
//        jsonNodeService.deleteProperties(resultsNode);
//        jsonNodeService.changeValue2EmptyArray(inputParams, "properties");
//
//        // Исправлено: Преобразуем в строку через экземпляр сервиса
//        String finalPayload = jsonNodeService.jsonNodeToString(inputParams, true);
//
//        // Записываем результат в файловую систему
//        fileService.writeToFileSystem(request.getPathNewFileVK(), finalPayload);

        VkEpcResponse response = new VkEpcResponse();
        response.setStatus("SUCCESS");
//        response.setMessage("Файл успешно эмбеддирован и сохранен по пути " + request.getPathNewFileVK());
        response.setMessage("Файл с именем " + nameFileVK + " успешно создан и сохранен по пути " + request.getPathNewFileVK());

        log.info("***** Окончание сценария создания файла встраивания *****");

        return response;
    }

    /**
     * Проверяется структура текста, если будет ссылка на файл то загружается содержимое файла.
     * На выходе JSON в формате строки.
     * @param epcParams исходный текст для ЕПК
     */
    public static String getStringEpcParams(String epcParams) {
        int DEBUG_LEVEL = 0;

        if (epcParams == null || epcParams.trim().isEmpty() || epcParams.equals("")) {
            throw new StopExecutionException("в epcParams нет данных");
        }

        epcParams = epcParams.trim();

        // если текст является URL адресом файла, тогда читаем файл
        if (FileService.isPathValid(epcParams)) {                                        // проверка на URL

            epcParams = FileService.readFromFileSystem(epcParams);

            if (epcParams == null || epcParams.trim().isEmpty() || epcParams.equals("")) {
                throw new StopExecutionException("getStringEpcParams: в прочитанном файле нет данных");
            }

            epcParams = epcParams.trim();
        }

        // 2. Если текст начинается с "{ и заканчивается на }"
        if ((epcParams.startsWith("\"{") && epcParams.endsWith("}\"")) || (epcParams.startsWith("'{") && epcParams.endsWith("}'"))) {
            epcParams = epcParams.substring(1, epcParams.length() - 1);
            return epcParams;
        }

        // 3. Если текст начинается с { и заканчивается на }
        if (epcParams.startsWith("{") && epcParams.endsWith("}")) {
            return epcParams;
        }

        // 4. Если текст не начинается с { и не заканчивается на }
        try {
            String epcParamsTemp = "{" + epcParams + "}";
            new ObjectMapper().readTree(epcParamsTemp);
            if (DEBUG_LEVEL >= 2) { System.out.println("getStringEpcParams: текст - JSON-строка без фигурных скобок"); }
            return epcParamsTemp;
        } catch (JsonProcessingException e) {
            System.err.println("Строка не является JSON объектом");
            e.printStackTrace();
        }

        throw new StopExecutionException("getEpcParams: текст не содержит документ нужного формата");
    }


}
