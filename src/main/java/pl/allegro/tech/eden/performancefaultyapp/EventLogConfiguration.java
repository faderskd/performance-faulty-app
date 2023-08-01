package pl.allegro.tech.eden.performancefaultyapp;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
@EnableConfigurationProperties(EventLogProperties.class)
public class EventLogConfiguration {

    @Bean
    EventLog eventLog(EventLogProperties properties) throws IOException {
        return new MMapEventLog(properties);
    }
}
