package multithreading;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

// ============================================================
//  CONCEPT 5: Thread Pools & ExecutorService
// ============================================================

public class ThreadPool {

    // ── Simulated task: download a file ─────────────────────
    static class DownloadTask implements Callable<String> {
        private final String fileName;
        private final int durationMs;

        DownloadTask(String fileName, int durationMs) {
            this.fileName   = fileName;
            this.durationMs = durationMs;
        }

        @Override
        public String call() throws Exception {
            System.out.printf("  [%s] ⬇️  Downloading %s ...%n",
                    Thread.currentThread().getName(), fileName);
            Thread.sleep(durationMs);
            System.out.printf("  [%s] ✅ Done: %s%n",
                    Thread.currentThread().getName(), fileName);
            return "Downloaded: " + fileName;
        }
    }

    public static void demo() throws InterruptedException, ExecutionException {
        System.out.println("\n╔══════════════════════════════════════════════╗");
        System.out.println("║     CONCEPT 5: Thread Pools & Executor       ║");
        System.out.println("╚══════════════════════════════════════════════╝");

        System.out.println("""
        
        Problem: Creating a new Thread for every task is expensive!
        Solution: Thread Pools — reuse a fixed set of threads.
        
        Types of ExecutorService:
          • newFixedThreadPool(n)    → exactly n threads always alive
          • newCachedThreadPool()    → grows/shrinks as needed  
          • newSingleThreadExecutor()→ exactly 1 thread (serial)
          • newScheduledThreadPool() → for delayed/repeated tasks
        """);

        // ── 1. Fixed Thread Pool ─────────────────────────────
        System.out.println("--- Fixed Thread Pool (3 threads, 7 tasks) ---");
        ExecutorService fixedPool = Executors.newFixedThreadPool(3);

        String[] files = {"report.pdf", "image.png", "video.mp4",
                          "data.csv",   "notes.txt", "app.jar", "music.mp3"};

        List<Future<String>> futures = new ArrayList<>();
        for (String f : files) {
            futures.add(fixedPool.submit(new DownloadTask(f, 300)));
        }

        // Collect results
        System.out.println("\n  Results:");
        for (Future<String> future : futures) {
            System.out.println("  📦 " + future.get()); // blocks until done
        }

        fixedPool.shutdown();
        fixedPool.awaitTermination(10, TimeUnit.SECONDS);

        // ── 2. Scheduled Thread Pool ─────────────────────────
        System.out.println("\n--- Scheduled Thread Pool (delayed & periodic) ---");
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

        // Run once after 500ms delay
        scheduler.schedule(() ->
                System.out.println("  ⏰ [Delayed]  Backup started! (500ms delay)"),
                500, TimeUnit.MILLISECONDS);

        // Run every 400ms, starting after 200ms
        ScheduledFuture<?> periodic = scheduler.scheduleAtFixedRate(() ->
                System.out.println("  🔁 [Periodic] Health check! (every 400ms)"),
                200, 400, TimeUnit.MILLISECONDS);

        Thread.sleep(1400);
        periodic.cancel(false); // stop the periodic task
        scheduler.shutdown();
        scheduler.awaitTermination(2, TimeUnit.SECONDS);

        // ── 3. CompletableFuture (modern async) ──────────────
        System.out.println("\n--- CompletableFuture (Java 8+ async pipeline) ---");
        CompletableFuture<String> cf = CompletableFuture
                .supplyAsync(() -> {
                    System.out.println("  [CF] Step 1: Fetching user from DB...");
                    sleep(200);
                    return "User{id=42, name='Alice'}";
                })
                .thenApply(user -> {
                    System.out.println("  [CF] Step 2: Enriching user → " + user);
                    sleep(100);
                    return user + " | role=ADMIN";
                })
                .thenApply(enriched -> {
                    System.out.println("  [CF] Step 3: Formatting response...");
                    return "✅ Response: " + enriched;
                });

        System.out.println("  [Main] Doing other work while CF runs...");
        System.out.println("  [Main] CF result → " + cf.get()); // get final result

        System.out.println("""
        
        ✅ Thread Pool demo complete!
        📌 Key points:
           • Always call shutdown() to release pool resources
           • Use Future.get() to retrieve async results (blocks until ready)
           • CompletableFuture enables elegant async pipelines
           • Prefer thread pools over raw Thread creation
        """);
    }

    private static void sleep(int ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }
}
