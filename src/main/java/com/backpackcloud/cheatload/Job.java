package com.backpackcloud.cheatload;

import com.backpackcloud.papi.hateoas.Link;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

@Link(uri = "/jobs/{id}", rel = "_self")
public class Job<E extends JobSpec> {

  private final UUID id;
  private final E spec;
  private final JobStatistics statistics;
  private JobState state;

  public Job(UUID id, E spec) {
    this.id = id;
    this.spec = spec;
    this.state = JobState.WAITING;
    this.statistics = new JobStatistics();
  }

  @JsonProperty
  public UUID id() {
    return id;
  }

  @JsonProperty
  public E spec() {
    return spec;
  }

  @JsonProperty
  public JobState state() {
    return state;
  }

  @JsonProperty
  public JobStatistics statistics() {
    return statistics;
  }

  public void pick() {
    if (state == JobState.WAITING) {
      state = JobState.RUNNING;
      statistics.start();
    }
  }

  public void fail() {
    if (state == JobState.RUNNING) {
      state = JobState.FAIL;
      statistics.end();
    }
  }

  public void done() {
    if (state == JobState.RUNNING) {
      state = JobState.SUCCESS;
      statistics.end();
    }
  }

  @JsonIgnore
  public boolean isWaiting() {
    return state() == JobState.WAITING;
  }

  @JsonIgnore
  public boolean isRunning() {
    return state() == JobState.RUNNING;
  }

  @JsonIgnore
  public boolean isFinished() {
    return state() == JobState.FAIL || state() == JobState.SUCCESS;
  }

}
