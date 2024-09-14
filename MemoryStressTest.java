package com.example.proj;

import java.util.ArrayList;
import java.util.List;

public class MemoryStressTest {

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        long objectCount = 0;
        long previousTime = startTime;
        List<long[]> objectList = new ArrayList<>();
        long slowdownThreshold = 50; // Threshold in ms to consider as a slowdown

        try {
            while (true) {
                long[] l = new long[500000]; // Adjust the size of the array
                objectList.add(l);
                objectCount++;

                long currentTime = System.currentTimeMillis();
                long timeTaken = currentTime - previousTime;

                // Print count more frequently to monitor progress
                if (objectCount % 10 == 0) {
                    long totalTimeTaken = currentTime - startTime;
                    System.out.println("Created " + objectCount + " objects so far... Time: " + totalTimeTaken + " ms");
                    System.out.flush(); // Ensure the message is printed before any potential crash
                }

                // Check for slowdown
                if (timeTaken > slowdownThreshold) {
                    System.out.println("Slowdown detected after creating " + objectCount + " objects. Time taken for the last batch: " + timeTaken + " ms");
                }

                previousTime = currentTime;
            }
        } catch (OutOfMemoryError e) {
            long endTime = System.currentTimeMillis();
            long duration = (endTime - startTime); // Duration in milliseconds

            System.out.println("OutOfMemoryError thrown!");
            System.out.println("Total objects created: " + objectCount);
            System.out.println("Total time taken: " + duration + " ms");
            System.out.flush(); // Ensure the message is printed before exiting
        }
    }
}
