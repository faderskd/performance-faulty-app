package pl.allegro.tech.eden.performancefaultyapp;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class PrepareConsumeBenchmark {

    private static final Event EVENT = new Event("Hello World");
    private static final Logger logger = LoggerFactory.getLogger(PrepareConsumeBenchmark.class);

    public enum EventLogType {
        //        Simple {
//            @Override
//            public EventLog create(EventLogProperties properties) throws IOException {
//                return new SimpleEventLog(properties);
//            }
//        },
        MMap {
            @Override
            public EventLog create(EventLogProperties properties) throws IOException {
                return new MMapEventLog(properties);
            }
        };
//        Direct {
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
        public volatile long committed = 0;
    }

    private final EventLogProperties readerProperties = new EventLogProperties();
    private final EventLogProperties writerProperties1 = new EventLogProperties();
    private final EventLogProperties writerProperties2 = new EventLogProperties();
    private final EventLogProperties writerProperties3 = new EventLogProperties();
    private final EventLogProperties writerProperties4 = new EventLogProperties();
    private EventLog readerEventLog;
    private EventLog writerEventLog1;
    private EventLog writerEventLog2;
    private EventLog writerEventLog3;
    private EventLog writerEventLog4;

    @Setup(Level.Iteration)
    public void setUp(EventLogState state) throws IOException {
//        readerProperties.setLogFilePath("/home/vagrant/large.txt");
        writerProperties1.setLogFilePath("large.txt");
//        writerProperties2.setLogFilePath("/home/vagrant/writer2.txt");
//        writerProperties3.setLogFilePath("/home/vagrant/writer3.txt");
//        writerProperties4.setLogFilePath("/home/vagrant/writer4.txt");
        readerProperties.setWriteFromBegin(true);

//        Stream.of(writerProperties1, writerProperties2, writerProperties3, writerProperties4).forEach(p -> {
//            Path path = Path.of(p.getLogFilePath());
//            if (Files.exists(path)) {
//                try {
//                    Files.delete(path);
//                } catch (IOException e) {
//                    throw new RuntimeException(e);
//                }
//                p.setWriteFromBegin(true);
//            }
//        });
//
//        readerEventLog = eventLogType.create(readerProperties);
        writerEventLog1 = eventLogType.create(writerProperties1);
//        writerEventLog2 = eventLogType.create(writerProperties2);
//        writerEventLog3 = eventLogType.create(writerProperties3);
//        writerEventLog4 = eventLogType.create(writerProperties4);

//        OS.clearPageCache();
//        OS.printPageCache(readerProperties.getLogFilePath());
    }

//    @Benchmark
//    @Group("prodconsume")
//    @GroupThreads(1)
//    public Event consume(EventLogState state) throws InterruptedException {
//        long offset = state.committed++ % readerProperties.getMaxEventCount();
//        return readerEventLog.get(offset);
//    }

    @Benchmark
    @Group("prodconsume")
    @GroupThreads(1)
    public long produce1(EventLogState state) throws InterruptedException {
        return writerEventLog1.store(EVENT, false).offset();
    }
//
//    @Benchmark
//    @Group("prodconsume")

//    @GroupThreads(1)
//    public long produce2(EventLogState state) throws InterruptedException {
//        return writerEventLog2.store(EVENT, false).offset();
//    }

//    @Benchmark
//    @Group("prodconsume")
//    @GroupThreads(1)
//    public long produce3(EventLogState state) throws InterruptedException {
//        return writerEventLog3.store(EVENT, false).offset();
//    }
//
//    @Benchmark
//    @Group("prodconsume")
//    @GroupThreads(1)
//    public long produce4(EventLogState state) throws InterruptedException {
//        return writerEventLog4.store(EVENT, false).offset();
//    }
}
