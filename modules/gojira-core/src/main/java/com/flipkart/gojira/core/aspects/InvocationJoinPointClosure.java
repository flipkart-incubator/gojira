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

import java.lang.reflect.AccessibleObject;

import org.aopalliance.intercept.Invocation;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.CodeSignature;
import org.aspectj.lang.reflect.ConstructorSignature;
import org.aspectj.lang.reflect.MethodSignature;

public abstract class InvocationJoinPointClosure extends JoinPointClosure implements Invocation {

    public InvocationJoinPointClosure(JoinPoint jp) {
        super(jp);
    }

    /**
     * @see org.aopalliance.intercept.Joinpoint#getStaticPart()
     */
    public AccessibleObject getStaticPart() {
        CodeSignature cSig = (CodeSignature) jp.getSignature();
        Class clazz = cSig.getDeclaringType();
        AccessibleObject ret = null;
        try {
            if (cSig instanceof MethodSignature) {
                ret = clazz.getMethod(cSig.getName(), cSig.getParameterTypes());
            } else if (cSig instanceof ConstructorSignature) {
                ret = clazz.getConstructor(cSig.getParameterTypes());
            }
        } catch (NoSuchMethodException mEx) {
            throw new UnsupportedOperationException(
                    "Can't find member " + cSig.toLongString());
        }
        return ret;
    }

    /**
     * @see org.aopalliance.intercept.Invocation#getArguments()
     */
    public Object[] getArguments() {
        return jp.getArgs();
    }

}