package com.backpackcloud.cheatload.impl.api;

import com.backpackcloud.cheatload.Job;
import com.backpackcloud.cheatload.JobEvent;
import com.backpackcloud.cheatload.JobManager;
import com.backpackcloud.papi.hateoas.ApiCollectionModel;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.websocket.OnClose;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint("/jobs")
@ApplicationScoped
public class WebsocketController {

  private static final Logger LOGGER = Logger.getLogger(WebsocketController.class);

  private final Map<String, Session> sessions = new ConcurrentHashMap<>();

  @Inject
  JobManager jobManager;

  @Inject
  ObjectMapper objectMapper;

  @OnOpen
  public void register(Session session) {
    LOGGER.infof("Registering session {%s}", session.getId());
    sessions.put(session.getId(), session);
    session.getAsyncRemote().sendObject(ApiCollectionModel.from(jobManager.listAll()));
  }

  @OnClose
  public void unregister(Session session) {
    LOGGER.infof("Unregistering session {%s}", session.getId());
    sessions.remove(session.getId());
  }



  private void sendUpdate(String eventName, Job job) {
    sessions.values().forEach(session -> {
      try {
        session.getAsyncRemote().sendObject(objectMapper.writeValueAsString(new JobEvent(eventName, job)));
      } catch (JsonProcessingException e) {
        e.printStackTrace();
      }
    });
  }

}
