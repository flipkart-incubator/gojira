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

/**
 * Sample application guice module containing all configurations that need to be installed.
 */
public class SampleAppServiceModule extends AbstractModule {
  private static final ObjectMapper mapper = new ObjectMapper();

  /**
   * Installs/Binds the following:
   * 1. {@link ISampleAppHttpHelper} to {@link SampleAppHttpHelper} which will be used for making
   * external HTTP calls from this application. The methods of this application have been
   * annotated with {@link com.flipkart.gojira.core.annotations.ProfileOrTest}, for method
   * interception to work in order to enable capturing rpc calls.
   * 2. Binds {@link ObjectMapper} to an instance, so the same can be used across the app, instead
   * of having to create new ones.
   * 3. {@link SampleAppServiceModule} which contains all configurations for testing.
   */
  @Override
  protected void configure() {
    bind(ISampleAppHttpHelper.class).to(SampleAppHttpHelper.class).asEagerSingleton();
    bind(ObjectMapper.class).toInstance(mapper);
    install(new SampleAppGojiraModule());
  }
}
