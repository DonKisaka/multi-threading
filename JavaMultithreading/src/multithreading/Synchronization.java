package multithreading;

import java.util.concurrent.atomic.AtomicInteger;

// ============================================================
//  CONCEPT 3: Synchronization — Race Conditions & Fixes
// ============================================================

public class Synchronization {

    // ── PROBLEM: Unsafe counter (race condition) ─────────────
    static class UnsafeCounter {
        private int count = 0;

        public void increment() {
            count++; // NOT atomic: read → modify → write (3 steps!)
        }

        public int getCount() { return count; }
    }

    // ── FIX 1: Synchronized method ───────────────────────────
    static class SynchronizedCounter {
        private int count = 0;

        public synchronized void increment() { // only 1 thread at a time
            count++;
        }

        public synchronized int getCount() { return count; }
    }

    // ── FIX 2: Synchronized block (more granular) ────────────
    static class BlockSyncCounter {
        private int count = 0;
        private final Object lock = new Object();

        public void increment() {
            // Only this block is locked, not the entire method
            synchronized (lock) {
                count++;
            }
        }

        public int getCount() { return count; }
    }

    // ── FIX 3: AtomicInteger (best performance) ──────────────
    static class AtomicCounter {
        private AtomicInteger count = new AtomicInteger(0);

        public void increment() {
            count.incrementAndGet(); // thread-safe, lock-free
        }

        public int getCount() { return count.get(); }
    }

    // ── Helper: Run 1000 increments across 5 threads ─────────
    interface Counter {
        void increment();
        int getCount();
    }

    private static int testCounter(Counter counter, String label) throws InterruptedException {
        int THREADS = 5, INCREMENTS = 1000;
        Thread[] threads = new Thread[THREADS];

        for (int i = 0; i < THREADS; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < INCREMENTS; j++) {
                    counter.increment();
                }
            });
            threads[i].start();
        }

        for (Thread t : threads) t.join();

        int result = counter.getCount();
        String status = result == THREADS * INCREMENTS ? "✅" : "❌";
        System.out.printf("  %s %-30s → count = %5d  (expected %d)%n",
                status, label, result, THREADS * INCREMENTS);
        return result;
    }

    public static void demo() throws InterruptedException {
        System.out.println("\n╔══════════════════════════════════════════════╗");
        System.out.println("║     CONCEPT 3: Synchronization               ║");
        System.out.println("╚══════════════════════════════════════════════╝");

        System.out.println("\n5 threads each do 1000 increments → expected total = 5000");
        System.out.println();

        UnsafeCounter      uc = new UnsafeCounter();
        SynchronizedCounter sc = new SynchronizedCounter();
        BlockSyncCounter    bc = new BlockSyncCounter();
        AtomicCounter       ac = new AtomicCounter();

        testCounter(uc::increment, uc::getCount, "UnsafeCounter (race condition)");
        testCounter(sc::increment, sc::getCount, "SynchronizedCounter (method)");
        testCounter(bc::increment, bc::getCount, "BlockSyncCounter (block)");
        testCounter(ac::increment, ac::getCount, "AtomicCounter (atomic)");

        System.out.println("""
        
        📌 Key Takeaways:
           • count++ is NOT thread-safe — it has 3 underlying steps
           • 'synchronized' ensures only 1 thread runs a block at a time
           • Synchronized blocks are more granular than methods
           • AtomicInteger uses CPU-level CAS instructions — fastest option
           • volatile only fixes visibility, NOT atomicity of compound ops
        """);

        // ── Volatile keyword demo ────────────────────────────
        System.out.println("--- volatile keyword ---");
        System.out.println("  Without volatile: a thread may cache a variable and miss updates from another thread.");
        System.out.println("  With    volatile: writes are immediately visible to all threads (no CPU cache).\n");

        VolatileDemo vd = new VolatileDemo();
        Thread writer = new Thread(vd::write, "WriterThread");
        Thread reader = new Thread(vd::read,  "ReaderThread");
        reader.start();
        Thread.sleep(100);
        writer.start();
        writer.join();
        reader.join();
    }

    // Helper overload to accept two lambdas
    private static void testCounter(Runnable inc, java.util.function.Supplier<Integer> get, String label)
            throws InterruptedException {
        int THREADS = 5, INCREMENTS = 1000;
        Thread[] threads = new Thread[THREADS];
        for (int i = 0; i < THREADS; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < INCREMENTS; j++) inc.run();
            });
            threads[i].start();
        }
        for (Thread t : threads) t.join();
        int result = get.get();
        String status = result == THREADS * INCREMENTS ? "✅" : "❌";
        System.out.printf("  %s %-35s → count = %5d  (expected %d)%n",
                status, label, result, THREADS * INCREMENTS);
    }

    // ── Volatile example class ───────────────────────────────
    static class VolatileDemo {
        private volatile boolean ready = false; // volatile ensures visibility

        public void write() {
            try { Thread.sleep(200); } catch (InterruptedException e) {}
            System.out.println("  [Writer] Setting ready = true");
            ready = true;
        }

        public void read() {
            System.out.println("  [Reader] Waiting for ready...");
            while (!ready) {
                Thread.yield(); // spin-wait
            }
            System.out.println("  [Reader] Saw ready = true! ✅ (volatile worked)");
        }
    }
}
