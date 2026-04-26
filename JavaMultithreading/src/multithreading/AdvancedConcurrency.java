package multithreading;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

// ============================================================
//  CONCEPT 7: Advanced Concurrency Utilities
// ============================================================

public class AdvancedConcurrency {

    public static void demo() throws InterruptedException, BrokenBarrierException {
        System.out.println("\n╔══════════════════════════════════════════════╗");
        System.out.println("║     CONCEPT 7: Advanced Concurrency Utils    ║");
        System.out.println("╚══════════════════════════════════════════════╝");

        demoCountDownLatch();
        demoCyclicBarrier();
        demoSemaphore();
        demoConcurrentCollections();
    }

    // ── CountDownLatch: wait for N events to complete ────────
    static void demoCountDownLatch() throws InterruptedException {
        System.out.println("\n--- CountDownLatch: Race → Wait for all runners ---");
        System.out.println("  Scenario: 5 runners start, announcer waits for all to finish.\n");

        int runners = 5;
        CountDownLatch startGun  = new CountDownLatch(1); // 1 signal to start
        CountDownLatch finishLine = new CountDownLatch(runners); // wait for all

        for (int i = 1; i <= runners; i++) {
            final int runnerId = i;
            new Thread(() -> {
                try {
                    System.out.println("  🏃 Runner " + runnerId + " ready at starting line...");
                    startGun.await(); // wait for the start signal
                    int time = 200 + (int)(Math.random() * 400);
                    Thread.sleep(time);
                    System.out.println("  🏁 Runner " + runnerId + " finished! (" + time + "ms)");
                    finishLine.countDown(); // signal "I'm done"
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }, "Runner-" + i).start();
        }

        Thread.sleep(200);
        System.out.println("\n  🔫 START GUN FIRED!\n");
        startGun.countDown(); // release all runners

        finishLine.await(); // main thread waits for everyone
        System.out.println("\n  🎉 All runners finished! Race complete.\n");
        System.out.println("  💡 CountDownLatch: count-down to zero = release waiting threads");
    }

    // ── CyclicBarrier: sync threads at a checkpoint ──────────
    static void demoCyclicBarrier() throws InterruptedException, BrokenBarrierException {
        System.out.println("\n--- CyclicBarrier: Multi-phase computation ---");
        System.out.println("  Scenario: 3 workers must sync at each phase before moving on.\n");

        int workers = 3;
        CyclicBarrier barrier = new CyclicBarrier(workers,
                () -> System.out.println("  ✅ [Barrier] All workers synced — next phase!\n"));

        for (int i = 1; i <= workers; i++) {
            final int id = i;
            new Thread(() -> {
                try {
                    for (int phase = 1; phase <= 2; phase++) {
                        int work = 100 + id * 50;
                        Thread.sleep(work);
                        System.out.printf("  [Worker-%d] Phase %d done (%dms)%n", id, phase, work);
                        barrier.await(); // wait for everyone else
                    }
                    System.out.println("  [Worker-" + id + "] All phases complete!");
                } catch (Exception e) {
                    Thread.currentThread().interrupt();
                }
            }, "Worker-" + i).start();
        }

        Thread.sleep(2000);
        System.out.println("  💡 CyclicBarrier: reusable (unlike CountDownLatch)");
    }

    // ── Semaphore: limit concurrent access ───────────────────
    static void demoSemaphore() throws InterruptedException {
        System.out.println("\n--- Semaphore: Parking Lot (only 3 spots) ---");
        System.out.println("  Scenario: 7 cars try to park, but only 3 spots available.\n");

        Semaphore parkingLot = new Semaphore(3); // 3 permits
        AtomicInteger parked = new AtomicInteger(0);

        for (int i = 1; i <= 7; i++) {
            final int carId = i;
            new Thread(() -> {
                try {
                    System.out.println("  🚗 Car-" + carId + " wants to park... (" +
                            parkingLot.availablePermits() + " spots left)");
                    parkingLot.acquire(); // wait for a spot
                    int current = parked.incrementAndGet();
                    System.out.println("  ✅ Car-" + carId + " parked! (" + current + "/3 spots used)");
                    Thread.sleep(300 + (int)(Math.random() * 300)); // stay parked
                    System.out.println("  🚙 Car-" + carId + " leaving. Spot freed.");
                    parked.decrementAndGet();
                    parkingLot.release(); // free the spot
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }, "Car-" + i).start();
        }

        Thread.sleep(3000);
        System.out.println("  💡 Semaphore: controls access to N resources simultaneously");
    }

    // ── Concurrent Collections ───────────────────────────────
    static void demoConcurrentCollections() throws InterruptedException {
        System.out.println("\n--- Concurrent Collections ---");

        // ConcurrentHashMap: thread-safe HashMap
        ConcurrentHashMap<String, Integer> scores = new ConcurrentHashMap<>();
        CountDownLatch latch = new CountDownLatch(3);

        // 3 threads updating the same map simultaneously
        for (int i = 1; i <= 3; i++) {
            final int teamId = i;
            new Thread(() -> {
                for (int g = 0; g < 5; g++) {
                    scores.merge("Team-" + teamId, 1, Integer::sum);
                }
                System.out.printf("  [Thread-%d] Updated Team-%d scores%n", teamId, teamId);
                latch.countDown();
            }).start();
        }

        latch.await();
        System.out.println("  Final scores (ConcurrentHashMap): " + scores);

        // ConcurrentLinkedQueue: thread-safe queue
        ConcurrentLinkedQueue<String> taskQueue = new ConcurrentLinkedQueue<>();
        taskQueue.add("Task-A");
        taskQueue.add("Task-B");
        taskQueue.add("Task-C");
        System.out.println("  ConcurrentLinkedQueue: " + taskQueue);
        System.out.println("  Poll: " + taskQueue.poll() + " → remaining: " + taskQueue);

        System.out.println("""
        
        💡 Thread-safe collection summary:
           • ConcurrentHashMap   → thread-safe HashMap, no full-map lock
           • CopyOnWriteArrayList → thread-safe list, good for read-heavy
           • ConcurrentLinkedQueue → thread-safe non-blocking queue
           • BlockingQueue        → great for producer-consumer (thread-safe)
           
        ⚠️  Never use HashMap / ArrayList directly across threads!
        """);
    }
}
