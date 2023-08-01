package pl.allegro.tech.eden.performancefaultyapp;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.nio.file.Files;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EventLogTest {

    @ParameterizedTest
    @MethodSource("eventLogStream")
    public void basicTest(EventLog eventLog) {
        // when
        StoreEventResult event1 = eventLog.store(new Event("event1"));
        StoreEventResult event2 = eventLog.store(new Event("event2"));
        StoreEventResult event3 = eventLog.store(new Event("event3"));

        // then
        assertEquals("event1", eventLog.get(event1.offset()).content());
        assertEquals("event2", eventLog.get(event2.offset()).content());
        assertEquals("event3", eventLog.get(event3.offset()).content());
    }

    static Stream<EventLog> eventLogStream() throws IOException {
        return Stream.of(
                new MMapEventLog(generateProperties("mmap_log")),
                new SimpleEventLog(generateProperties("simple_log"))
        );
    }

    private static EventLogProperties generateProperties(String logFileName) throws IOException {
        EventLogProperties prop = new EventLogProperties();
        prop.setLogFilePath(Files.createTempFile(logFileName, "").toFile().getAbsolutePath());
        return prop;
    }
}
