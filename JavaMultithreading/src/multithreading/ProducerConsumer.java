package multithreading;

import java.util.LinkedList;
import java.util.Queue;

// ============================================================
//  CONCEPT 4: Producer-Consumer with wait() / notify()
// ============================================================

public class ProducerConsumer {

    // ── Shared Buffer (bounded queue) ────────────────────────
    static class SharedBuffer {
        private final Queue<Integer> queue = new LinkedList<>();
        private final int CAPACITY = 3;

        // Producer calls this
        public synchronized void produce(int item) throws InterruptedException {
            while (queue.size() == CAPACITY) {
                System.out.println("  [Producer] Buffer FULL (" + CAPACITY + "). Waiting...");
                wait(); // releases lock and waits
            }
            queue.add(item);
            System.out.printf("  [Producer] ➕ Produced: %-3d  | Buffer: %s%n", item, queue);
            notify(); // wake up consumer
        }

        // Consumer calls this
        public synchronized int consume() throws InterruptedException {
            while (queue.isEmpty()) {
                System.out.println("  [Consumer] Buffer EMPTY. Waiting...");
                wait(); // releases lock and waits
            }
            int item = queue.poll();
            System.out.printf("  [Consumer] ➖ Consumed: %-3d  | Buffer: %s%n", item, queue);
            notify(); // wake up producer
            return item;
        }
    }

    // ── Producer Thread ──────────────────────────────────────
    static class Producer implements Runnable {
        private final SharedBuffer buffer;
        private final int items;

        Producer(SharedBuffer buffer, int items) {
            this.buffer = buffer;
            this.items  = items;
        }

        @Override
        public void run() {
            for (int i = 1; i <= items; i++) {
                try {
                    Thread.sleep(150); // simulate production time
                    buffer.produce(i);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            System.out.println("  [Producer] 🏁 Done producing.");
        }
    }

    // ── Consumer Thread ──────────────────────────────────────
    static class Consumer implements Runnable {
        private final SharedBuffer buffer;
        private final int items;

        Consumer(SharedBuffer buffer, int items) {
            this.buffer = buffer;
            this.items  = items;
        }

        @Override
        public void run() {
            for (int i = 0; i < items; i++) {
                try {
                    Thread.sleep(300); // consumer is slower!
                    buffer.consume();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            System.out.println("  [Consumer] 🏁 Done consuming.");
        }
    }

    public static void demo() throws InterruptedException {
        System.out.println("\n╔══════════════════════════════════════════════╗");
        System.out.println("║     CONCEPT 4: Producer-Consumer Pattern     ║");
        System.out.println("╚══════════════════════════════════════════════╝");
        System.out.println("""
        
        Scenario: Producer makes items, Consumer uses them.
        Buffer size is LIMITED → they must coordinate.
        
        wait()   → "I can't proceed, release lock and sleep."
        notify() → "Hey other thread, condition changed, wake up!"
        
        Buffer capacity: 3  |  Producer: fast  |  Consumer: slow
        """);

        SharedBuffer buffer   = new SharedBuffer();
        int totalItems        = 6;

        Thread producer = new Thread(new Producer(buffer, totalItems), "Producer");
        Thread consumer = new Thread(new Consumer(buffer, totalItems), "Consumer");

        producer.start();
        consumer.start();
        producer.join();
        consumer.join();

        System.out.println("""
        
        ✅ Producer-Consumer complete!
        📌 Key insight: wait() releases the lock (unlike sleep()), 
           allowing the other thread to enter the synchronized block.
        """);
    }
}
