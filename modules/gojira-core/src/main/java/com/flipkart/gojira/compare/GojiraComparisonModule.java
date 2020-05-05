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

package com.flipkart.gojira.compare;

import com.flipkart.compare.diff.DiffDetail;
import com.flipkart.compare.diff.DiffIgnoreRepository;
import com.flipkart.compare.diff.DiffIgnoreRepositoryImpl;
import com.flipkart.compare.handlers.TestCompareHandler;
import com.flipkart.gojira.compare.config.GojiraComparisonConfig;
import com.google.inject.AbstractModule;

/**
 * Guice module that needs to be installed for enabling comparison inside gojira. It handles which
 * compare handlers to pick from CompareHandlerRepository and allows for setting up of ignorable
 * {@link DiffDetail} patterns
 */
public class GojiraComparisonModule extends AbstractModule {

  private final GojiraComparisonConfig gojiraComparisonConfig;

  public GojiraComparisonModule(GojiraComparisonConfig gojiraComparisonConfig) {
    this.gojiraComparisonConfig = gojiraComparisonConfig;
  }

  @Override
  protected void configure() {
    GojiraCompareHandlerRepository gojiraCompareHandlerRepository =
        new GojiraCompareHandlerRepositoryImpl();
    gojiraCompareHandlerRepository
        .setDefaultCompareHandler(gojiraComparisonConfig.getDefaultCompareHandler());
    gojiraCompareHandlerRepository
        .setResponseDataCompareHandler(gojiraComparisonConfig.getResponseDataCompareHandler());
    bind(GojiraCompareHandlerRepository.class).toInstance(gojiraCompareHandlerRepository);

    DiffIgnoreRepository diffIgnoreRepository = new DiffIgnoreRepositoryImpl();
    diffIgnoreRepository.setupDiffIgnorePatterns(gojiraComparisonConfig.getDiffIgnoreMap());
    bind(DiffIgnoreRepository.class).toInstance(diffIgnoreRepository);

    requestStaticInjection(TestCompareHandler.class);
  }
}
