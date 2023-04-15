package alloyfl.mutation.util;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ExecutorExporter {
  private static ThreadFactory newDefaultThreadFactory(String threadPoolName) {
    return new ThreadFactoryBuilder().setNameFormat(threadPoolName + "-%d").build();
  }

  public static ExecutorService newFixedThreadPool(
      int nThreads,
      String threadPoolName) {
    return Executors.newFixedThreadPool(nThreads, newDefaultThreadFactory(threadPoolName));
  }

  /**
   * Creates a fixed thread pool executor with a bounded blocking queue.
   *
   * <p>If the bounded queue is full then submitting a task will run it on the submitting thread.
   * The queueCapacity should be large enough to ensure that if the task is running on the
   * submitting thread, that the queue won't drain fully.
   */
  public static ExecutorService newFixedThreadPoolWithBoundedBlockingQueue(
      int nThreads,
      int queueCapacity,
      String threadPoolName) {
    ArrayBlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(queueCapacity);
    ThreadFactory threadFactory = newDefaultThreadFactory(threadPoolName);
    return new ThreadPoolExecutor(
        nThreads,
        nThreads,
        0L,
        TimeUnit.MILLISECONDS,
        queue,
        threadFactory,
        new ThreadPoolExecutor.CallerRunsPolicy());
  }

  public static ExecutorService newCachedThreadPool(
      String threadPoolName) {
    return Executors.newCachedThreadPool(newDefaultThreadFactory(threadPoolName));
  }
}
