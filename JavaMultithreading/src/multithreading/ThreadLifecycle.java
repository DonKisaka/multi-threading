package multithreading;

// ============================================================
//  CONCEPT 2: Thread Lifecycle & States
// ============================================================

public class ThreadLifecycle {

    public static void demo() throws InterruptedException {
        System.out.println("\n╔══════════════════════════════════════════════╗");
        System.out.println("║     CONCEPT 2: Thread Lifecycle & States     ║");
        System.out.println("╚══════════════════════════════════════════════╝");

        System.out.println("""
        
        Thread States:
        ┌─────────┐    start()    ┌──────────┐   CPU assigned   ┌─────────┐
        │   NEW   │ ───────────► │ RUNNABLE │ ──────────────► │ RUNNING │
        └─────────┘              └──────────┘                  └────┬────┘
                                       ▲                            │
                                       │   lock released /          │ wait() / sleep() /
                                       │   notify() called          │ waiting for lock
                                       │                            ▼
                                       │                     ┌──────────────┐
                                       └─────────────────────│   BLOCKED /  │
                                                             │   WAITING    │
                                                             └──────────────┘
                                                             
                                         When run() ends → TERMINATED
        """);

        // Create a thread and observe its states
        Object lock = new Object();

        Thread worker = new Thread(() -> {
            System.out.println("  [Worker] I am now RUNNING!");
            try {
                synchronized (lock) {
                    System.out.println("  [Worker] Entering WAITING state (calling wait)...");
                    lock.wait(1000); // goes to WAITING
                    System.out.println("  [Worker] Woke up! Back to RUNNABLE/RUNNING.");
                }
                Thread.sleep(500); // goes to TIMED_WAITING
                System.out.println("  [Worker] Sleep done. About to TERMINATE.");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "WorkerThread");

        // ── Observe States ──────────────────────────────────
        System.out.println("State 1 → Before start():  " + worker.getState()); // NEW

        worker.start();
        Thread.sleep(100); // let it get running
        System.out.println("State 2 → After start():   " + worker.getState());  // RUNNABLE or WAITING

        synchronized (lock) {
            Thread.sleep(200); // worker is now waiting for lock
        }
        Thread.sleep(50);
        System.out.println("State 3 → While waiting:   " + worker.getState()); // TIMED_WAITING

        worker.join(); // wait for it to finish
        System.out.println("State 4 → After finish:    " + worker.getState()); // TERMINATED

        System.out.println("\n✅ Thread went through: NEW → RUNNABLE → WAITING → TIMED_WAITING → TERMINATED");

        // ── Thread Priority ─────────────────────────────────
        System.out.println("\n--- Thread Priority (1=MIN, 5=NORMAL, 10=MAX) ---");
        Thread low  = new Thread(() -> System.out.println("  Low  priority thread ran"), "LowPriority");
        Thread high = new Thread(() -> System.out.println("  High priority thread ran"), "HighPriority");
        Thread norm = new Thread(() -> System.out.println("  Normal priority thread ran"), "NormalPriority");

        low.setPriority(Thread.MIN_PRIORITY);   // 1
        high.setPriority(Thread.MAX_PRIORITY);  // 10
        norm.setPriority(Thread.NORM_PRIORITY); // 5

        low.start(); high.start(); norm.start();
        low.join(); high.join(); norm.join();
        System.out.println("  ⚠️  Priority is a hint to the OS scheduler, not a guarantee!");

        // ── Daemon Thread ────────────────────────────────────
        System.out.println("\n--- Daemon vs User Threads ---");
        Thread daemon = new Thread(() -> {
            System.out.println("  👻 Daemon thread started (will die when main thread ends)");
        }, "DaemonThread");
        daemon.setDaemon(true); // must be set BEFORE start()
        daemon.start();
        daemon.join();
        System.out.println("  💡 Daemon threads are background threads (e.g., GC). JVM exits when only daemons remain.");
    }
}
