package pl.allegro.tech.eden.performancefaultyapp;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import pl.allegro.tech.eden.performancefaultyapp.list.FastListIterator;
import pl.allegro.tech.eden.performancefaultyapp.list.ListIterator;
import pl.allegro.tech.eden.performancefaultyapp.list.SlowListIterator;

import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class ListIteratorBenchmark {

    public enum ListIteratorType {
        Slow {
            @Override
            public ListIterator create() {
                return new SlowListIterator();
            }
        },
        Fast {
            @Override
            public ListIterator create() {
                return new FastListIterator();
            }
        };

        public abstract ListIterator create();
    }

    @Param
    ListIteratorType iteratorType;

    private ListIterator iterator;

    @Setup(Level.Iteration)
    public void setUp() {
        iterator = iteratorType.create();
        for (int i = 0; i < 1_000_000; i++) {
            iterator.append(i);
        }
    }

    @Benchmark
    public int iterate() {
        return iterator.findMax();
    }
}
