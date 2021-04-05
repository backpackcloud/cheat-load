package com.backpackcloud.cheatload.impl.cdi;

import com.backpackcloud.cheatload.Broadcaster;
import io.vertx.mutiny.core.eventbus.EventBus;

public class StandaloneBroadcaster implements Broadcaster {

  private final EventBus eventBus;

  public StandaloneBroadcaster(EventBus eventBus) {
    this.eventBus = eventBus;
  }

  @Override
  public void broadcast(String eventName, Object object) {
    eventBus.publish(eventName, object);
  }
}
