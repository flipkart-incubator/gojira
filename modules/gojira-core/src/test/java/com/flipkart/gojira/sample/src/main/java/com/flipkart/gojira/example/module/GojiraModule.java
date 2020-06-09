package com.flipkart.gojira.sample.src.main.java.com.flipkart.gojira.example.module;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flipkart.compare.handlers.json.JsonTestCompareHandler;
import com.flipkart.gojira.compare.config.GojiraComparisonConfig;
import com.flipkart.gojira.core.BindingsModule;
import com.flipkart.gojira.core.SetupModule;
import com.flipkart.gojira.core.TestExecutionModule;
import com.flipkart.gojira.external.config.HttpConfig;
import com.flipkart.gojira.external.http.HttpManager;
import com.google.common.collect.Lists;
import com.flipkart.gojira.sample.src.main.java.com.flipkart.gojira.example.gojira.GojiraConfig;
import com.flipkart.gojira.execute.TestExecutorModule;
import com.flipkart.gojira.external.ManagedModule;
import com.flipkart.gojira.external.config.ExternalConfig;
//import com.flipkart.gojira.external.config.HelperConfig;
import com.flipkart.gojira.queuedsender.config.TestQueuedSenderConfig;
import com.flipkart.gojira.requestsampling.config.RequestSamplingConfig;
import com.flipkart.gojira.serde.config.SerdeConfig;
import com.flipkart.gojira.serde.handlers.json.JsonDefaultTestSerdeHandler;
import com.flipkart.gojira.sinkstore.config.DataStoreConfig;
import com.flipkart.gojira.sinkstore.file.FileBasedDataStoreHandler;
import com.google.common.collect.Maps;
import com.google.inject.AbstractModule;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author narendra.vardi
 */
public class GojiraModule extends AbstractModule {
    private ObjectMapper mapper;
    private static final String JSON_DIFF_IGNORES_PATH = "gojira-core/src/test/java/com/flipkart/gojira/sample/config/dev/jsonDiffIgnorePatterns.json";
    private static final String GOJIRA_CONFIG_PATH = "gojira-core/src/test/java/com/flipkart/gojira/sample/config/dev/gojiraConfig.json";
    private static final String BIQ_QUEUE_PATH = "gojira-core/src/test/java/com/flipkart/gojira/sample/config/dev/queue-messages";
    private static final String DATA_STORE_PATH = "gojira-core/src/test/java/com/flipkart/gojira/sample/config/dev/datastore";

    public GojiraModule(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    protected void configure() {
        // TODO narendra make a proper SinkHandler implementation
        Map<String, List<String>> jsonDiffIgnoreMap = null;
        GojiraConfig gojiraConfig = null;
        try {
            jsonDiffIgnoreMap = mapper.readValue(Files.readAllBytes(Paths.get(JSON_DIFF_IGNORES_PATH)), HashMap.class);
            gojiraConfig = mapper.readValue(Files.readAllBytes(Paths.get(GOJIRA_CONFIG_PATH)), GojiraConfig.class);
        } catch (Exception e) {
            // consider throwing an exception.
        }

        RequestSamplingConfig requestSamplingConfig = RequestSamplingConfig.builder()
                .setSamplingPercentage(gojiraConfig.getSamplingPercentage())
                .setWhitelist(gojiraConfig.getWhitelistedURIs())
                .build();

        SerdeConfig serdeConfig = SerdeConfig.builder()
                .setDefaultSerdeHandler(new JsonDefaultTestSerdeHandler())
                .build();

        GojiraComparisonConfig gojiraComparisonConfig = GojiraComparisonConfig.builder()
                .setDiffIgnoreMap(jsonDiffIgnoreMap)
                .setDefaultCompareHandler(new JsonTestCompareHandler())
                .setResponseDataCompareHandler(new JsonTestCompareHandler())
                .build();

        DataStoreConfig dataStoreConfig = DataStoreConfig.builder()
                .setDataStoreHandler(new FileBasedDataStoreHandler(DATA_STORE_PATH))
                .build();

        TestQueuedSenderConfig testQueuedSenderConfig = TestQueuedSenderConfig.builder()
                .setPath(BIQ_QUEUE_PATH)
                .setQueueSize(gojiraConfig.getMaxQueueSize())
                .build();

        install(new SetupModule(gojiraConfig.getMode(),
                requestSamplingConfig,
                serdeConfig,
                gojiraComparisonConfig,
                dataStoreConfig,
                testQueuedSenderConfig
        ));
        install(new BindingsModule());
        install(new TestExecutorModule());


        Map<String, List<ExternalConfig>> externalConfigMap = Maps.newHashMap();
        HttpConfig externalConfig = new HttpConfig();
        externalConfig.setConnectionTimeout(5000);
        externalConfig.setHostNamePort("localhost:5000");
        externalConfig.setMaxConnections(50);
        externalConfig.setOperationTimeout(5000);
        externalConfigMap.put("DEFAULT", Lists.newArrayList(externalConfig));
        install(new ManagedModule());
//        HelperConfig helperConfig = HelperConfig.builder().setExternalConfigMap(externalConfigMap).build();
        install(new TestExecutionModule(externalConfigMap));
        HttpManager.HTTP_MANAGER.setup();
    }
}
