package pl.allegro.tech.eden.performancefaultyapp;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "event-log")
public class EventLogProperties {
    private String logFilePath = "log.txt";
    private int maxEventCount = 1000;

    String getLogFilePath() {
        return logFilePath;
    }

    void setLogFilePath(String logFilePath) {
        this.logFilePath = logFilePath;
    }

    public int getMaxEventCount() {
        return maxEventCount;
    }

    public void setMaxEventCount(int maxEventCount) {
        this.maxEventCount = maxEventCount;
    }
}
