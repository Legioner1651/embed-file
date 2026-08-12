package ru.ruslan.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.ruslan.controller.VkEpcController.VkEpcRequest;
import ru.ruslan.service.impl.FileService;
import ru.ruslan.service.impl.JsonNodeService;
import ru.ruslan.service.impl.RestClientService;

import java.time.Instant;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest // Запускает полное приложение со всеми реальными бинами
@AutoConfigureMockMvc // Настраивает MockMvc для отправки HTTP-запросов в реальный контекст
class VkEpcIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // Внедряем реальный FileService из контекста приложения
    @Autowired
    private FileService fileService;

    @Autowired
    private JsonNodeService jsonNodeService;

    // Внедряем заглушку внешнего сервиса embed-file в контекст Spring Boot 4
    @MockitoBean
    private RestClientService restClientService;

    @Value("${endpoints.embed-file}")
    private String endpoint;

    @Test
    @DisplayName("Интеграционный тест: проверка сквозного прохождения запроса через реальный сервис")
    void processVkFiles_ReturnsActualServiceResponse() throws Exception {

        String epcParams = FileService.readFromResources("EpcParamsZenin1.json");

        // Мокируем ответ метода getProdBackCalculateFindByBankbookEIP
        String profileResponseZenin1 = fileService.readFromResources("ProfileResponseZenin1.json");
        JsonNode mockResponseZenin1 = jsonNodeService.parseJsonNode(profileResponseZenin1);
        Mockito.when(restClientService.getProdBackCalculateFindByBankbookEIP("850018744815", "32617698"))
                .thenReturn(mockResponseZenin1);

        // Arrange (Готовим реальные данные для запроса)
        VkEpcRequest request = new VkEpcRequest();
        request.setPathNewFileVK("/home/ruslan/Documents/vkepc/");
        request.setTextEpcParams(epcParams);
        request.setExerciseId("123456789");
        request.setOrderId("456789");
        request.setKNS("KNS-1789");
        request.setBillingAccount("1234567890");
        request.setTime(Instant.now());

        // Act & Assert
        mockMvc.perform(post(endpoint)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                // Здесь укажите те значения, которые ваш реальный VkEpcService
                // возвращает при обработке этого файла:
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Файл с именем " + request.getKNS() + "VK.html" + " успешно создан и сохранен по пути " + request.getPathNewFileVK()));
    }
}
