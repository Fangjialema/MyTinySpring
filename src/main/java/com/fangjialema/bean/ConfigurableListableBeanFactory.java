package com.fangjialema.bean;

public interface ConfigurableListableBeanFactory extends BeanFactory {
    void preInstantiateSingletons();
}
