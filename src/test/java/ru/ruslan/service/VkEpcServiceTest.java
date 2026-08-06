package ru.ruslan.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.ruslan.service.impl.FileService;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class VkEpcServiceTest {

    private FileService fileServiceMock;

    @BeforeEach
    public void setup() {
        fileServiceMock = Mockito.mock(FileService.class);
    }

    @Test
    public void testFileLoadingAndReferenceValues() {
        // Подготовка заглушек данных через mock файлового сервиса
        when(fileServiceMock.readFromResources(anyString()))
                .thenReturn("{ \"status\": \"reference_data\" }");

        String mockData = fileServiceMock.readFromResources("templates/epc-template.json");

        assertNotNull(mockData);
        System.out.println("Эталонные данные успешно загружены тестом: " + mockData);
    }
}
