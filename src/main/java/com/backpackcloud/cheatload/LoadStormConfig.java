package com.backpackcloud.cheatload;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class LoadStormConfig {

  private final List<JobPicker> executors;
  private final int threads;

  public LoadStormConfig(@JsonProperty("executors") List<JobPicker> executors,
                         @JsonProperty("threads") Integer threads) {
    this.executors = executors;
    this.threads = Optional.ofNullable(threads).orElse(1);
  }

  public List<JobPicker> executors() {
    return Collections.unmodifiableList(executors);
  }

  public int threads() {
    return threads;
  }

}
