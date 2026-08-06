package ru.ruslan.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import java.util.TimeZone;

@Configuration
public class JacksonConfig {

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();

        // Регистрируем модуль поддержки современных Java 8+ дат (включая Instant)
        mapper.registerModule(new JavaTimeModule());

        // Устанавливаем единую временную зону для приложения
        mapper.setTimeZone(TimeZone.getTimeZone("UTC"));

        return mapper;
    }
}
