package com.backpackcloud.cheatload;

import com.backpackcloud.cheatload.impl.jobs.InfinispanJobSpec;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
  @JsonSubTypes.Type(value = InfinispanJobSpec.class, name = "infinispan"),
})
public interface JobSpec {

  String type();

  Job newJob();

}
