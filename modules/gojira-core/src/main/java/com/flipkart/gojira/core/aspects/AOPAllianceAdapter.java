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

package com.flipkart.gojira.core.aspects;

import org.aopalliance.intercept.ConstructorInterceptor;
import org.aopalliance.intercept.MethodInterceptor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.SoftException;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public abstract class AOPAllianceAdapter {

    /**
     * Return the interceptor to use at method execution join points.
     * Must be overriden by subclasses.
     *
     * @return MethodInterceptor, or null if no method advice required
     */
    protected abstract MethodInterceptor getMethodInterceptor();

    /**
     * Return the interceptor to use at constructor execution join points.
     * May be overriden by subclasses.
     *
     * @return ConstructorInterceptor, or null if no constructor advice required
     */
    protected ConstructorInterceptor getConstructorInterceptor() {
        return null;
    }

    @Pointcut
    protected abstract void targetJoinPoint();

    @Pointcut("execution(* *(..))")
    public void methodExecution() {}

    @Pointcut("execution(new(..))")
    public void constructorExecution() {}

    @Around("targetJoinPoint() && methodExecution()")
    public Object around(ProceedingJoinPoint thisJoinPoint) throws Throwable {
        MethodInvocationClosure mic = new MethodInvocationClosure(thisJoinPoint) {
            public Object execute() throws Throwable { return thisJoinPoint.proceed();}
        };
        MethodInterceptor mInt = getMethodInterceptor();
        if (mInt != null) {
            try {
                return mInt.invoke(mic);
            } catch (Throwable t) {
                throw new SoftException(t);
            }
        } else {
            return thisJoinPoint.proceed();
        }
    }

    @Around("targetJoinPoint() && constructorExecution()")
    public Object aroundConstructor(ProceedingJoinPoint thisJoinPoint) throws Throwable {
        ConstructorInvocationClosure cic = new ConstructorInvocationClosure(thisJoinPoint) {
            public Object execute() throws Throwable {
                thisJoinPoint.proceed();
                return thisJoinPoint.getThis();
            }
        };
        ConstructorInterceptor cInt = getConstructorInterceptor();
        if (cInt != null) {
            try {
                return cInt.construct(cic);
            } catch (Throwable t) {
                throw new SoftException(t);
            }
        } else {
            return thisJoinPoint.proceed();
        }
    }

}