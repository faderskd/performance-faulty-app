package pl.allegro.tech.eden.performancefaultyapp;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.atomic.AtomicLong;

public class SimpleEventLog implements EventLog {

    private static final int MAX_EVENT_SIZE_BYTES = 1024;

    private final FileChannel fileChannel;
    private final AtomicLong currentOffset = new AtomicLong(0);

    public SimpleEventLog(EventLogProperties properties) throws IOException {
        fileChannel = FileChannel.open(
                Path.of(properties.getLogFilePath()),
                StandardOpenOption.READ,
                StandardOpenOption.WRITE,
                StandardOpenOption.CREATE
        );
        currentOffset.set(fileChannel.size());
    }

    @Override
    public StoreEventResult store(Event event) {
        ByteBuffer buffer = ByteBuffer.allocate(MAX_EVENT_SIZE_BYTES);
        byte[] bytes = event.content().getBytes();

        if (bytes.length + Integer.BYTES /* lenght */ > MAX_EVENT_SIZE_BYTES) {
            throw new RuntimeException("Event is too big");
        }

        buffer.putInt(bytes.length);
        buffer.put(bytes);
        buffer.position(0);

        long offset = -1;
        try {
            fileChannel.position(currentOffset.get());
            fileChannel.write(buffer);
            fileChannel.force(true);
            offset = (fileChannel.position() - MAX_EVENT_SIZE_BYTES) / MAX_EVENT_SIZE_BYTES;
            currentOffset.set(currentOffset.get() + MAX_EVENT_SIZE_BYTES);
        } catch (IOException e) {
            throw new RuntimeException(e);
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
}
