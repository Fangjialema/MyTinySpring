# 复习一下spring中关于单例bean循环依赖的解决方法
首先，最重要的是这三个缓存：
```java
    /**一级缓存*/
    private final Map<String, Object> singletonObjects = new ConcurrentHashMap<>(256);
    /**三级缓存*/
    private final Map<String, ObjectFactory<?>> singletonFactories = new HashMap<>(16);
    /**二级缓存*/
    private final Map<String, Object> earlySingletonObjects = new ConcurrentHashMap<>(16);
```
一级缓存singletonObjects：存储的是已经构建好的单例bean对象。Spring在创建bean对象时，会将已经创建好的bean对象缓存在singletonObjects中，当bean对象间出现循环依赖时，已创建的对象在singletonObjects中被提取，避免循环依赖的对象无限递归地创建
二级缓存earlySingletonObjects，叫做提前引用缓存，假设有以下情况：
```java
    public class A{
      public B b;
    }
    public class B{
    }
```
假设Bean A先进行创建，那么Bean A在创建时依赖Bean B，那么Bean A的创建过程中，需要先暂停Bean A的创建，
