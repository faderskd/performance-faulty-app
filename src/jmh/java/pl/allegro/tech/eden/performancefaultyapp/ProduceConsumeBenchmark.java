package pl.allegro.tech.eden.performancefaultyapp;

import java.nio.file.Files;
import java.nio.file.Path;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Group;
import org.openjdk.jmh.annotations.GroupThreads;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class ProduceConsumeBenchmark {

    private static final Event EVENT = new Event("Hello World");

    public enum EventLogType {
//        Simple {
//            @Override
//            public EventLog create(EventLogProperties properties) throws IOException {
//                return new SimpleEventLog(properties);
//            }
//        };
        MMap {
            @Override
            public EventLog create(EventLogProperties properties) throws IOException {
                return new MMapEventLog(properties);
            }
        };
//        SuperFast {
//            @Override
//            public EventLog create(EventLogProperties properties) throws IOException {
//                return new SuperFastEventLog(properties);
//            }
//        };

        public abstract EventLog create(EventLogProperties properties) throws IOException;
    }

    @Param
    EventLogType eventLogType;

    @State(Scope.Group)
    public static class EventLogState {
        public volatile int initialNumberOfEvents = 1000000;
        public int maxNumberOfEvents = initialNumberOfEvents * 2;
        public volatile long committed = 0;
    }

    private final EventLogProperties properties = new EventLogProperties();
    private EventLog eventLog;

    @Setup(Level.Trial)
    public void setUp(EventLogState state) throws IOException {
        properties.setMaxEventCount(state.maxNumberOfEvents);
//        Path path = Path.of(properties.getLogFilePath());
//        if (Files.exists(path)) {
//            Files.delete(path);
//        }
        eventLog = eventLogType.create(properties);
//        for (int i = 0; i < state.initialNumberOfEvents; i++) {
//            eventLog.store(EVENT, false);
//        }
        OS.clearPageCache();
        OS.printPageCache(properties.getLogFilePath());
    }

    @Benchmark
    @Group("produce_consume")
    @GroupThreads(1)
    public Event consume(EventLogState state) {
        long offset = state.committed++ % state.initialNumberOfEvents;
        return eventLog.get(offset);
    }

    @Benchmark
    @Group("produce_consume")
    @GroupThreads(1)
    public long produce(EventLogState state) throws InterruptedException {
        return eventLog.store(EVENT, false).offset();
    }
}
