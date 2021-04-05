package com.backpackcloud.cheatload;

import com.fasterxml.jackson.annotation.JsonProperty;

public class JobSnapshot {

  @JsonProperty
  private final long time;
  @JsonProperty
  private final double instant;
  @JsonProperty
  private final long count;

  public JobSnapshot(long time, double instant, long count) {
    this.time = time;
    this.instant = instant;
    this.count = count;
  }

  public double instant() {
    return instant;
  }

  public long count() {
    return count;
  }

  public long time() {
    return time;
  }

}
