package multithreading;

// ============================================================
//  CONCEPT 6: Deadlock — Cause & Prevention
// ============================================================

public class DeadlockDemo {

    // ── Resources (locks) ────────────────────────────────────
    static final Object RESOURCE_A = new Object();
    static final Object RESOURCE_B = new Object();

    // ── DEADLOCK scenario ────────────────────────────────────
    // Thread-1: locks A → tries to lock B
    // Thread-2: locks B → tries to lock A
    // → Both wait forever!
    static void showDeadlockCause() {
        System.out.println("\n--- ⚠️  Deadlock Scenario (we will DETECT it, not run it) ---");
        System.out.println("""
          Thread-1 flow:  Lock(A) ──► wait for Lock(B)  ← STUCK!
          Thread-2 flow:  Lock(B) ──► wait for Lock(A)  ← STUCK!
        
          Code that causes deadlock:
        
          Thread 1:                   Thread 2:
          synchronized(RESOURCE_A) {  synchronized(RESOURCE_B) {
            sleep(100);                 sleep(100);
            synchronized(RESOURCE_B) { synchronized(RESOURCE_A) {
              // never reached!           // never reached!
            }                         }
          }                           }
        
          ❌ Both threads hold one resource and wait for the other.
             Neither can proceed → DEADLOCK!
        """);
    }

    // ── FIX 1: Consistent Lock Ordering ─────────────────────
    // Both threads lock A before B — no circular wait!
    static class SafeThread1 extends Thread {
        SafeThread1() { super("SafeThread-1"); }

        @Override
        public void run() {
            synchronized (RESOURCE_A) {          // always A first
                System.out.println("  [" + getName() + "] Locked Resource A");
                sleep(100);
                synchronized (RESOURCE_B) {      // then B
                    System.out.println("  [" + getName() + "] Locked Resource B ✅");
                }
            }
        }

        private void sleep(int ms) {
            try { Thread.sleep(ms); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        }
    }

    static class SafeThread2 extends Thread {
        SafeThread2() { super("SafeThread-2"); }

        @Override
        public void run() {
            synchronized (RESOURCE_A) {          // same order: A first
                System.out.println("  [" + getName() + "] Locked Resource A");
                sleep(100);
                synchronized (RESOURCE_B) {      // then B
                    System.out.println("  [" + getName() + "] Locked Resource B ✅");
                }
            }
        }

        private void sleep(int ms) {
            try { Thread.sleep(ms); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        }
    }

    // ── FIX 2: tryLock with timeout (using ReentrantLock) ───
    static class TryLockThread extends Thread {
        private final java.util.concurrent.locks.ReentrantLock lockA;
        private final java.util.concurrent.locks.ReentrantLock lockB;

        TryLockThread(String name,
                      java.util.concurrent.locks.ReentrantLock first,
                      java.util.concurrent.locks.ReentrantLock second) {
            super(name);
            this.lockA = first;
            this.lockB = second;
        }

        @Override
        public void run() {
            try {
                boolean gotA = lockA.tryLock(200, java.util.concurrent.TimeUnit.MILLISECONDS);
                if (!gotA) {
                    System.out.println("  [" + getName() + "] ⚠️  Couldn't get Lock-A, backing off.");
                    return;
                }
                System.out.println("  [" + getName() + "] 🔒 Got Lock-A");
                Thread.sleep(50);

                boolean gotB = lockB.tryLock(200, java.util.concurrent.TimeUnit.MILLISECONDS);
                if (!gotB) {
                    System.out.println("  [" + getName() + "] ⚠️  Couldn't get Lock-B, releasing A and backing off.");
                    lockA.unlock();
                    return;
                }
                System.out.println("  [" + getName() + "] 🔒 Got Lock-B → doing work ✅");
                lockB.unlock();
                lockA.unlock();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public static void demo() throws InterruptedException {
        System.out.println("\n╔══════════════════════════════════════════════╗");
        System.out.println("║     CONCEPT 6: Deadlock                      ║");
        System.out.println("╚══════════════════════════════════════════════╝");

        // Show the cause (don't run it — it would hang forever)
        showDeadlockCause();

        // Fix 1: Consistent lock ordering
        System.out.println("--- Fix 1: Consistent Lock Ordering ---");
        Thread t1 = new SafeThread1();
        Thread t2 = new SafeThread2();
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        System.out.println("  Both threads completed without deadlock!");

        // Fix 2: tryLock with timeout
        System.out.println("\n--- Fix 2: ReentrantLock with tryLock (timeout-based) ---");
        var lockA = new java.util.concurrent.locks.ReentrantLock();
        var lockB = new java.util.concurrent.locks.ReentrantLock();

        // Intentional resource order reversal to show tryLock handles it
        Thread tl1 = new TryLockThread("TryThread-1", lockA, lockB);
        Thread tl2 = new TryLockThread("TryThread-2", lockB, lockA); // reversed order
        tl1.start();
        tl2.start();
        tl1.join();
        tl2.join();

        System.out.println("""
        
        ✅ Deadlock demo complete!
        📌 Prevention strategies:
           1. Lock ordering  → always acquire locks in the same global order
           2. tryLock()      → attempt lock with timeout, back off if failed
           3. Avoid nested locks → minimize synchronized blocks
           4. Use higher-level concurrency tools (ExecutorService, ConcurrentHashMap)
        """);
    }
}
