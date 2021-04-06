package com.backpackcloud.cheatload;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class JobStatistics {

  @JsonProperty
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSS")
  private LocalDateTime start;

  @JsonIgnore
  private long startMillis;

  @JsonProperty
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSS")
  private LocalDateTime end;

  @JsonProperty
  private AtomicLong count = new AtomicLong(0);

  private List<JobSnapshot> snapshots = new ArrayList<>();

  public void incrementCount() {
    count.incrementAndGet();
  }

  public synchronized void start() {
    if (start == null) {
      start = LocalDateTime.now();
      startMillis = System.currentTimeMillis();
    }
  }

  public synchronized void end() {
    if (end == null) {
      end = LocalDateTime.now();
      takeSnapshot();
    }
  }

  public synchronized JobSnapshot takeSnapshot() {
    long currentTime = System.currentTimeMillis();
    double instant = (currentTime - startMillis) / 1000.0;
    long count = this.count.get();

    JobSnapshot snapshot = new JobSnapshot(currentTime, instant, count);
    snapshots.add(snapshot);
    return snapshot;
  }

  public long count() {
    return count.get();
  }

  @JsonProperty
  public List<JobSnapshot> snapshots() {
    return Collections.unmodifiableList(snapshots);
  }

  @JsonProperty
  public long duration() {
    if (start == null) {
      return 0;
    }

    Duration duration = Duration.between(start, (end != null ? end : LocalDateTime.now()));
    return duration.toMillis();
  }

}
