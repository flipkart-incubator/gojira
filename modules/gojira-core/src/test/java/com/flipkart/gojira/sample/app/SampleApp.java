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

package com.flipkart.gojira.sample.app;

import com.flipkart.gojira.core.HttpFilter;
import com.flipkart.gojira.sample.app.api.SampleAppGithubResource;
import com.flipkart.gojira.sample.app.api.SampleAppHttpBinPostResource;
import com.flipkart.gojira.sample.app.module.SampleAppDI;
import com.flipkart.gojira.sample.app.module.SampleAppServiceModule;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import java.util.EnumSet;
import javax.servlet.DispatcherType;

/**
 * Sample Dropwizard application.
 */
public class SampleApp extends Application<SampleAppConfiguration> {

  public static void main(final String[] args) throws Exception {
    new SampleApp().run("server");
  }

  @Override
  public String getName() {
    return "GojiraSample";
  }

  @Override
  public void initialize(final Bootstrap<SampleAppConfiguration> bootstrap) {
    // TODO: application initialization
  }

  /**
   * 1. Installs {@link SampleAppServiceModule} which has application related configurations to be
   * installed and {@link com.flipkart.gojira.sample.app.module.SampleAppGojiraModule}.
   * 2. Add {@link HttpFilter} to environment-servlets so that requests can be intercepted and
   * gojira executions in various {@link com.flipkart.gojira.core.Mode} can be begun.
   * 3. Registers a couple of resources for enabling demo/test.
   *
   * @param configuration application configuration.
   * @param environment dropwizard application environment.
   */
  @Override
  public void run(final SampleAppConfiguration configuration, final Environment environment) {
    SampleAppDI.install(new SampleAppServiceModule());

    environment
        .servlets()
        .addFilter("test-filter", new HttpFilter())
        .addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, "*");

    final SampleAppGithubResource resource =
        SampleAppDI.di().getInstance(SampleAppGithubResource.class);
    final SampleAppHttpBinPostResource postResource =
        SampleAppDI.di().getInstance(SampleAppHttpBinPostResource.class);

    environment.jersey().register(resource);
    environment.jersey().register(postResource);
  }
}
