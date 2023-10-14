package pl.allegro.tech.eden.performancefaultyapp;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.FileStore;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.sun.nio.file.ExtendedOpenOption.DIRECT;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.WRITE;

public class SuperFastEventLog implements EventLog {

    private static final int MAX_EVENT_SIZE_BYTES = 1024;
    private static final int PAGE_SIZE_BYTES = 4096;

    private final MappedByteBuffer map;
    private final FileChannel channel;
    private final byte[] empty = new byte[PAGE_SIZE_BYTES];
    private final Logger logger = LoggerFactory.getLogger(SuperFastEventLog.class);
    private final EventLogProperties properties;

    private int activeSegmentIndex;
    private final ByteBuffer activeSegment;

    public SuperFastEventLog(EventLogProperties properties) throws IOException {
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
        channel = FileChannel.open(logFile.toPath(), CREATE, WRITE, DIRECT);
        FileStore fs = Files.getFileStore(logFile.toPath());
        int alignment = (int) fs.getBlockSize();
        activeSegment = ByteBuffer
                .allocateDirect(PAGE_SIZE_BYTES + alignment - 1)
                .alignedSlice(alignment);
    }

    @Override
    public StoreEventResult store(Event event, boolean force) {
        byte[] bytes = event.content().getBytes();

        if (bytes.length + Integer.BYTES /* lenght */ > MAX_EVENT_SIZE_BYTES) {
            throw new RuntimeException("Event is too big");
        }

        long offset;
        try {
            int remaining = activeSegment.remaining();
            if (remaining == 0) {
                activeSegmentIndex += activeSegment.capacity();
                activeSegment.put(0, empty);
                activeSegment.clear();
            }

            int currentPos = activeSegment.position();
            activeSegment.putInt(bytes.length);
            activeSegment.put(bytes, 0, bytes.length);
            activeSegment.position(PAGE_SIZE_BYTES);
            activeSegment.flip();
            channel.write(activeSegment, activeSegmentIndex);
            channel.force(force);
            activeSegment.position(currentPos + MAX_EVENT_SIZE_BYTES);
            offset = ((long) activeSegmentIndex / MAX_EVENT_SIZE_BYTES) + ((long) currentPos / MAX_EVENT_SIZE_BYTES);

            if (offset == properties.getMaxEventCount()) {
                activeSegment.put(0, empty);
                activeSegment.clear();
                activeSegmentIndex = 0;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new StoreEventResult(offset);
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
