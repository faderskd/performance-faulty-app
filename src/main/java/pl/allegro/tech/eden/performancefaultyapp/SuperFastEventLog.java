package pl.allegro.tech.eden.performancefaultyapp;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.FileStore;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

import static com.sun.nio.file.ExtendedOpenOption.DIRECT;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.WRITE;

public class SuperFastEventLog implements EventLog {

    private static final int MAX_EVENT_SIZE_BYTES = 1024;
    private static final int PAGE_SIZE_BYTES = 4096;

    private final MappedByteBuffer map;
    private final FileChannel channel;

    private int activeSegmentIndex;
    private final ByteBuffer activeSegment;

    public SuperFastEventLog(EventLogProperties properties) throws IOException {
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
        activeSegmentIndex = 0;
        activeSegment = ByteBuffer
                .allocateDirect(PAGE_SIZE_BYTES + alignment - 1)
                .alignedSlice(alignment);
    }

    @Override
    public StoreEventResult store(Event event) {
        byte[] bytes = event.content().getBytes();

        if (bytes.length + Integer.BYTES /* lenght */ > MAX_EVENT_SIZE_BYTES) {
            throw new RuntimeException("Event is too big");
        }

        long offset;
        try {
            int remaining = activeSegment.remaining();
            if (remaining == 0) {
                activeSegmentIndex += activeSegment.capacity();
                activeSegment.clear();
            }

            int currentPos = activeSegment.position();
            activeSegment.putInt(bytes.length);
            activeSegment.put(bytes, 0, bytes.length);
            activeSegment.position(PAGE_SIZE_BYTES);
            activeSegment.flip();
            channel.write(activeSegment, activeSegmentIndex);
            channel.force(true);
            activeSegment.position(currentPos + MAX_EVENT_SIZE_BYTES);
            offset = ((long) activeSegmentIndex / MAX_EVENT_SIZE_BYTES) + ((long) currentPos / MAX_EVENT_SIZE_BYTES);
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
