package com.backpackcloud.cheatload;

public interface Broadcaster {

  void broadcast(String eventName, Object object);

  default void broadcast(JobEvent jobEvent) {
    broadcast(jobEvent.event(), jobEvent);
  }

}
