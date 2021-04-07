package com.backpackcloud.cheatload;

import com.backpackcloud.cheatload.impl.executors.InfinispanJobExecutor;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
  @JsonSubTypes.Type(value = InfinispanJobExecutor.class, name = "infinispan"),
})
public interface JobExecutor<E extends JobSpec> {

  String type();
  void execute(Job<E> job);

}


