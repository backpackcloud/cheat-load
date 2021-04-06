package com.backpackcloud.cheatload.impl.jobs;

import com.backpackcloud.cheatload.JobSpec;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Optional;
import java.util.UUID;

public class InfinispanJobSpec implements JobSpec {

  private final String name;
  private final String cache;
  private final long quantity;
  private final int size;
  private final int threads;

  @JsonCreator
  public InfinispanJobSpec(@JsonProperty("name") String name,
                           @JsonProperty("cache") String cache,
                           @JsonProperty("quantity") long quantity,
                           @JsonProperty("size") int size,
                           @JsonProperty("threads") int threads) {
    this.name = name;
    this.cache = cache;
    this.quantity = Optional.ofNullable(quantity).orElse(1000L);
    this.size = Optional.ofNullable(size).orElse(1000);
    this.threads = Optional.ofNullable(threads).orElse(1);
  }

  @Override
  public String type() {
    return "infinispan";
  }

  @JsonProperty
  public String name() {
    return name;
  }

  @JsonProperty
  public String cache() {
    return cache;
  }

  @JsonProperty
  public long quantity() {
    return quantity;
  }

  @JsonProperty
  public int size() {
    return size;
  }

  @JsonProperty
  public int threads() {
    return threads;
  }

  @Override
  public InfinispanJob newJob() {
    return new InfinispanJob(UUID.randomUUID(), this);
  }

}
