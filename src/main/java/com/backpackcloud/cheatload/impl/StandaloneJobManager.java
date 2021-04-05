package com.backpackcloud.cheatload.impl;

import com.backpackcloud.cheatload.*;
import org.jboss.logging.Logger;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class StandaloneJobManager implements JobManager {

  private static final Logger LOGGER = Logger.getLogger(StandaloneJobManager.class);

  private final ExecutorService executorService;

  private final ExecutorService snapshotService;
  private final Broadcaster broadcaster;

  private final List<JobPicker> jobPickers;

  private final Map<UUID, Job> results;

  public StandaloneJobManager(List<JobPicker> jobPickers,
                              int threads,
                              Broadcaster broadcaster) {
    this.jobPickers = jobPickers;
    this.executorService = Executors.newFixedThreadPool(threads);
    this.snapshotService = Executors.newFixedThreadPool(threads);
    this.broadcaster = broadcaster;
    this.results = new ConcurrentHashMap<>();
  }

  public Job submit(JobRequest jobRequest) {
    Job job = jobRequest.spec().newJob();
    JobExecutor executor = jobPickers.stream()
      .filter(picker -> picker.executor().type().equals(jobRequest.spec().type()))
      .filter(picker -> picker.selector().test(jobRequest.tags()))
      .findFirst()
      .map(JobPicker::executor)
      .orElseThrow(() -> new RequestException("There is no executor suitable for picking the given job."));
    executorService.submit(() -> executor.execute(job));
    snapshotService.submit(() -> {
      JobSnapshot snapshot;
      while (!job.isFinished()) {
        snapshot = job.statistics().takeSnapshot();
        broadcaster.broadcast("job.snapshot.taken", snapshot);
        try {
          Thread.sleep(1000);
        } catch (InterruptedException e) {
          LOGGER.errorf(e, "[%s] Error while waiting for next snapshot generation", job.id());
        }
      }
    });
    results.put(job.id(), job);
    broadcaster.broadcast(new JobEvent("job.created", job));
    return job;
  }

  public Optional<Job> get(UUID id) {
    return Optional.ofNullable(results.get(id));
  }

  public Collection<Job> listAll() {
    return results.values();
  }

  public Collection<Job> listRunning() {
    return filter(Job::isRunning);
  }

  public Collection<Job> listWaiting() {
    return filter(Job::isWaiting);
  }

  public Collection<Job> listFinished() {
    return filter(Job::isFinished);
  }

  public void clearFinished() {
    results.entrySet().stream()
      .filter(entry -> entry.getValue().isFinished())
      .forEach(entry -> results.remove(entry.getKey()));
  }

  private List<Job> filter(Predicate<Job> predicate) {
    return results.values().stream()
      .filter(predicate)
      .collect(Collectors.toList());
  }


}
