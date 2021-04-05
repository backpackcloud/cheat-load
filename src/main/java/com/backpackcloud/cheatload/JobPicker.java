package com.backpackcloud.cheatload;

import com.backpackcloud.zipper.Selector;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Optional;

public class JobPicker<E extends Job> {

  private final Selector selector;

  private final JobExecutor executor;

  @JsonCreator
  public JobPicker(@JsonProperty("selector") Selector selector,
                   @JsonProperty("executor") JobExecutor executor) {
    this.selector = Optional.ofNullable(selector).orElseGet(Selector::empty);
    this.executor = executor;
  }

  public Selector selector() {
    return selector;
  }

  public JobExecutor executor() {
    return executor;
  }

}
