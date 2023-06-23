package com.fangjialema.bean;

public interface SingletonBeanRegistry {
    void registerSingleton(String beanName, Object singletonObject);
    Object getSingleton(String beanName);
}
