package pl.allegro.tech.eden.performancefaultyapp;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class EventLogConfiguration {

    @Bean
    EventLog eventLog() throws IOException {
        return new SimpleEventLog();
    }
}
