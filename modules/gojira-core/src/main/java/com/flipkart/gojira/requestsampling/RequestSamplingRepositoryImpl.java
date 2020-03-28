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

package com.flipkart.gojira.requestsampling;

import com.flipkart.gojira.core.Mode;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Implementation for {@link RequestSamplingRepository}
 */
public class RequestSamplingRepositoryImpl extends RequestSamplingRepository {

  /**
   * @return
   */
  @Override
  public double getSamplingPercentage() {
    return super.samplingPercentage;
  }

  /**
   * @param samplingPercentage This method sets the sampling percentage for {@link Mode#PROFILE}
   *                           mode in
   */
  @Override
  void setSamplingPercentage(double samplingPercentage) {
    super.samplingPercentage = samplingPercentage;
  }

  /**
   * @return
   */
  @Override
  public List<Pattern> getWhitelist() {
    return whitelist;
  }

  /**
   * @param whitelist This method sets the whiteList for {@link Mode#PROFILE} mode in
   */
  @Override
  void setWhitelist(List<Pattern> whitelist) {
    super.whitelist = whitelist;
  }
}
