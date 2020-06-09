package com.flipkart.gojira.sample.src.main.java.com.flipkart.gojira.example;

import com.flipkart.gojira.core.HttpFilter;
import com.flipkart.gojira.sample.src.main.java.com.flipkart.gojira.example.api.GithubResource;
import com.flipkart.gojira.sample.src.main.java.com.flipkart.gojira.example.api.HttpBinPostResource;
import com.flipkart.gojira.sample.src.main.java.com.flipkart.gojira.example.core.APIHealthCheck;
import com.flipkart.gojira.sample.src.main.java.com.flipkart.gojira.example.module.DI;
import com.flipkart.gojira.sample.src.main.java.com.flipkart.gojira.example.module.ServiceSampleModule;
import com.flipkart.gojira.external.Managed;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

import javax.servlet.DispatcherType;
import java.util.EnumSet;

import static com.flipkart.gojira.sample.src.main.java.com.flipkart.gojira.example.module.DI.di;


public class GojiraSampleApplication extends Application<GojiraSampleConfiguration> {

    public static void main(final String[] args) throws Exception {
        new GojiraSampleApplication().run(args);
    }

    @Override
    public String getName() {
        return "GojiraSample";
    }

    @Override
    public void initialize(final Bootstrap<GojiraSampleConfiguration> bootstrap) {
        // TODO: application initialization
    }

    @Override
    public void run(final GojiraSampleConfiguration configuration,
                    final Environment environment) {
        DI.install(new ServiceSampleModule());
        di().getInstance(Managed.class).setup();
        environment.healthChecks().register("APIHealthCheck", new APIHealthCheck());
        environment.servlets().addFilter("test-filter", new HttpFilter()).addMappingForUrlPatterns(
                EnumSet.of(DispatcherType.REQUEST), true, "*");
        final GithubResource resource = di().getInstance(GithubResource.class);
        final HttpBinPostResource postResource = di().getInstance(HttpBinPostResource.class);
        environment.jersey().register(resource);
        environment.jersey().register(postResource);
    }
}
