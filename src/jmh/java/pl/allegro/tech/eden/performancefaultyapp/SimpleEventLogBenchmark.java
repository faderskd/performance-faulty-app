package pl.allegro.tech.eden.performancefaultyapp;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class SimpleEventLogBenchmark {

    @State(Scope.Thread)
    public static class EventLogState {
        public int numberOfStoredEvents = 1000;
        public String content = "Hello World";

        public int key = ThreadLocalRandom.current().nextInt(1000);
    }

    private final EventLogProperties properties = new EventLogProperties();
    private EventLog eventLog;

    @Setup(Level.Iteration)
    public void setUp(EventLogState state) throws IOException {
        Path path = Path.of(properties.getLogFilePath());
        if (Files.exists(path)) {
            Files.delete(path);
        }
        eventLog = new SimpleEventLog(properties);
        for (int i = 0; i < state.numberOfStoredEvents; i++) {
            eventLog.store(new Event(state.content));
        }
    }

    @Benchmark
    public String readFromLog(EventLogState state) {
        return eventLog.get(state.key++ % state.numberOfStoredEvents).content();
    }
}
