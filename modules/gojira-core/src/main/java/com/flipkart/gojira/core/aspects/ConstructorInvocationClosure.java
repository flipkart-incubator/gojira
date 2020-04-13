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

package com.flipkart.gojira.core.aspects;

import java.lang.reflect.Constructor;

import org.aopalliance.intercept.ConstructorInvocation;
import org.aspectj.lang.JoinPoint;

public abstract class ConstructorInvocationClosure extends InvocationJoinPointClosure
        implements ConstructorInvocation {

    public ConstructorInvocationClosure(JoinPoint jp) {
        super(jp);
    }

    /**
     * @see org.aopalliance.intercept.MethodInvocation#getMethod()
     */
    public Constructor getConstructor() {
        return (Constructor) getStaticPart();
    }

}