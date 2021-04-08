package com.backpackcloud.cheatload.impl.executors;

import com.backpackcloud.cheatload.Job;
import com.backpackcloud.cheatload.JobExecutor;
import com.backpackcloud.cheatload.impl.jobs.ArtemisJobSpec;
import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;

public class ArtemisJobExecutor implements JobExecutor<ArtemisJobSpec> {

  @Override
  public String type() {
    return "artemis";
  }

  @Override
  public void execute(Job<ArtemisJobSpec> job) {
    ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
  }

}
