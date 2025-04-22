package org.example;

import java.util.Collections;
import java.util.List;

public class Metrics {
    public static void calculateResponseTime(List<Long> times) {
        if (times.isEmpty()) {
            System.out.println("\nNo data available for response time statistics.");
            return;
        }

        Collections.sort(times);
        long sum = times.stream().mapToLong(Long::longValue).sum();

        System.out.println("Total size: " + times.size() + " messages");
        System.out.println("Average response time: " + (double) sum / times.size() + " ms");
        System.out.println("Median response time: " + times.get(times.size() / 2) + " ms");
        System.out.println("Min response time: " + times.getFirst() + " ms");
        System.out.println("Max response time: " + times.getLast() + " ms");
    }

    public static void calculateLatency(List<Long> times) {
        if (times.isEmpty()) {
            System.out.println("\nNo data available for latency statistics.");
            return;
        }

        Collections.sort(times);
        long sum = times.stream().mapToLong(Long::longValue).sum();

        System.out.println("Total size: " + times.size() + " messages");
        System.out.println("Average latency: " + (double) sum / times.size() + " ms");
        System.out.println("Median latency: " + times.get(times.size() / 2) + " ms");
        System.out.println("Min latency: " + times.getFirst() + " ms");
        System.out.println("Max latency: " + times.getLast() + " ms");
    }
}
