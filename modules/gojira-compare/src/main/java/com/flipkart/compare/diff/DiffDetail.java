/*
 * Copyright 2020 Flipkart Internet, pvt ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.flipkart.compare.diff;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * DiffDetail is used to store generated diffs.
 *
 * <p>TODO: Check if we can use RFC 6902 JSON Patch
 */
public class DiffDetail {

  private static final Logger LOGGER = LoggerFactory.getLogger(DiffDetail.class);
  private static final ObjectMapper objectMapper = new ObjectMapper();
  /**
   * diffType to store type of diff.
   */
  private DiffType diffType;
  /**
   * diffPath represents the path to the object being compared Class A { int a, int b} Class C { int
   * d, A e} Class F { int g, C[] h} Class I { String k, Map{@literal <}String, F{@literal >} l,
   * List{@literal <}C{@literal >} m}.
   *
   * <p>A : [{/, a, /}, {/, b, /}] C : [{/, d, /}, {/, e, a, /}, {/, e, b, /}] F : [{/, g, /}, {/,
   * h, (.*), d, /}, {/, h, (.*), e, a, /}, {/, h, (.*), e, b, /} I : [{/, k, /}, {/, l, (.*), g,
   * /}, {/, l, (.*), h, (.*), e, a, /}, {/, l, (.*), h, (.*), e, b, /}, {/, m, (.*), d, /}, {/, m,
   * (.*), e, a, /}, {/, m, (.*), e, b, /}
   */
  private String diffPath = "";
  /**
   * value to be expected.
   */
  private Object expectedValue;
  /**
   * actual value.
   */
  private Object actualValue;

  private DiffDetail() {}

  public static Builder builder() {
    return new Builder();
  }

  public DiffType getDiffType() {
    return diffType;
  }

  public String getDiffPath() {
    return diffPath;
  }

  public Object getExpectedValue() {
    return expectedValue;
  }

  public Object getActualValue() {
    return actualValue;
  }

  @Override
  public String toString() {
    // using object mapper for writing to string.
    // see if we can remove object mapper dependency in this class.
    try {
      return objectMapper.writeValueAsString(this);
    } catch (JsonProcessingException e) {
      LOGGER.error("something  went  wrong with diff detail: ", e);
      return null;
    }
  }

  public static class Builder {

    private DiffDetail diffDetailToBuild;

    private Builder() {
      this.diffDetailToBuild = new DiffDetail();
    }

    public DiffDetail build() {
      return this.diffDetailToBuild;
    }

    public Builder setDiffType(DiffType diffType) {
      this.diffDetailToBuild.diffType = diffType;
      return this;
    }

    public Builder setDiffPath(String diffPath) {
      this.diffDetailToBuild.diffPath = diffPath;
      return this;
    }

    public Builder setExpectedValue(Object expectedValue) {
      this.diffDetailToBuild.expectedValue = expectedValue;
      return this;
    }

    public Builder setActualValue(Object actualValue) {
      this.diffDetailToBuild.actualValue = actualValue;
      return this;
    }
  }
}
