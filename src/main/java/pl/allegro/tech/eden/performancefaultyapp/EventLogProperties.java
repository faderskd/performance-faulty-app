package pl.allegro.tech.eden.performancefaultyapp;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "event-log")
public class EventLogProperties {
    private String logFilePath = "/playground/log.txt";

    String getLogFilePath() {
        return logFilePath;
    }

    void setLogFilePath(String logFilePath) {
        this.logFilePath = logFilePath;
    }
}
