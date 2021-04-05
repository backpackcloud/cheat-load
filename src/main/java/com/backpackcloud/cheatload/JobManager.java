package com.backpackcloud.cheatload;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface JobManager {

  Job submit(JobRequest definition);

  Optional<Job> get(UUID id);

  Collection<Job> listAll();

  Collection<Job> listRunning();

  Collection<Job> listWaiting();

  Collection<Job> listFinished();

}
