package com.backpackcloud.cheatload;

import com.backpackcloud.papi.hateoas.Link;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

@Link(uri = "/jobs/{id}", rel = "_self")
public interface Job {

  @JsonProperty
  UUID id();

  @JsonProperty
  JobSpec spec();

  @JsonProperty
  JobStatistics statistics();

  @JsonProperty
  JobState state();

  @JsonIgnore
  default boolean isWaiting() {
    return state() == JobState.WAITING;
  }

  @JsonIgnore
  default boolean isRunning() {
    return state() == JobState.RUNNING;
  }

  @JsonIgnore
  default boolean isFinished() {
    return state() == JobState.FAIL || state() == JobState.SUCCESS;
  }

}
