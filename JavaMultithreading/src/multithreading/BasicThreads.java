package multithreading;

// ============================================================
//  CONCEPT 1: Basic Thread Creation — 3 Ways
// ============================================================

public class BasicThreads {

    // ── WAY 1: Extend Thread class ──────────────────────────
    static class Dog extends Thread {
        private String name;

        Dog(String name) {
            super(name); // sets the thread name
            this.name = name;
        }

        @Override
        public void run() {
            for (int i = 1; i <= 3; i++) {
                System.out.println("  🐶 Dog [" + name + "] barks " + i + "x  (Thread: " + Thread.currentThread().getName() + ")");
                sleep(200);
            }
        }

        private void sleep(int ms) {
            try { Thread.sleep(ms); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        }
    }

    // ── WAY 2: Implement Runnable ───────────────────────────
    static class Cat implements Runnable {
        private String name;

        Cat(String name) { this.name = name; }

        @Override
        public void run() {
            for (int i = 1; i <= 3; i++) {
                System.out.println("  🐱 Cat [" + name + "] meows " + i + "x  (Thread: " + Thread.currentThread().getName() + ")");
                try { Thread.sleep(200); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            }
        }
    }

    public static void demo() throws InterruptedException {
        System.out.println("\n╔══════════════════════════════════════════════╗");
        System.out.println("║     CONCEPT 1: Basic Thread Creation         ║");
        System.out.println("╚══════════════════════════════════════════════╝");

        // WAY 1: Extend Thread
        System.out.println("\n--- Way 1: Extending Thread class ---");
        Dog dog1 = new Dog("Rex");
        Dog dog2 = new Dog("Buddy");
        dog1.start();
        dog2.start();
        dog1.join(); // wait for threads to finish
        dog2.join();

        // WAY 2: Implement Runnable
        System.out.println("\n--- Way 2: Implementing Runnable ---");
        Thread catThread1 = new Thread(new Cat("Whiskers"), "CatThread-1");
        Thread catThread2 = new Thread(new Cat("Mittens"), "CatThread-2");
        catThread1.start();
        catThread2.start();
        catThread1.join();
        catThread2.join();

        // WAY 3: Lambda (Java 8+)
        System.out.println("\n--- Way 3: Lambda Expression (Java 8+) ---");
        Thread lambdaThread1 = new Thread(() -> {
            for (int i = 1; i <= 3; i++) {
                System.out.println("  🦜 Parrot says 'Hello' #" + i + "  (Thread: " + Thread.currentThread().getName() + ")");
                try { Thread.sleep(200); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            }
        }, "ParrotThread-1");

        Thread lambdaThread2 = new Thread(() -> {
            for (int i = 1; i <= 3; i++) {
                System.out.println("  🦜 Parrot says 'World' #" + i + "  (Thread: " + Thread.currentThread().getName() + ")");
                try { Thread.sleep(200); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            }
        }, "ParrotThread-2");

        lambdaThread1.start();
        lambdaThread2.start();
        lambdaThread1.join();
        lambdaThread2.join();

        System.out.println("\n✅ All 3 thread creation styles demonstrated!");
        System.out.println("   Notice how outputs from threads INTERLEAVE — that's concurrency!");
    }
}
