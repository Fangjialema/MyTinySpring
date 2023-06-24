package com.fangjialema.bean;

import java.lang.reflect.Method;
import java.util.List;

public interface BeanDefinition {
    Class<?> getBeanClass();
    List<Method> getInitMethods();
    void addInitMethods(Method initMethod);
}
