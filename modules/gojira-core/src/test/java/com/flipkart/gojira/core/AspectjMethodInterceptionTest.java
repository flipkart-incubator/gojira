/*
 *
 * Copyright 2020 Flipkart Internet, pvt ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http:www.apache.orglicensesLICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.flipkart.gojira.core;

import com.flipkart.gojira.core.annotations.ProfileOrTest;
import com.flipkart.gojira.core.aspect.test.AspectJConfig;
import com.flipkart.gojira.core.aspect.test.AspectJUnit4Runner;
import com.flipkart.gojira.execute.TestExecutionException;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

@AspectJConfig(classpathAdditions = "src/test/java/com/flipkart/gojira/core/aspect/test")
@RunWith(AspectJUnit4Runner.class)
public class AspectjMethodInterceptionTest {

    @ProfileOrTest
    private int testPrivateMethod (int i) {
        return i;
    }

    private int testNonAnnotated (int i) {
        return i;
    }

    private int testException (int... i) throws TestExecutionException {
        throw new TestExecutionException("test");
    }

    @Test
    public void testMethodInterception () throws Throwable {
        assertEquals(11, testPrivateMethod(10));
    }

    @Test
    public void testMethodInterceptionNonAnnotated () throws Throwable {
        assertEquals(11, testNonAnnotated(10));
    }

    @Test
    public void testMethodInteceptionException () throws Throwable {
        int [] arr = new int[] { 10 };
        assertThrows("test", TestExecutionException.class, () -> testException(arr));
        assertEquals(11, arr[0]);
    }

}
