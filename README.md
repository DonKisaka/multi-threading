# ☕ Java Multithreading — Complete Learning Lab

A fully interactive Java project covering all core multithreading concepts with real, runnable examples and clear console output.

## 📂 Project Structure

```
JavaMultithreading/
├── src/multithreading/
│   ├── Main.java               ← Entry point (interactive menu)
│   ├── BasicThreads.java       ← Concept 1: 3 ways to create threads
│   ├── ThreadLifecycle.java    ← Concept 2: States, priority, daemon
│   ├── Synchronization.java    ← Concept 3: Race conditions, volatile
│   ├── ProducerConsumer.java   ← Concept 4: wait() / notify()
│   ├── ThreadPool.java         ← Concept 5: ExecutorService, CompletableFuture
│   ├── DeadlockDemo.java       ← Concept 6: Deadlock cause & prevention
│   └── AdvancedConcurrency.java← Concept 7: Latch, Barrier, Semaphore
├── out/                        ← Compiled .class files
└── run.sh                      ← Run script
```

## 🚀 How to Run

### Requirements
- Java JDK 11 or higher

### Steps
```bash
# 1. Compile
javac -d out src/multithreading/*.java

# 2. Run
java -cp out multithreading.Main

# OR use the script
chmod +x run.sh && ./run.sh
```

## 📚 Concepts Covered

| # | Concept | What You'll Learn |
|---|---------|-------------------|
| 1 | **Basic Thread Creation** | Thread class, Runnable, Lambda |
| 2 | **Thread Lifecycle** | NEW→RUNNABLE→RUNNING→WAITING→TERMINATED, Priority, Daemon |
| 3 | **Synchronization** | Race conditions, `synchronized`, `volatile`, `AtomicInteger` |
| 4 | **Producer-Consumer** | `wait()`, `notify()`, bounded buffer |
| 5 | **Thread Pools** | `ExecutorService`, `Future`, `ScheduledExecutor`, `CompletableFuture` |
| 6 | **Deadlock** | Circular wait, lock ordering fix, `tryLock` fix |
| 7 | **Advanced Utils** | `CountDownLatch`, `CyclicBarrier`, `Semaphore`, `ConcurrentHashMap` |

## 💡 Key Takeaways

- **Never** share mutable state across threads without synchronization
- **Prefer** `Runnable` / `ExecutorService` over extending `Thread`
- **Use** `AtomicInteger` for simple counters instead of `synchronized`
- **Avoid** nested locks to prevent deadlocks
- **Use** `ConcurrentHashMap` instead of `HashMap` in multi-threaded code
- **`wait()`** releases the lock; **`sleep()`** does NOT
