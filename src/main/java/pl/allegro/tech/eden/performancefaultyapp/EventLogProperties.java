package pl.allegro.tech.eden.performancefaultyapp;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "event-log")
public class EventLogProperties {
    private String logFilePath = "large.txt";
    private int maxEventCount = 2000000;
    private boolean writeFromBegin = true;

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

    public boolean isWriteFromBegin() {
        return writeFromBegin;
    }

    public void setWriteFromBegin(boolean writeFromBegin) {
        this.writeFromBegin = writeFromBegin;
    }
}
