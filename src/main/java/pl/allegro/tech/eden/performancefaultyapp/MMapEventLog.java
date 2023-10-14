package pl.allegro.tech.eden.performancefaultyapp;

import java.io.File;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MMapEventLog implements EventLog {

    private static final int MAX_EVENT_SIZE_BYTES = 1024;

    private final MappedByteBuffer map;
    private final AtomicInteger currentOffset = new AtomicInteger(0);
    private final Logger logger = LoggerFactory.getLogger(MMapEventLog.class);
    private final EventLogProperties properties;

    public MMapEventLog(EventLogProperties properties) throws IOException {
        this.properties = properties;
        File logFile = new File(properties.getLogFilePath());
        FileChannel fileChannel = FileChannel.open(
                logFile.toPath(),
                StandardOpenOption.READ,
                StandardOpenOption.WRITE,
                StandardOpenOption.CREATE
        );
        int maxLogSizeInBytes = MAX_EVENT_SIZE_BYTES * properties.getMaxEventCount();
        map = fileChannel.map(FileChannel.MapMode.READ_WRITE, 0, maxLogSizeInBytes);
    }

    @Override
    public StoreEventResult store(Event event, boolean force) {
        byte[] bytes = event.content().getBytes();

        if (bytes.length + Integer.BYTES /* lenght */ > MAX_EVENT_SIZE_BYTES) {
            throw new RuntimeException("Event is too big");
        }

        int eventOffset = currentOffset.get();
        int offsetInFile = eventOffset * MAX_EVENT_SIZE_BYTES;
        map.position(offsetInFile);
        map.putInt(bytes.length);
        map.put(bytes);
        if (force) {
            map.force();
        }
        currentOffset.incrementAndGet();
        if (currentOffset.get() == properties.getMaxEventCount()) {
            currentOffset.set(0);
        }

        return new StoreEventResult(eventOffset);
    }

    @Override
    public Event get(long offset) {
        int fileOffset = (int) (offset * MAX_EVENT_SIZE_BYTES);
        int contentLength = map.getInt(fileOffset);
        byte[] buffer = new byte[contentLength];
        map.get(fileOffset + Integer.BYTES, buffer);
        return new Event(new String(buffer));
    }
}
