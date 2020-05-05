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

package com.flipkart.gojira.core.aspect.test;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

public class MockProfileOrTestMethodInterceptor implements MethodInterceptor {
    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        if (methodInvocation.getArguments().length > 0) {
            Object arg = methodInvocation.getArguments()[0];
            int i = 0;
            if (arg instanceof Integer) {
                i = (Integer) arg;
                i++;
            } else if (arg instanceof int []) {
                int[] arr = (int[]) arg;
                i = ++arr[0];
            }
            methodInvocation.proceed();
            return i;
        }
        return 0;
    }
}
