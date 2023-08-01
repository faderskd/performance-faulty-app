package pl.allegro.tech.eden.performancefaultyapp;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.SECONDS)
public class SimpleEventLogBenchmark {

//    @State(Scope.Thread)
//    public static class State {
//        public int a = 1;
//        public int b = 2;
//        public int sum ;
//    }

    private final EventLogProperties properties = new EventLogProperties();
    private SimpleEventLog eventLog;

    @Setup(Level.Trial)
    public void setUp() throws IOException {
        int numberOfStoredEvents = 1000;
        String content = "Hello World";
        eventLog = new SimpleEventLog(properties);
        for (int i = 0; i < numberOfStoredEvents; i++) {
          eventLog.store(new Event(content));
        }
    }

    @Benchmark
    public void readFromLog() {
        int numberOfStoredEvents = 1;
        String expectedContent = "Hello World";
        for (int i = 0; i < numberOfStoredEvents; i++) {
            String content = eventLog.get(i).content();
            assert Objects.equals(content, expectedContent);
        }
    }
}
