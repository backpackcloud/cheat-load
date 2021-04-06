package com.backpackcloud.cheatload.impl.jobs;

import com.backpackcloud.cheatload.Job;
import com.backpackcloud.cheatload.JobState;
import com.backpackcloud.cheatload.JobStatistics;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public class InfinispanJob implements Job {

  private final UUID id;
  private final InfinispanJobSpec definition;
  private final JobStatistics statistics;
  private JobState state;

  public InfinispanJob(UUID id, InfinispanJobSpec definition) {
    this.id = id;
    this.definition = definition;
    this.state = JobState.WAITING;
    this.statistics = new JobStatistics();
  }

  @JsonProperty
  public UUID id() {
    return id;
  }

  @JsonProperty
  public InfinispanJobSpec spec() {
    return definition;
  }

  @JsonProperty
  public JobState state() {
    return state;
  }

  @JsonProperty
  public JobStatistics statistics() {
    return statistics;
  }

  public synchronized void picked() {
    if (state == JobState.WAITING) {
      state = JobState.RUNNING;
      statistics.start();
    }
  }

  public synchronized void failed() {
    if (state == JobState.RUNNING) {
      state = JobState.FAIL;
      statistics.end();
    }
  }

  public synchronized void done() {
    if (state == JobState.RUNNING) {
      state = JobState.SUCCESS;
      statistics.end();
    }
  }

}
