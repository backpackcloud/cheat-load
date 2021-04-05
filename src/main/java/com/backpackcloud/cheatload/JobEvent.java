package com.backpackcloud.cheatload;

import com.fasterxml.jackson.annotation.JsonProperty;

public class JobEvent {

  private final String event;
  private final Job job;

  public JobEvent(String event, Job job) {
    this.event = event;
    this.job = job;
  }

  @JsonProperty
  public String event() {
    return event;
  }

  @JsonProperty
  public Job job() {
    return job;
  }

}
