package com.backpackcloud.cheatload;

import com.backpackcloud.papi.hateoas.Link;

import java.util.UUID;

@Link(uri = "/jobs/{id}", rel = "_self")
public interface Job {

  UUID id();

  JobSpec spec();

  JobStatistics statistics();

  boolean isRunning();

  boolean isWaiting();

  boolean isFinished();

}
