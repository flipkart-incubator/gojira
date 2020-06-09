package com.flipkart.gojira.sample.src.main.java.com.flipkart.gojira.example.models;

/**
 * @author narendra.vardi
 */
public class HttpPostSampleData {
    private String hello = "world";
    private TimeDetails timeDetails = new TimeDetails();

    public String getHello() {
        return hello;
    }

    public TimeDetails getTimeDetails() {
        return timeDetails;
    }

    // helps with json diff ignore patterns.
    class TimeDetails {
        private final long epochTime = System.currentTimeMillis();

        public long getEpochTime() {
            return epochTime;
        }
    }
}
