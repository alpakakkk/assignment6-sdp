package edu.transit;

import java.util.*;
import java.util.concurrent.*;


public final class ProducerRunner {
    private final int threads;
    public ProducerRunner(int threads) {
        if (threads < 1) throw new IllegalArgumentException();
        this.threads = threads;
    }
    public void run(int producers, int perProducer, Subject<TransitEvent>feed) throws InterruptedException {
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        try {
            List<Future< ? >> tasks = new ArrayList<>();
            for (int i = 0; i < producers; i++) tasks.add(pool.submit(() -> {
                for (int j = 0; j < perProducer; j++) feed.publish(TransitEvent.of(EventType.ARRIVAL, "R10", "S1", j));
            }));
            for (Future< ? > task: tasks) {
                try {
                    task.get();
                } catch (ExecutionException e) {
                    throw new IllegalStateException("Producer failed", e.getCause());
                }
            }
        } finally {
            pool.shutdown();
            if (!pool.awaitTermination(30, TimeUnit.SECONDS)) {
                pool.shutdownNow();
                throw new IllegalStateException("Executor did not terminate");
            }
        }
    }
}
