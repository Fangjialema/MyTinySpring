package com.fangjialema.bean;

public class MyTinyBeanDefinition implements BeanDefinition{
    private Class<?> beanClass;
    public MyTinyBeanDefinition(Class<?> beanClass){
        this.beanClass=beanClass;
    }
    @Override
    public Class<?> getBeanClass() {
        return beanClass;
    }
}
