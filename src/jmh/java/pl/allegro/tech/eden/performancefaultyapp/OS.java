package pl.allegro.tech.eden.performancefaultyapp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class OS {

    private static final Logger logger = LoggerFactory.getLogger(OS.class);

    public static void clearPageCache() {
        executeAndWait("sudo sync && echo 3 | sudo tee /proc/sys/vm/drop_caches");
    }
    public static void printPageCache(String file) {
        executeAndWait("vmtouch " + file);
    }

    public static void executeAndWait(String cmd) {
        try {
            execute(cmd).waitFor();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static Process execute(String cmd) {
        logger.info("executing: {}", cmd);

        ProcessBuilder processBuilder = new ProcessBuilder("/bin/bash", "-c", cmd)
                .inheritIO()
                .redirectErrorStream(true);
        try {
            return processBuilder.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
