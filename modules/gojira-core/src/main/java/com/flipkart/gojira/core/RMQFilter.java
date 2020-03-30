package com.flipkart.gojira.core;

import com.flipkart.gojira.core.injectors.GuiceInjector;
import com.flipkart.gojira.requestsampling.RequestSamplingRepository;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.LongString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import static com.flipkart.gojira.core.FilterConstants.TEST_HEADER;

/**
 * Filter implementation to capture RMQ request and response data. Also responsible for starting
 * and ending the recording of data per request-response capture lifecycle.
 */
public class RMQFilter {

    private static final Logger logger = LoggerFactory.getLogger(RMQFilter.class);

    public RMQFilter(){
    }

    /**
     * Initializes a map of {@link Mode} specific filter handlers for RMQ.
     */
    private static final Map<Mode, RMQFilterHandler> filterHashMap = Collections.unmodifiableMap(
            new HashMap<Mode, RMQFilterHandler>() {{
                put(Mode.NONE, new NoneRMQFilterHandler());
                put(Mode.PROFILE, new ProfileRMQFilterHandler());
                put(Mode.TEST, new TestRMQFilterHandler());
                put(Mode.SERIALIZE, new SerializeRMQFilterHandler());
            }}
    );

    public void start(String exchangeName, byte[] routingKey, byte[] data, AMQP.BasicProperties basicProperties, boolean mandatory) {

        Map<String, Object> headers = basicProperties.getHeaders();
        String testId = getTestId(headers);
        if (!(ProfileRepository.getGlobalProfileSetting().getMode().equals(Mode.TEST) || ProfileRepository.getGlobalProfileSetting().getMode().equals(Mode.SERIALIZE)) && (testId != null)) {
            logger.error("Header with name: " + TEST_HEADER + " present. But service is not running in TEST mode. : " + ProfileRepository.getGlobalProfileSetting().getMode());
            throw new RuntimeException("Header with name: " + TEST_HEADER + " present. But service is not running in TEST mode. : " + ProfileRepository.getGlobalProfileSetting().getMode());
        }
        if (!ProfileRepository.getMode().equals(Mode.NONE) && isExchangeWhitelisted(exchangeName)) {
            try {
                RMQTestRequestData rmqTestRequestData = getRMQTestRequestData(exchangeName, routingKey, data, basicProperties, mandatory);
                if (ProfileRepository.getMode().equals(Mode.PROFILE)) {
                    testId = String.valueOf(System.nanoTime()) + Thread.currentThread().getId();
                }
                if (testId == null) {
                    throw new RuntimeException("X-GOJIRA-ID header not present");
                }
                DefaultProfileOrTestHandler.start(testId, rmqTestRequestData);
            } catch (Exception e) {
                logger.error("Exception trying to construct RMQTestRequest. ", e);
            }
        }
    }

    public void end(byte[] bytes) {
        RMQTestResponseData rmqTestResponseData = new RMQTestResponseData();
        rmqTestResponseData.setResponseData(bytes);
        DefaultProfileOrTestHandler.end(rmqTestResponseData);
    }

    private RMQTestRequestData getRMQTestRequestData(String exchangeName, byte[] routingKey, byte[] data, AMQP.BasicProperties basicProperties, boolean mandatory) {
        RMQTestRequestData rmqTestRequestData = new RMQTestRequestData();
        rmqTestRequestData.setExchangeName(exchangeName);
        rmqTestRequestData.setData(data);
        rmqTestRequestData.setRoutingKey(routingKey);
        rmqTestRequestData.setProperties(basicProperties);
        rmqTestRequestData.setMandatory(mandatory);
        return rmqTestRequestData;
    }

    private static String getTestId(Map<String, Object> headersMap) {
        if (headersMap == null) {
            return null;
        }
        try {
            for (Map.Entry<String, Object> header : headersMap.entrySet()) {
                if (TEST_HEADER.equals(header.getKey())) {
                    if (header.getValue() instanceof LongString) {
                        byte[] correlationIdAsByteArray = ((LongString) header.getValue()).getBytes();
                        return new String(correlationIdAsByteArray, "UTF-8");
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Unable to encode test headers", e);
        }
        return null;
    }

    private boolean isExchangeWhitelisted(String exchangeName) {
        List<Pattern> whitelistedExchanges = GuiceInjector.getInjector().getInstance(RequestSamplingRepository.class).getWhitelistedExchanges();
        for (Pattern whitelistedExchange : whitelistedExchanges) {
            if (whitelistedExchange.matcher(exchangeName).matches()) {
                return true;
            }
        }
        return false;
    }



}
