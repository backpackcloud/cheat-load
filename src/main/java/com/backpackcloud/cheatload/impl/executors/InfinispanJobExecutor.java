package com.backpackcloud.cheatload.impl.executors;

import com.backpackcloud.cheatload.JobExecutor;
import com.backpackcloud.cheatload.JobStatistics;
import com.backpackcloud.cheatload.impl.jobs.InfinispanJob;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.Configuration;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.jboss.logging.Logger;

import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class InfinispanJobExecutor implements JobExecutor<InfinispanJob> {
  private static final Logger LOGGER = Logger.getLogger(InfinispanJobExecutor.class);

  private final RemoteCacheManager remoteCacheManager;

  @JsonCreator
  public InfinispanJobExecutor(@JsonProperty("hotrod_client") Map<String, String> properties) {
    Properties configurationProperties = new Properties();

    properties.forEach((key, value) -> configurationProperties.put("infinispan.client.hotrod." + key, value));

    Configuration infinispanConfiguration = new ConfigurationBuilder()
      .withProperties(configurationProperties)
      .build();

    remoteCacheManager = new RemoteCacheManager(infinispanConfiguration);
  }

  @Override
  public String type() {
    return "infinispan";
  }

  @Override
  public void execute(InfinispanJob job) {
    LOGGER.infof("[%s] Initializing", job.id());
    int threads = job.spec().threads();
    ExecutorService dataPushExecutor = Executors.newFixedThreadPool(threads);
    AtomicLong count = new AtomicLong(0);

    RemoteCache<String, byte[]> dataTestCache = remoteCacheManager.getCache(job.spec().cache());

    job.picked();
    byte[] data = new byte[job.spec().size()];
    long entries = job.spec().quantity();
    JobStatistics statistics = job.statistics();
    for (int i = 0; i < threads; i++) {
      dataPushExecutor.execute(() -> {
        while (job.isRunning() && count.incrementAndGet() <= entries) {
          statistics.incrementCount();
          try {
            dataTestCache.put(UUID.randomUUID().toString(), data);
          } catch (Exception e) {
            LOGGER.errorf(e, "[%s] Error while adding data", job.id());
            job.failed();
          }
        }
        job.done();
      });
    }
    dataPushExecutor.shutdown();
    try {
      dataPushExecutor.awaitTermination(1, TimeUnit.DAYS);
    } catch (InterruptedException e) {
      LOGGER.errorf(e, "[%s] Error while waiting for job to finish", job.id());
    }
  }

}
