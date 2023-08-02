package pl.allegro.tech.eden.performancefaultyapp;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EventLogTest {

    @ParameterizedTest
    @MethodSource("eventLogStream")
    public void basicTest(EventLog eventLog) {
        List<StoreEventResult> storedEvents = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            StoreEventResult event = eventLog.store(new Event("event" + i));
            storedEvents.add(event);
        }

        for (StoreEventResult e : storedEvents) {
            assertEquals("event" + e.offset(), eventLog.get(e.offset()).content());
        }
    }

    static Stream<EventLog> eventLogStream() throws IOException {
        return Stream.of(
                new MMapEventLog(generateProperties("mmap_log")),
                new SimpleEventLog(generateProperties("simple_log")),
                new SuperFastEventLog(generateProperties("superFast_log"))
        );
    }

    private static EventLogProperties generateProperties(String logFileName) throws IOException {
        EventLogProperties prop = new EventLogProperties();
        prop.setLogFilePath(Files.createTempFile(logFileName, "").toFile().getAbsolutePath());
        prop.setMaxEventCount(1000000);
        return prop;
    }
}
