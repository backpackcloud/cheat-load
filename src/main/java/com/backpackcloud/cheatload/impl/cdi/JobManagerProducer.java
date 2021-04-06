package com.backpackcloud.cheatload.impl.cdi;

import com.backpackcloud.cheatload.*;
import com.backpackcloud.cheatload.impl.StandaloneJobManager;
 import com.backpackcloud.cheatload.impl.executors.InfinispanJobExecutor;
import com.backpackcloud.cheatload.impl.jobs.InfinispanJob;
import com.backpackcloud.zipper.Configuration;
import com.backpackcloud.zipper.Selector;
import com.backpackcloud.zipper.Serializer;
import io.quarkus.vertx.LocalEventBusCodec;
import io.vertx.mutiny.core.eventbus.EventBus;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Singleton;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static com.backpackcloud.zipper.Configuration.*;

@ApplicationScoped
public class JobManagerProducer {

  @Produces
  @Singleton
  public JobManager getJobManager(@ConfigProperty(name = "config.file", defaultValue = "__defaults__") String configFile,
                                  EventBus eventBus) {
    registerCodecs(eventBus);

    if ("__defaults__".equals(configFile)) {
      return defaultJobManager(eventBus);
    }

    StandaloneBroadcaster broadcaster = new StandaloneBroadcaster(eventBus);
    Serializer yaml = Serializer.yaml();
    yaml.addDependency(Broadcaster.class, broadcaster);

    LoadStormConfig config = yaml.deserialize(new File(configFile), LoadStormConfig.class);

    return new StandaloneJobManager(config.executors(), config.threads(), broadcaster);
  }

  private void registerCodecs(EventBus eventBus) {
    eventBus.getDelegate().registerDefaultCodec(InfinispanJob.class, new LocalEventBusCodec<>());
    eventBus.getDelegate().registerDefaultCodec(JobSnapshot.class, new LocalEventBusCodec<>());
    eventBus.getDelegate().registerDefaultCodec(JobEvent.class, new LocalEventBusCodec<>());
  }

  private JobManager defaultJobManager(EventBus eventBus) {
    List<JobPicker> executors = new ArrayList<>();

    Map<String, String> hotrodClientProperties = new HashMap<>();
    hotrodClientProperties.put(
      "server_list",
      configuration()
        .env("INFINISPAN_SERVER_LIST")
        .property("infinispan.server.list")
        .orElse("localhost:11222")
    );
    hotrodClientProperties.put(
      "auth_username",
      configuration()
        .env("INFINISPAN_AUTH_USERNAME")
        .property("infinispan.auth_username")
        .orElse("developer")
    );
    hotrodClientProperties.put(
      "auth_password",
      configuration()
        .env("INFINISPAN_AUTH_PASSWOED")
        .property("infinispan.auth_password")
        .orElse("backpackcloud")
    );
    executors.add(new JobPicker(Selector.empty(), new InfinispanJobExecutor(hotrodClientProperties)));

    int threads = configuration().env("INFINISPAN_EXECUTOR_THREADS")
      .property("infinispan.executor.threads")
      .orElse(1);

    return new StandaloneJobManager(executors, 1, new StandaloneBroadcaster(eventBus));
  }

}
