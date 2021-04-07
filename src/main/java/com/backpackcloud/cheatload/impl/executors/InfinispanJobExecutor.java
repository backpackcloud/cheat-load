package com.backpackcloud.cheatload.impl.executors;

import com.backpackcloud.cheatload.Job;
import com.backpackcloud.cheatload.JobExecutor;
import com.backpackcloud.cheatload.impl.jobs.InfinispanJobSpec;
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

public class InfinispanJobExecutor implements JobExecutor<InfinispanJobSpec> {
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
  public void execute(Job<InfinispanJobSpec> job) {
    LOGGER.infof("[%s] Initializing", job.id());

    RemoteCache<String, byte[]> cache = remoteCacheManager.getCache(job.spec().cache());
    byte[] data = new byte[job.spec().size()];

    new JobRunner<>(job).run(n -> cache.put(UUID.randomUUID().toString(), data));
  }

}
