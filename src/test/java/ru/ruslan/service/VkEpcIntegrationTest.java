package ru.ruslan.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;

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

    // ЗАМЕНА: вместо @Autowired используем @MockitoBean для создания мока в контексте Spring 4
    @MockitoBean
    private FileService fileService;

    @Autowired
    private JsonNodeService jsonNodeService;

    // Внедряем заглушку внешнего сервиса embed-file в контекст Spring Boot 4
    @MockitoBean
    private RestClientService restClientService;

    @Value("${endpoints.embed-file}")
    private String endpoint;

    // Объявляем каптор для захвата второго аргумента (содержимого файла)
    @Captor
    private ArgumentCaptor<String> contentCaptor;

    @Test
    @DisplayName("Интеграционный тест: проверка сквозного прохождения запроса через реальный сервис")
    void processVkFiles_ReturnsActualServiceResponse() throws Exception {

        String epcParams = FileService.readFromResources("EpcParamsZenin1.json");
        String resultFile = FileService.readFromResources("VK_Zenin1.html");

        // Мокируем ответ метода getProdBackCalculateFindByBankbookEIP
        String profileResponseZenin1 = FileService.readFromResources("ProfileResponseZenin1.json");
        JsonNode mockResponseZenin1 = jsonNodeService.parseJsonNode(profileResponseZenin1);
        Mockito.when(restClientService.getProdBackCalculateFindByBankbookEIP("850018744815", "32617698"))
                .thenReturn(mockResponseZenin1);

        // КЛЮЧЕВОЙ ШАГ: Мокаем void-метод записи в файловую систему, чтобы он ничего не делал
        Mockito.doNothing().when(fileService).writeToFileSystem(Mockito.anyString(), Mockito.anyString());

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

        // Assert: Проверяем факт вызова метода и захватываем второй аргумент
        // Первый аргумент игнорируем с помощью anyString(), во второй передаем каптор
        Mockito.verify(fileService, Mockito.times(1))
                .writeToFileSystem(anyString(), contentCaptor.capture());

        // Получаем строку, которая была передана в метод во время работы VkEpcService
        String actualContent = contentCaptor.getValue();

        // Делаем любые необходимые проверки содержимого (используя AssertJ)
        assertThat(actualContent)
                .isEqualToNormalizingWhitespace(resultFile)
                .isEqualToNormalizingNewlines(resultFile);
    }
}
