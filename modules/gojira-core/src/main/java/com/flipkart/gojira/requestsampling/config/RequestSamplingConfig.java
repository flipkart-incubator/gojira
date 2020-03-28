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

package com.flipkart.gojira.requestsampling.config;

import com.flipkart.gojira.core.Mode;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * RequestSamplingConfig holds all configuration for sampling requests. This needs to be provided by
 * the client application.
 */
public class RequestSamplingConfig {

  /**
   * In {@link Mode#PROFILE} mode, requests are profiled based on the sampling percentage. This is a
   * time based sampling percentage. For this last 4 digits of {@link System#nanoTime()} is used. If
   * the value is less than samplingPercentage * 100, it is profiled else not.
   */
  private double samplingPercentage = 0.00d;

  /**
   * In {@link Mode#PROFILE} mode, requests are profiled only those URIs which are white-listed. e.g
   * GET /myresource/version/(.*) would profile all GET requests which being with
   * /myresource/version/(.*)
   */
  private List<Pattern> whitelist = null;

  private RequestSamplingConfig() {

  }

  public static Builder builder() {
    return new Builder();
  }

  public double getSamplingPercentage() {
    return samplingPercentage;
  }

  public List<Pattern> getWhitelist() {
    return whitelist;
  }

  public static class Builder {

    private RequestSamplingConfig requestSamplingConfigToBuild;

    private Builder() {
      this.requestSamplingConfigToBuild = new RequestSamplingConfig();
    }

    public RequestSamplingConfig build() {
      return this.requestSamplingConfigToBuild;
    }

    public Builder setSamplingPercentage(double samplingPercentage) {
      // multiply and divide by 100.00 for setting the precision to two decimal places
      this.requestSamplingConfigToBuild.samplingPercentage = (samplingPercentage * 100.00 / 100.00);
      return this;
    }

    public Builder setWhitelist(List<String> whitelist) {
      if (whitelist == null) {
        return this;
      }

      this.requestSamplingConfigToBuild.whitelist = new ArrayList<>();
      for (String whitelisted : whitelist) {
        Pattern pattern = Pattern.compile(whitelisted, Pattern.CASE_INSENSITIVE);
        this.requestSamplingConfigToBuild.whitelist.add(pattern);
      }
      return this;
    }
  }
}
