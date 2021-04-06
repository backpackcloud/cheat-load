package com.backpackcloud.cheatload.impl.executors;

import com.backpackcloud.cheatload.Job;
import com.backpackcloud.cheatload.JobStatistics;
import org.jboss.logging.Logger;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

public class JobRunner {

  private static final Logger LOGGER = Logger.getLogger(JobRunner.class);

  private final Consumer<Long> action;

  public JobRunner(Consumer<Long> action) {
    this.action = action;
  }

  public void run(Job job) {
    LOGGER.infof("[%s] Initializing", job.id());
    int threads = job.spec().threads();
    ExecutorService executorService = Executors.newFixedThreadPool(threads);
    AtomicLong count = new AtomicLong(0);

    long entries = job.spec().quantity();
    JobStatistics statistics = job.statistics();
    for (int i = 0; i < threads; i++) {
      executorService.execute(() -> {
        while (job.isRunning()) {
          long n = count.incrementAndGet();
          if (!(n <= entries)) break;

          statistics.incrementCount();
          action.accept(n);
        }
      });
    }
    executorService.shutdown();
    try {
      executorService.awaitTermination(1, TimeUnit.DAYS);
    } catch (InterruptedException e) {
      LOGGER.errorf(e, "[%s] Error while waiting for job to finish", job.id());
    }
  }

}
