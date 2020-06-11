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

package com.flipkart.gojira.sample.app.module;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flipkart.gojira.sample.app.http.ISampleAppHttpHelper;
import com.flipkart.gojira.sample.app.http.SampleAppHttpHelper;
import com.google.inject.AbstractModule;

public class SampleAppServiceModule extends AbstractModule {
  private static final ObjectMapper mapper = new ObjectMapper();

  @Override
  protected void configure() {
    bind(ISampleAppHttpHelper.class).to(SampleAppHttpHelper.class).asEagerSingleton();
    bind(ObjectMapper.class).toInstance(mapper);
    install(new SampleAppGojiraModule());
  }
}
