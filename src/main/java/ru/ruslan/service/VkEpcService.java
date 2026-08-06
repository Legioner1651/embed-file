package ru.ruslan.service;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.ruslan.controller.VkEpcController.VkEpcRequest;
import ru.ruslan.controller.VkEpcController.VkEpcResponse;
import ru.ruslan.service.impl.JsonNodeService;

import ru.ruslan.service.impl.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class VkEpcService {

    // Внедряем зависимости через конструктор (благодаря Lombok @RequiredArgsConstructor)
    private final FileService fileService;
    private final RestClientService restClientService;
    private final JsonNodeService jsonNodeService;
    private final JavaScriptService javaScriptService;
    private final JsoupService jsoupService;

    public VkEpcResponse processEpcData(VkEpcRequest request) {
        log.info("Запуск обработки логики для orderId: {}", request.getOrderId());

        // Исправлено: Вызов через экземпляр jsonNodeService (с маленькой буквы)
        JsonNode inputParams = jsonNodeService.parseJsonNode(request.getEpcParams());

        jsonNodeService.printTemplateForEPC(inputParams);

        // Исправлено: Вызов через экземпляр restClientService и передача параметров из инстанса jsonNodeService
        JsonNode calcResult = restClientService.getProdBackCalculateFindByBankbookEIP(
                request.getBillingAccount(),
                jsonNodeService.getOrponCode(inputParams)
        );

        // Исправлено: Модифицируем JSON, используя методы экземпляра jsonNodeService
        JsonNode resultsNode = jsonNodeService.getResultsFromResponse(calcResult);
        jsonNodeService.deleteProperties(resultsNode);
        jsonNodeService.changeValue2EmptyArray(inputParams, "properties");

        // Исправлено: Преобразуем в строку через экземпляр сервиса
        String finalPayload = jsonNodeService.jsonNodeToString(inputParams, true);

        // Записываем результат в файловую систему
        fileService.writeToFileSystem(request.getPathCreateFileVK(), finalPayload);

        VkEpcResponse response = new VkEpcResponse();
        response.setStatus("SUCCESS");
        response.setMessage("Файл успешно эмбеддирован и сохранен по пути " + request.getPathCreateFileVK());
        return response;
    }
}
