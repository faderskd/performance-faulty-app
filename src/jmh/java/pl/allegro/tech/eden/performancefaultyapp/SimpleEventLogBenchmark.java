package pl.allegro.tech.eden.performancefaultyapp;

import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class SimpleEventLogBenchmark {

    public enum EventLogType {
        Simple {
            @Override
            public EventLog create(EventLogProperties properties, EventLogState state) throws IOException {
                SimpleEventLog eventLog = new SimpleEventLog(properties);
                for (int i = 0; i < state.numberOfStoredEvents; i++) {
                    eventLog.store(new Event(state.content), false);
                }
                OS.clearPageCache();
                OS.printPageCache(properties.getLogFilePath());
                return eventLog;
            }
        },
        SimpleDirty {
            @Override
            public EventLog create(EventLogProperties properties, EventLogState state) throws IOException {
                SimpleEventLog simpleEventLog = new SimpleEventLog(properties);
                for (int i = 0; i < state.numberOfStoredEvents; i++) {
                    simpleEventLog.store(new Event(state.content), false);
                }
                for (int i = 0; i < state.numberOfStoredEvents; i++) {
                    simpleEventLog.get(i);
                }
                OS.printPageCache(properties.getLogFilePath());
                return simpleEventLog;
            }
        },
        MMap {
            @Override
            public EventLog create(EventLogProperties properties, EventLogState state) throws IOException {
                MMapEventLog mMapEventLog = new MMapEventLog(properties);
                for (int i = 0; i < state.numberOfStoredEvents; i++) {
                    mMapEventLog.store(new Event(state.content), false);
                }
                OS.clearPageCache();
                OS.printPageCache(properties.getLogFilePath());
                return mMapEventLog;
            }
        },

        MMapDirty {
            @Override
            public EventLog create(EventLogProperties properties, EventLogState state) throws IOException {
                MMapEventLog mMapEventLog = new MMapEventLog(properties);
                for (int i = 0; i < state.numberOfStoredEvents; i++) {
                    mMapEventLog.store(new Event(state.content), false);
                }
                for (int i = 0; i < state.numberOfStoredEvents; i++) {
                    mMapEventLog.get(i);
                }

                OS.printPageCache(properties.getLogFilePath());
                return mMapEventLog;
            }
        };

        public abstract EventLog create(EventLogProperties properties, EventLogState state) throws IOException;
    }

    @Param
    EventLogType eventLogType;

    @State(Scope.Thread)
    public static class EventLogState {
        public int numberOfStoredEvents = 1000000;
        public String content = "Hello World";

        public int key = ThreadLocalRandom.current().nextInt(1000);
    }

    private final EventLogProperties properties = new EventLogProperties();
    private EventLog eventLog;

    @Setup(Level.Iteration)
    public void setUp(EventLogState state) throws IOException {
        Path path = Path.of(properties.getLogFilePath());
//        if (Files.exists(path)) {
//            Files.delete(path);
//        }
        eventLog = eventLogType.create(properties, state);
    }

    @Benchmark
    public String readFromLog(EventLogState state) {
        return eventLog.get(state.key++ % state.numberOfStoredEvents).content();
    }
}
