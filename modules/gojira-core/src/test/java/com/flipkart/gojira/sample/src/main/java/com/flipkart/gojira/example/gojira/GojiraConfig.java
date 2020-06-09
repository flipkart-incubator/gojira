package com.flipkart.gojira.sample.src.main.java.com.flipkart.gojira.example.gojira;

import com.flipkart.gojira.core.Mode;

import java.util.List;

/**
 * @author narendra.vardi
 */
public class GojiraConfig {
    private Mode mode;
    private Long maxQueueSize;
    private List<String> whitelistedURIs;
    private double samplingPercentage;
    private int version;

    public GojiraConfig() {
    }

    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public Long getMaxQueueSize() {
        return maxQueueSize;
    }

    public void setMaxQueueSize(Long maxQueueSize) {
        this.maxQueueSize = maxQueueSize;
    }

    public List<String> getWhitelistedURIs() {
        return whitelistedURIs;
    }

    public void setWhitelistedURIs(List<String> whitelistedURIs) {
        this.whitelistedURIs = whitelistedURIs;
    }

    public double getSamplingPercentage() {
        return samplingPercentage;
    }

    public void setSamplingPercentage(double samplingPercentage) {
        this.samplingPercentage = samplingPercentage;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }
}
