package com.backpackcloud.cheatload.impl.cdi;

import com.backpackcloud.cheatload.*;
import com.backpackcloud.cheatload.impl.StandaloneJobManager;
import com.backpackcloud.cheatload.impl.jobs.InfinispanJob;
import com.backpackcloud.zipper.Serializer;
import io.quarkus.vertx.LocalEventBusCodec;
import io.vertx.mutiny.core.eventbus.EventBus;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import java.io.File;

@ApplicationScoped
public class JobManagerProducer {

  @Produces
  public JobManager getJobManager(@ConfigProperty(name = "config.file", defaultValue = "") String configFile,
                                  EventBus eventBus) {
    registerCodecs(eventBus);
    StandaloneBroadcaster broadcaster = new StandaloneBroadcaster(eventBus);

    Serializer yaml = Serializer.yaml();
    yaml.addDependency(Broadcaster.class, broadcaster);

    LoadStormConfig config = yaml.deserialize(new File(configFile), LoadStormConfig.class);

    return new StandaloneJobManager(config.executors(), config.threads(), broadcaster);
  }

  private void registerCodecs(EventBus eventBus) {
    eventBus.getDelegate().registerDefaultCodec(Job.class, new LocalEventBusCodec<>());
    eventBus.getDelegate().registerDefaultCodec(InfinispanJob.class, new LocalEventBusCodec<>());
    eventBus.getDelegate().registerDefaultCodec(JobSnapshot.class, new LocalEventBusCodec<>());
    eventBus.getDelegate().registerDefaultCodec(JobEvent.class, new LocalEventBusCodec<>());
  }

}
