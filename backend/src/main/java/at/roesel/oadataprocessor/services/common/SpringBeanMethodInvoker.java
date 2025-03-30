/*
 *  Copyright (c) 2025 Dr. Martin Rösel <opensource@roesel.at>
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package at.roesel.oadataprocessor.services.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public class SpringBeanMethodInvoker {

    private final static Logger logger = LoggerFactory.getLogger(SpringBeanMethodInvoker.class);

    private final String allowedPackage = "at.roesel";

    @Autowired
    private ApplicationContext context;

    // methodStr: e.g. publisherIdentificationService.identifyMainPublisher()
    public void execute(String methodStr) {

        logger.debug("Executing method {}", methodStr);

        int posDot = methodStr.indexOf(".");
        if (posDot == -1) {
            throw new IllegalArgumentException("Invalid method name 1: " + methodStr);
        }

        int posParenthesis = methodStr.indexOf("(");
        if (posParenthesis == -1) {
            throw new IllegalArgumentException("Invalid method name 2: " + methodStr);
        }

        // Name of the bean and method
        String beanName = methodStr.substring(0, posDot);
        String methodName = methodStr.substring(posDot+1, posParenthesis);

        try {
            // Get the bean from the Spring context
            Object bean = context.getBean(beanName);
            if (!bean.getClass().getName().startsWith(allowedPackage)) {
                throw new IllegalArgumentException("Cannot invoke method of: " + beanName);
            }

            Class<?> beanClass = bean.getClass();
            Method method = findMethod(beanClass, methodName);
            if (method == null) {
                throw new IllegalArgumentException("Cannot find method of: " + beanName);
            }

            // Dynamically build the parameter list
            List<Object> paramList = new ArrayList<>();
            if (method.getParameters().length > 0) {
                String paramStr = methodStr.substring(posParenthesis+1, methodStr.length()-1);
                String[] givenParameters = paramStr.split(",");
                if (givenParameters.length != method.getParameters().length) {
                    throw new IllegalArgumentException("Required parameters are missing: " + methodStr);
                }
                for (int i = 0; i < method.getParameters().length; i++) {
                    Parameter param = method.getParameters()[i];
                    String type = param.getType().getName();
                    Object parameter = createParameter(type, givenParameters[i]);
                    paramList.add(parameter);
                }
            }

            // Convert the list to an array
            Object[] params = paramList.toArray();

            // Invoke the method
            logger.debug("Executing method {}.{}", beanClass.getName(), method.getName());
            Object result = method.invoke(bean, params);

        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    private Object createParameter(String type, String givenParameter) {
        switch (type) {
            case "java.lang.String":
                if (givenParameter.equals("null")) {
                    givenParameter = null;
                }
                return givenParameter;
            case "java.lang.Integer":
                return Integer.parseInt(givenParameter);
            case "java.lang.Boolean":
                return Boolean.parseBoolean(givenParameter);
            case "java.time.LocalDate":
                return LocalDate.parse(givenParameter);
        }
        throw new IllegalArgumentException("Invalid parameter type: " + type);
    }

    // simple version for just one parameter
    private Method findMethod(Class<?> beanClass, String methodName) {
        // Find the method using getDeclaredMethods
        for (Method method : beanClass.getDeclaredMethods()) {
            if (method.getName().equals(methodName)) {
                return method;
            }
        }
        return null;
    }
}
