package com.fangjialema.bean;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class MyTinyBeanDefinition implements BeanDefinition{
    private Class<?> beanClass;
    public MyTinyBeanDefinition(Class<?> beanClass){
        this.beanClass=beanClass;
    }
    List<Method> initMethods=new ArrayList<>();
    @Override
    public Class<?> getBeanClass() {
        return beanClass;
    }

    @Override
    public List<Method> getInitMethods() {
        return initMethods;
    }

    @Override
    public void addInitMethods(Method initMethod) {
        initMethods.add(initMethod);
    }
}
