package com.fangjialema.bean;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class MyTinySingletonBeanRegistry implements SingletonBeanRegistry{
    /**一级缓存*/
    private final Map<String, Object> singletonObjects = new ConcurrentHashMap<>(256);
    /**三级缓存*/
    private final Map<String, ObjectFactory<?>> singletonFactories = new HashMap<>(16);
    /**二级缓存*/
    private final Map<String, Object> earlySingletonObjects = new ConcurrentHashMap<>(16);
    private final Set<String> singletonsCurrentlyInCreation = Collections.newSetFromMap(new ConcurrentHashMap<>(16));
    @Override
    public void registerSingleton(String beanName, Object singletonObject) {
        singletonObjects.put(beanName,singletonObject);
    }

    @Override
    public Object getSingleton(String beanName) {
        return getSingleton(beanName,true);
    }
    protected Object getSingleton(String beanName,boolean allowEarlyReference){
        Object singletonObject=singletonObjects.get(beanName);
        if(singletonObject==null && isSingletonCurrentlyInCreation(beanName)){
            singletonObject=earlySingletonObjects.get(beanName);
            if (singletonObject==null && allowEarlyReference){
                synchronized (singletonFactories){
                    singletonObject=singletonObjects.get(beanName);
                    if (singletonObject==null){
                        singletonObject=earlySingletonObjects.get(beanName);
                        if (singletonObject==null){
                            ObjectFactory<?> singletonFactory=singletonFactories.get(beanName);
                            if (singletonFactory!=null){
                                singletonObject=singletonFactory.getObject();
                                earlySingletonObjects.put(beanName,singletonObject);
                                singletonFactories.remove(beanName);
                            }
                        }
                    }
                }
            }
        }
        return singletonObject;
    }
    public boolean isSingletonCurrentlyInCreation(String beanName) {
        return singletonsCurrentlyInCreation.contains(beanName);
    }
    protected void beforeSingletonCreation(String beanName) {
        this.singletonsCurrentlyInCreation.add(beanName);
    }
    protected void afterSingletonCreation(String beanName) {
        this.singletonsCurrentlyInCreation.remove(beanName);
    }
    protected void addSingletonFactory(String beanName, ObjectFactory<?> objectFactory) {
        this.singletonFactories.put(beanName, objectFactory);
    }

}
