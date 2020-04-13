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

package com.flipkart.gojira.models;

import com.flipkart.gojira.core.Mode;
import com.flipkart.gojira.core.ProfileState;
import java.util.Objects;

/**
 * This class contains execution related data for a given request-response cycle in different {@link
 * Mode}.
 *
 * <p>//TODO: Rename this ExecutionData
 */
public class ProfileData<T extends TestDataType> {

  /**
   * Instance of test-data in a given execution cycle. It gets mutated as the execution progresses.
   */
  private TestData<TestRequestData<T>, TestResponseData<T>, T> testData = new TestData<>();

  /**
   * Keeps track of the current execution state. Defaults to {@link ProfileState#NONE}.
   */
  private ProfileState profileState = ProfileState.NONE;

  public TestData<TestRequestData<T>, TestResponseData<T>, T> getTestData() {
    return testData;
  }

  public void setTestData(TestData<TestRequestData<T>, TestResponseData<T>, T> testData) {
    this.testData = testData;
  }

  public ProfileState getProfileState() {
    return profileState;
  }

  public void setProfileState(ProfileState profileState) {
    this.profileState = profileState;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    ProfileData that = (ProfileData) o;

    if (!Objects.equals(testData, that.testData)) {
      return false;
    }
    return profileState == that.profileState;
  }

  @Override
  public int hashCode() {
    int result = testData != null ? testData.hashCode() : 0;
    result = 31 * result + (profileState != null ? profileState.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return "ProfileData{" + "profileState=" + profileState + ", testData=" + testData + '}';
  }
}
