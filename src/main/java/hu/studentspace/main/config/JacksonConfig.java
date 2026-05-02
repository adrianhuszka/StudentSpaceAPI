package hu.studentspace.main.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        // Automatically discover and register Jackson modules present on the classpath
        mapper.findAndRegisterModules();
        return mapper;
    }
}

