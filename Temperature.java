import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import java.util.Collections;
import java.util.Random;
import java.util.*;

public class Temperature {

    private static final int NUM_THREADS = 8;
    private static final int MINUTES = 60;
    private static final int HOURS = 48;
    private static ReentrantLock mutex = new ReentrantLock();

    public static void main(String[] args) {
        // Keep track of every temperature reading for the threads
        // Each thread can only capture 60 readings (1 per minute), so thread 0 writes on indexes 0-59,
        // thread 1 writes on indexes 60-119, etc
        List<Integer> sensorReadings = new ArrayList<>(NUM_THREADS * MINUTES);
        for (int i = 0; i < NUM_THREADS * MINUTES; i++) {
            sensorReadings.add(0);
        }

        // Keep track if the sensor is ready to read another temperature
        List<Boolean> sensorsReady = new ArrayList<>(NUM_THREADS);
        for (int i = 0; i < NUM_THREADS; i++) {
            sensorsReady.add(false);
        }

        // Start the threads, start the timer, and join the threads
        Thread[] threads = new Thread[NUM_THREADS];
        for (int i = 0; i < NUM_THREADS; i++) {
            final int index=i;
            threads[i] = new Thread(() -> measureTemperature(index, sensorReadings, sensorsReady));
            threads[i].start();
        }
        long start = System.currentTimeMillis();
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // End timer and print how long it took to finish the tasks
        long end = System.currentTimeMillis();
        long duration = end - start;
        System.out.println("Finished in " + duration + "ms");
    }

    public static void measureTemperature(int threadId, List<Integer> sensorReadings, List<Boolean> sensorsReady) {
        for (int hour = 0; hour < HOURS; hour++) {
            for (int minute = 0; minute < MINUTES; minute++) {
                // Get the temperature reading and add it to sensorReadings
                // then set the sensor as ready for another temperature reading
                Random rand = new Random();
                int min = -100;
                int max = 70;
                int randomNum = rand.nextInt((max - min) + 1) + min;
                sensorsReady.set(threadId, false);
                sensorReadings.set(minute + (threadId * MINUTES), randomNum);
                sensorsReady.set(threadId, true);
    
                // Make sure all sensors are ready for another temperature reading
                while (!allSensorsReady(threadId, sensorsReady)) {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            // Make thread 0 responsible for generating the report
            if (threadId == 0) {
                mutex.lock();
                generateReport(hour, sensorReadings);
                mutex.unlock();
            }
        }
    }

    private static boolean allSensorsReady(int threadId, List<Boolean> sensorsReady) {
        // Check if all sensors are ready to capture the next temperature reading
        for (int i = 0; i < NUM_THREADS; i++) {
            if (i != threadId && !sensorsReady.get(i)) {
                return false;
            }
        }
        return true;
    }

    public static void generateReport(int hour, List<Integer> sensorReadings) {
        // Write report with necessary information
        System.out.println("[Hour " + (hour + 1) + " report]");
    
        printLargestDifference(sensorReadings);
        
        try{
            Collections.sort(sensorReadings);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    
        printHighestTemperatures(sensorReadings);
        printLowestTemperatures(sensorReadings);
    
        System.out.println();
        System.out.println();
    }

    public static void printHighestTemperatures(List<Integer> sensorReadings) {
        // Print the 5 highest temperature readings on the current hour interval
        Set<Integer> temperatures = new TreeSet<>();
    
        for (int i = sensorReadings.size() - 1; i >= 0; i--) {
            int reading = sensorReadings.get(i);
    
            if (!temperatures.contains(reading)) {
                temperatures.add(reading);
            }
    
            if (temperatures.size() == 5) {
                break;
            }
        }
    
        System.out.println("Highest temperatures: ");
    
        for (int temperature : temperatures) {
            System.out.print(temperature + "F ");
        }
    
        System.out.println();
    }

    public static void printLowestTemperatures(List<Integer> sensorReadings) {
        // Print the 5 lowest temperature readings on the current hour interval
        Set<Integer> temperatures = new TreeSet<>();
    
        for (int temperature : sensorReadings) {
            if (!temperatures.contains(temperature)) {
                temperatures.add(temperature);
            }
    
            if (temperatures.size() == 5) {
                break;
            }
        }
    
        System.out.println("Lowest temperatures: ");
    
        for (int temperature : temperatures) {
            System.out.print(temperature + "F ");
        }
    
        System.out.println();
    }

    public static void printLargestDifference(List<Integer> sensorReadings) {
        // Print the largest temperature difference in a 10-min interval
        int interval = 10;
        int startInterval = 0;
        int maxDifference = Integer.MIN_VALUE;
        int NUM_THREADS = 1;
        int MINUTES = sensorReadings.size();
    
        // Loop through the array to find the largest difference for the current sensor
        for (int threadIndex = 0; threadIndex < NUM_THREADS; threadIndex++) {
            int offset = threadIndex * MINUTES;
    
            for (int i = offset; i < MINUTES - interval + 1; i++) {
                int max = Collections.max(sensorReadings.subList(i, i + interval));
                int min = Collections.min(sensorReadings.subList(i, i + interval));
                int diff = max - min;
    
                if (diff > maxDifference) {
                    maxDifference = diff;
                    startInterval = i;
                }
            }
        }
    
        System.out.println("Largest temperature difference (" + "observed from minute " + startInterval + " to minute " + (startInterval + 10) + "): \n" + maxDifference + "F");
    }

}
