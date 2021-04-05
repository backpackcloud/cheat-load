package com.backpackcloud.cheatload;

import com.backpackcloud.zipper.TagMap;
import com.backpackcloud.zipper.Taggable;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Optional;

public class JobRequest implements Taggable {

  private final JobSpec spec;
  private final TagMap tags;

  public JobRequest(@JsonProperty("spec") JobSpec spec,
                    @JsonProperty("tags") TagMap tags) {
    this.spec = spec;
    this.tags = Optional.ofNullable(tags).orElseGet(TagMap::empty);
  }

  public JobSpec spec() {
    return spec;
  }

  @Override
  public TagMap tags() {
    return tags;
  }

}
