package com.fangjialema.bean;

import com.fangjialema.annotation.Aop;
import com.fangjialema.annotation.Di;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MyTinyBeanFactory extends MyTinySingletonBeanRegistry implements ConfigurableListableBeanFactory, BeanDefinitionRegistry {
    private final Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>(256);

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
                    System.out.println("正在创建代理");
                    singleton = createBeanProxy(finalSingleton, beanClass);
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
        Method[] Methods = clazz.getMethods();
        for (Method field : Methods) {
            if (field.isAnnotationPresent(Aop.class)) {
                Enhancer enhancer = new Enhancer();
                enhancer.setSuperclass(clazz);
                enhancer.setCallback((MethodInterceptor) (obj, method, args, proxy) -> {
                    if (args.length==0)
                        System.out.format("调用前，调用的方法名：%s，方法没有入参\n", method.getName());
                    else
                        System.out.format("调用前，调用的方法名：%s，方法的入参是：%s\n", method.getName(), Arrays.toString(args));
                    Object res = proxy.invokeSuper(obj, args);
                    if (res==null)
                        System.out.format("调用后，调用的方法名：%s，返回结果是：void\n", method.getName());
                    else
                        System.out.format("调用后，调用的方法名：%s，结果是：%s\n", method.getName(), res.toString());
                    return res;
                });
                return enhancer.create();
            }
        }
        return singleton;
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
