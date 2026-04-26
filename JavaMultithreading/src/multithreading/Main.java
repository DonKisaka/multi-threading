package multithreading;

import java.util.Scanner;

// ============================================================
//  Java Multithreading — Complete Learning Project
//  Covers: Thread creation, lifecycle, synchronization,
//          producer-consumer, thread pools, deadlocks,
//          and advanced concurrency utilities.
// ============================================================

public class Main {

    static final String BANNER = """
    
    ╔══════════════════════════════════════════════════════════╗
    ║         ☕  Java Multithreading — Learning Lab           ║
    ╠══════════════════════════════════════════════════════════╣
    ║  1. Basic Thread Creation  (3 ways)                      ║
    ║  2. Thread Lifecycle & States                            ║
    ║  3. Synchronization & Race Conditions                    ║
    ║  4. Producer-Consumer (wait/notify)                      ║
    ║  5. Thread Pools & ExecutorService                       ║
    ║  6. Deadlock — Cause & Prevention                        ║
    ║  7. Advanced Concurrency (Latch, Barrier, Semaphore)     ║
    ║  8. Run ALL concepts                                     ║
    ║  0. Exit                                                 ║
    ╚══════════════════════════════════════════════════════════╝
    """;

    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println(BANNER);
            System.out.print("Choose a concept (0-8): ");
            String input = scanner.nextLine().trim();

            switch (input) {
                case "1" -> BasicThreads.demo();
                case "2" -> ThreadLifecycle.demo();
                case "3" -> Synchronization.demo();
                case "4" -> ProducerConsumer.demo();
                case "5" -> ThreadPool.demo();
                case "6" -> DeadlockDemo.demo();
                case "7" -> AdvancedConcurrency.demo();
                case "8" -> {
                    System.out.println("\n🚀 Running ALL concepts...\n");
                    BasicThreads.demo();
                    ThreadLifecycle.demo();
                    Synchronization.demo();
                    ProducerConsumer.demo();
                    ThreadPool.demo();
                    DeadlockDemo.demo();
                    AdvancedConcurrency.demo();
                    System.out.println("\n🎉 All concepts complete!");
                }
                case "0" -> {
                    System.out.println("\nGoodbye! Happy threading! 👋\n");
                    scanner.close();
                    return;
                }
                default -> System.out.println("  ⚠️  Invalid option. Choose 0-8.");
            }

            System.out.println("\nPress ENTER to return to menu...");
            scanner.nextLine();
        }
    }
}
