package com.flipkart.gojira.compare.handlers.json;

import com.flipkart.compare.TestCompareException;
import com.flipkart.compare.handlers.json.JsonTestCompareHandler;
import com.flipkart.gojira.models.http.HttpTestResponseData;
import com.flipkart.gojira.serde.handlers.json.JsonDefaultTestSerdeHandler;
import com.google.common.primitives.Bytes;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.zip.GZIPInputStream;

public class HttpResponseCompareHandler extends JsonTestCompareHandler {
  JsonDefaultTestSerdeHandler jsonDefaultTestSerdeHandler = new JsonDefaultTestSerdeHandler();

  @Override
  protected void doCompare(byte[] profiledData, byte[] testData) throws TestCompareException {
    try {
      HttpTestResponseData profiled =
          jsonDefaultTestSerdeHandler.deserialize(profiledData, HttpTestResponseData.class);
      HttpTestResponseData test =
          jsonDefaultTestSerdeHandler.deserialize(testData, HttpTestResponseData.class);
      try {
        super.doCompare(
            jsonDefaultTestSerdeHandler.serialize(profiled.getStatusCode()),
            jsonDefaultTestSerdeHandler.serialize(test.getStatusCode()));

        Map<String, String> testResponseHeaders = test.getHeaders();
        Map<String, String> profiledResponseHeaders = profiled.getHeaders();
        super.doCompare(
            jsonDefaultTestSerdeHandler.serialize(profiledResponseHeaders),
            jsonDefaultTestSerdeHandler.serialize(testResponseHeaders));

        byte[] testResponseBodyBytes = null;
        if (testResponseHeaders != null
            && testResponseHeaders.containsKey("Content-Encoding")
            && testResponseHeaders.get("Content-Encoding").equalsIgnoreCase("gzip")) {
          try {
            testResponseBodyBytes = decompress(test.getBody());
          } catch (Exception e) {
            testResponseBodyBytes = test.getBody();
          }
        } else {
          testResponseBodyBytes = test.getBody();
        }

        byte[] profiledResponseBodyBytes = null;
        if (profiledResponseHeaders != null
            && profiledResponseHeaders.containsKey("Content-Encoding")
            && profiledResponseHeaders.get("Content-Encoding").equalsIgnoreCase("gzip")) {
          try {
            profiledResponseBodyBytes = decompress(profiled.getBody());
          } catch (Exception e) {
            profiledResponseBodyBytes = profiled.getBody();
          }
        } else {
          profiledResponseBodyBytes = profiled.getBody();
        }

        if (profiledResponseBodyBytes != null
            && testResponseBodyBytes != null
            && profiledResponseBodyBytes.length > 0
            && testResponseBodyBytes.length > 0
            && new String(profiledResponseBodyBytes)
                .contains("There was an error processing your request. It has been logged (ID")
            && new String(testResponseBodyBytes)
                .contains("There was an error processing your request. It has been logged (ID")) {
          return;
        }
        super.doCompare(profiledResponseBodyBytes, testResponseBodyBytes);
      } catch (Exception e) {
        // hack to check for proto bytes in string instead of bytes
        if (!new String(profiled.getBody()).equals(new String(test.getBody()))) throw e;
      }
    } catch (TestCompareException e) {
      throw e;
    } catch (Exception e) {
      throw new RuntimeException("unable to deserialize HTTP Response data.", e);
    }
  }

  private byte[] decompress(byte[] dataBytes) throws TestCompareException {
    try {
      GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(dataBytes));
      BufferedReader bf = new BufferedReader(new InputStreamReader(gis));

      byte[] out = new byte[0];
      String line;
      while ((line = bf.readLine()) != null) {
        out = Bytes.concat(out, line.getBytes());
      }
      return out;
    } catch (IOException e) {
      throw new TestCompareException("unable to decompress saved response data.");
    }
  }
}
