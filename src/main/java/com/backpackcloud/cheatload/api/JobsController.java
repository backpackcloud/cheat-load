package com.backpackcloud.cheatload.api;


import com.backpackcloud.cheatload.JobRequest;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.UUID;

@Path("/jobs")
public interface JobsController {

  @POST
  @Consumes("application/json")
  @Produces("application/json")
  Response createJobs(List<JobRequest> requests);

  @GET
  @Path("/{id}")
  @Produces("application/json")
  Response getJob(@PathParam("id") UUID id);

  @GET
  @Produces("application/json")
  Response getJobs();

  @DELETE
  Response clearJobs();

}
