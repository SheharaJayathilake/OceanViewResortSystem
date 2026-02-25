package com.oceanview.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Thread-safe reservation number generator
 * Generates unique reservation numbers with format: RES-YYYYMMDD-XXXX
 */
public class ReservationNumberGenerator {
    // Initialize with a time-based value instead of 1000 to prevent collisions
    // across JVM restarts (especially during testing)
    private static final AtomicInteger counter = new AtomicInteger(
            1000 + (int) (System.currentTimeMillis() % 8999));
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");

    private ReservationNumberGenerator() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static synchronized String generate() {
        String datePart = LocalDateTime.now().format(DATE_FORMAT);
        int sequence = counter.getAndIncrement();
        if (sequence > 9999) {
            counter.set(1000);
            sequence = counter.getAndIncrement();
        }
        return String.format("RES-%s-%04d", datePart, sequence);
    }
}
