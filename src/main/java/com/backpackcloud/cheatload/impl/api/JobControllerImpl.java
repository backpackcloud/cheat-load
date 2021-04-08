package com.backpackcloud.cheatload.impl.api;

import com.backpackcloud.cheatload.Job;
import com.backpackcloud.cheatload.JobManager;
import com.backpackcloud.cheatload.JobRequest;
import com.backpackcloud.cheatload.api.JobsController;
import com.backpackcloud.papi.hateoas.ApiCollectionModel;
import com.backpackcloud.papi.hateoas.ApiModel;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@ApplicationScoped
public class JobControllerImpl implements JobsController {

  @Inject
  JobManager jobManager;

  @Inject
  WebsocketController websocketController;

  @Override
  public Response createJobs(List<JobRequest> requests) {
    List<Job> jobs = requests.stream()
      .map(jobManager::submit)
      .collect(Collectors.toList());
    return ApiCollectionModel.from(jobs).toResponse(202);
  }

  @Override
  public Response getJob(UUID id) {
    return jobManager.get(id)
      .map(ApiModel::from)
      .map(ApiModel::toResponse)
      .orElseGet(() -> Response.status(404).build());
  }

  @Override
  public Response getJobs() {
    return ApiCollectionModel.from(jobManager.listAll())
      .link("/jobs").to("_self")
      .toResponse();
  }

  @Override
  public Response clearJobs() {
    jobManager.clearFinished();
    websocketController.clearJobs();
    return Response.ok().build();
  }

}