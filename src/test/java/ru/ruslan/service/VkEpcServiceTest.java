package ru.ruslan.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.ruslan.model.CreateFileVKRequest;
import ru.ruslan.model.CreateFileVKResponse;
import ru.ruslan.service.impl.*;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VkEpcServiceTest {

//    @Mock
//    private OracleDbService oracleDbService;
//
//    @Mock
//    private PostgreSqlDbService postgreSqlDbService;

    @Mock
    private RestClientService restClientService;

    @Mock
    private JavaScriptService javaScriptService;

    @Mock
    private JsonProcessingService jsonProcessingService;

    @Mock
    private HtmlProcessingService htmlProcessingService;

    @InjectMocks
    private VkEpcService vkEpcService;

    @Test
    void testProcessRequest_Success() {
        // Arrange
        CreateFileVKRequest request = new CreateFileVKRequest();
        request.setOrderId("ORDER123");
        request.setExerciseId("EXERCISE456");
        request.setKns("KNS-11789");
        request.setBillingAccount("BA001");
        request.setTime(Instant.parse("2024-01-01T12:00:00"));
        request.setEpcParams("{}");
        request.setPathFileVK("/tmp/test");

//        when(oracleDbService.getExerciseData(anyString())).thenReturn("Oracle Data");
//        when(postgreSqlDbService.getBillingData(anyString())).thenReturn("PostgreSQL Data");
        when(restClientService.fetchExternalData(anyString())).thenReturn("{}");
        when(restClientService.readFilesFromPath(anyString())).thenReturn("File Content");

        // Act
        CreateFileVKResponse response = vkEpcService.processRequest(request);

        // Assert
        assertNotNull(response);
        assertEquals("SUCCESS", response.getStatus());
        assertNotNull(response.getHtmlContent());
        assertEquals("vk_epc_ORDER123.html", response.getFileName());
    }

    @Test
    void testProcessRequest_Error() {
        // Arrange
        CreateFileVKRequest request = new CreateFileVKRequest();
        request.setOrderId("ORDER123");

//        when(oracleDbService.getExerciseData(anyString()))
//                .thenThrow(new RuntimeException("Database error"));

        // Act
        CreateFileVKResponse response = vkEpcService.processRequest(request);

        // Assert
        assertNotNull(response);
        assertEquals("ERROR", response.getStatus());
        assertTrue(response.getMessage().contains("Failed to process request"));
    }
}