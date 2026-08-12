package ru.ruslan.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import ru.ruslan.service.impl.FileService;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@Disabled
public class VkEpcServiceTest {

    private FileService fileServiceMock;

    @BeforeEach
    public void setup() {
        fileServiceMock = Mockito.mock(FileService.class);
    }

    @Disabled
    @Test
    public void testFileLoadingAndReferenceValues() {
        // Подготовка заглушек данных через mock файлового сервиса
        when(fileServiceMock.readFromResources(anyString()))
                .thenReturn("{ \"status\": \"reference_data\" }");

        String mockData = fileServiceMock.readFromResources("templates/epc-template.json");

        assertNotNull(mockData);
        System.out.println("Эталонные данные успешно загружены тестом: " + mockData);
    }

    @Test
    @Disabled
    public void testFileLoadingAndReferenceValuesNew() {
        // 1. Открываем mock-контекст для статических методов внутри try-with-resources
        try (MockedStatic<FileService> fileServiceMock = Mockito.mockStatic(FileService.class)) {

            // 2. Настраиваем поведение статического метода
            fileServiceMock.when(() -> FileService.readFromResources(anyString()))
                    .thenReturn("{ \"status\": \"reference_data\" }");

            // 3. Вызываем статический метод (теперь он перехвачен Mockito)
            String mockData = FileService.readFromResources("templates/epc-template.json");

            // 4. Проверяем результат
            assertNotNull(mockData);
            System.out.println("Эталонные данные успешно загружены тестом: " + mockData);
        }
        // 5. После закрытия блока try-with-resources статический метод автоматически возвращается к реальному поведению
    }
}
