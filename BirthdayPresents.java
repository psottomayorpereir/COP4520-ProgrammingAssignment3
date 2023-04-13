import java.util.*;
import java.util.concurrent.*;

public class BirthdayPresents {

    private static final int NUM_THREADS = 4;
    private static final int NUM_GUESTS = 500000;
    private static final int ADD_PRESENT = 0;
    private static final int WRITE_CARD = 1;
    private static final int SEARCH_FOR_PRESENT = 2;

    public static void main(String[] args) {

        // Instantiate a linked list object
        // and create a concurrent hash map to store the cards
        ConcurrentLinkedList list= new ConcurrentLinkedList();
        ConcurrentHashMap<Integer, Integer> cards = new ConcurrentHashMap<Integer, Integer>();

        // Create a threads array with 4 servants
        Thread[] threads = new Thread[NUM_THREADS];

        // Fill our gift bag with random values
        System.out.println("Generating " + NUM_GUESTS + " numbers...");
        NavigableSet<Integer> giftBag = generateSet(NUM_GUESTS);

        // Start each thread, start timer, and join the threads
        for (int i = 0; i < NUM_THREADS; i++) {
            threads[i] = new Thread(() -> completeTask(list, giftBag, cards));
        }
        System.out.println("Running " + NUM_THREADS + " threads...");
        long start = System.currentTimeMillis();
        for (Thread thread : threads) {
            thread.start();
        }

        // Join the threads
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Stop timer and print how long it took to finish the tasks
        long end = System.currentTimeMillis();
        long duration = end - start;
        System.out.println("Finished in " + duration + "ms");
    }

    private static NavigableSet<Integer> generateSet(int size) {
        // Generate random numbers for the gift bag
        NavigableSet<Integer> set = new TreeSet<Integer>();
        Random rand = new Random();
        while (set.size() < size) {
            set.add(rand.nextInt(size));
        }
        return set;
    }

    private static void completeTask(ConcurrentLinkedList list, NavigableSet<Integer> giftBag, ConcurrentHashMap<Integer, Integer> cards) {
        while (cards.size() < NUM_GUESTS) {
            // Generate a random number between 0 and 2 
            // to decide which task will be done next
            Random rand = new Random();
            int task = rand.nextInt(3);
    
            switch (task) {

                case ADD_PRESENT: {
                    // Take a gift from the bag and add it to the linked list
                    Integer num;
                    synchronized (giftBag) {
                        Iterator<Integer> iterator = giftBag.iterator();
                        if (iterator.hasNext()) {
                            num = iterator.next();
                            iterator.remove();
                        } else {
                            continue;
                        }
                    }
                    list.insert(num);

                    break;
                }

                case WRITE_CARD: {
                    // Write 'thank you' card to the guest
                    if (list.empty()) {
                        continue;
                    }
                    Integer guest = list.removeHead();
                    if (guest == Integer.MIN_VALUE) {
                        continue;
                    }
                    cards.put(guest, guest);

                    break;
                }
                case SEARCH_FOR_PRESENT: {
                    // Search for the present in the linked list
                    rand = new Random();
                    int randomGuest = rand.nextInt(NUM_GUESTS);
                    boolean found = list.contains(randomGuest);

                    break;
                }
            }
        }
    }
}