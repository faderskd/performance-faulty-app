package pl.allegro.tech.eden.performancefaultyapp;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.catalina.Store;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleEventLog implements EventLog {

    private static final int MAX_EVENT_SIZE_BYTES = 1024;
    private static final Logger logger = LoggerFactory.getLogger(SimpleEventLog.class);

    private final FileChannel fileChannel;
    private final EventLogProperties properties;
    private final AtomicLong currentOffset = new AtomicLong(0);

    public SimpleEventLog(EventLogProperties properties) throws IOException {
        fileChannel = FileChannel.open(
                Path.of(properties.getLogFilePath()),
                StandardOpenOption.READ,
                StandardOpenOption.WRITE,
                StandardOpenOption.CREATE
        );
        this.properties = properties;
        setupPosition(properties);
    }

    @Override
    public StoreEventResult store(Event event, boolean force) {
        ByteBuffer buffer = ByteBuffer.allocate(MAX_EVENT_SIZE_BYTES);
        byte[] bytes = event.content().getBytes();

        if (bytes.length + Integer.BYTES /* lenght */ > MAX_EVENT_SIZE_BYTES) {
            throw new RuntimeException("Event is too big");
        }

        buffer.putInt(bytes.length);
        buffer.put(bytes);
        buffer.position(0);

        try {
            fileChannel.position(currentOffset.get());
            fileChannel.write(buffer);
            fileChannel.force(force);
            currentOffset.set(currentOffset.get() + MAX_EVENT_SIZE_BYTES);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        long offset = currentOffset.get() / MAX_EVENT_SIZE_BYTES - 1;
        if (offset == properties.getMaxEventCount()) {
            currentOffset.set(0);
        }
        return new StoreEventResult(offset);
    }

    @Override
    public Event get(long offset) {
        long fileOffset = offset * MAX_EVENT_SIZE_BYTES;
        ByteBuffer buffer = ByteBuffer.allocate(MAX_EVENT_SIZE_BYTES);
        try {
            fileChannel.read(buffer, fileOffset);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        buffer.position(0);
        int contentLength = buffer.getInt();
        byte[] bytes = new byte[contentLength];
        buffer.get(bytes);
        return new Event(new String(bytes));
    }

    private void setupPosition(EventLogProperties properties) {
        if (properties.isWriteFromBegin()) {
            logger.info("Starting from offset 0");
            return;
        }
        int i = 0;
        while (i < Integer.MAX_VALUE) {
            Event e = get(i);
            if (Objects.equals(e.content(), "")) {
                currentOffset.set((long) i * MAX_EVENT_SIZE_BYTES);
                logger.info("Last offset is {}", i);
                break;
            }
            i += 1;
        }
    }
}