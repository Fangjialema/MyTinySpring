package com.fangjialema.bean;

import com.fangjialema.annotation.Aop;
import com.fangjialema.annotation.Di;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;


import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class MyTinyBeanFactory extends MyTinySingletonBeanRegistry implements ConfigurableListableBeanFactory, BeanDefinitionRegistry {
    private final Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>(256);
    protected static final List<BeanPostProcessor> postProcessorsList = new ArrayList<>();

    @Override
    public void registerBeanDefinition(String beanName, BeanDefinition beanDefinition) {
        beanDefinitionMap.put(beanName, beanDefinition);
    }

    @Override
    public void removeBeanDefinition(String beanName) {
        beanDefinitionMap.remove(beanName);
    }

    @Override
    public BeanDefinition getBeanDefinition(String beanName) {
        return beanDefinitionMap.get(beanName);
    }

    @Override
    public Object getBean(String name) {
        try {
            Object singleton = getSingleton(name);
            if (singleton == null) {
                beforeSingletonCreation(name);
                MyTinyBeanDefinition beanDefinition = (MyTinyBeanDefinition) getBeanDefinition(name);
                Class<?> beanClass = beanDefinition.getBeanClass();
                singleton = beanClass.getConstructor().newInstance();
                Object finalSingleton = singleton;
                addSingletonFactory(name, () -> createBeanProxy(finalSingleton, beanClass));
                popular(singleton, beanClass);
                Object singletonEarly = getSingleton(name, false);
                if (singletonEarly != null) {
                    singleton = singletonEarly;
                } else {
                    singleton = createBeanProxy(finalSingleton, beanClass);
                }
                for (var processor : postProcessorsList) {
                    processor.postProcessBeforeInitialization(singleton, name);
                }
                var initMethods = beanDefinition.getInitMethods();
                for (var initMethod : initMethods) {
                    initMethod.setAccessible(true);
                    initMethod.invoke(singleton, (Object[]) null);
                }
                for (var processor : postProcessorsList) {
                    processor.postProcessAfterInitialization(singleton, name);
                }
                registerSingleton(name, singleton);
                afterSingletonCreation(name);
            }
            return singleton;
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private Object createBeanProxy(Object singleton, Class<?> clazz) {
        Method[] Methods = clazz.getDeclaredMethods();
        HashSet<Method> aopMethods = new HashSet<>();
        for (Method field : Methods) {
            if (field.isAnnotationPresent(Aop.class)) {
                aopMethods.add(field);
            }
        }
        if (aopMethods.size()==0) return singleton;
        else{
            Enhancer enhancer = new Enhancer();
            enhancer.setSuperclass(clazz);
            enhancer.setCallback((MethodInterceptor) (obj, method, args, proxy) -> {
                if (aopMethods.contains(method)) {
                    if (args.length == 0)
                        System.out.format("调用前，调用对象为:%s，调用的方法名：%s，方法没有入参\n", clazz.getSimpleName(), method.getName());
                    else
                        System.out.format("调用前，调用对象为:%s，调用的方法名：%s，方法的入参是：%s\n", clazz.getSimpleName(), method.getName(), Arrays.toString(args));
                    Object res = proxy.invokeSuper(obj, args);
                    if (res == null)
                        System.out.format("调用后，调用对象为:%s，调用的方法名：%s，返回结果是：void\n", clazz.getSimpleName(), method.getName());
                    else
                        System.out.format("调用后，调用对象为:%s，调用的方法名：%s，结果是：%s\n", clazz.getSimpleName(), method.getName(), res.toString());
                    return res;
                } else
                    return proxy.invokeSuper(obj, args);
            });
            return enhancer.create();
        }
    }

    @Override
    public void preInstantiateSingletons() {
        for (var entry : beanDefinitionMap.entrySet()) {
            getBean(entry.getKey());
        }
    }

    private void popular(Object singleton, Class<?> clazz) {
        for (Field declaredField : clazz.getDeclaredFields()) {
            Di anno = declaredField.getAnnotation(Di.class);
            if (anno != null) {
                declaredField.setAccessible(true);
                try {
                    declaredField.set(singleton, getBean(declaredField.getType().getSimpleName()));
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

}
