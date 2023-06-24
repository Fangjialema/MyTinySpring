package com.fangjialema.test;


import com.fangjialema.bean.BeanPostProcessor;

public class BeanProcessor implements BeanPostProcessor {
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName)  {
        System.out.print("bean后置处理器，初始化之前执行,当前bean对象：");
        System.out.println(beanName+"::"+bean);
        return bean;
    }
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        System.out.print("bean后置处理器，初始化之后执行,当前bean对象：");
        System.out.println(beanName+"::"+bean);
        return bean;
    }
}
