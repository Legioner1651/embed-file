package ru.ruslan.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean; // Новый импорт вместо прежнего MockBean
import org.springframework.test.web.servlet.MockMvc;
import ru.ruslan.controller.VkEpcController;
import ru.ruslan.controller.VkEpcController.VkEpcRequest;
import ru.ruslan.controller.VkEpcController.VkEpcResponse;
import ru.ruslan.service.VkEpcService;

import java.time.Instant;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Disabled
@WebMvcTest(VkEpcController.class)
class VkEpcControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // В Spring Boot 4.0 используется строго эта аннотация
    @MockitoBean
    private VkEpcService vkEpcService;

    @Test
    @Disabled
    @DisplayName("Успешная обработка валидного запроса")
    void processVkFiles_ReturnsValidResponse() throws Exception {
        // Arrange
        VkEpcRequest request = new VkEpcRequest();
        request.setPathNewFileVK("/home/ruslan/Documents/vkepc/KNS-789.html");
        request.setTextEpcParams("param1=value1");
        request.setExerciseId("ex-123");
        request.setOrderId("order-456");
        request.setKNS("KNS-789");
        request.setBillingAccount("ACC-000");
        request.setTime(Instant.now());

        VkEpcResponse expectedResponse = new VkEpcResponse();
        expectedResponse.setStatus("SUCCESS");
        expectedResponse.setMessage("Файл успешно обработан");

        Mockito.when(vkEpcService.processEpcData(any(VkEpcRequest.class)))
                .thenReturn(expectedResponse);

        // Act & Assert
        mockMvc.perform(post("/api/vk/files")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Файл успешно обработан"));
    }

    @Test
    @Disabled
    @DisplayName("Ошибка 400 Bad Request при пустых обязательных полях")
    void processVkFiles_ReturnsBadRequest_WhenFieldsAreBlank() throws Exception {
        // Arrange
        VkEpcRequest invalidRequest = new VkEpcRequest();

        // Act & Assert
        mockMvc.perform(post("/api/vk/files")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }
}
