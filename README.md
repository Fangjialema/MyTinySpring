# 这玩意是啥？
按照自己的理解，简单的写了一个tinySpring，实现了用注解的方式配置bean，用注解的方式注入属性（byType），以及通过`@AOP`注解标记bean对象的代理方法，实现对方法名和参数的打印。
主要是用来复习一下spring中关于IOC容器构建、AOP实现、以及单例bean循环依赖的解决方法

# Spring是如何解决循环依赖的？
首先，spring中对bean的生命周期定义为：
* bean对象创建（调用无参构造器）
* 给bean对象设置属性
* bean的后置处理器（初始化之前）
* bean对象初始化（需在配置bean时指定初始化方法）
* bean的后置处理器（初始化之后）
* bean对象就绪可以使用
* bean对象销毁（需在配置bean时指定销毁方法）
* IOC容器关闭

可以看出，Spring是先调用无参构造生成bean对象，再设置bean对象的属性的，因此简单地讲，循环依赖的解决方法就是，缓存利用无参构造生成的bean对象，在注入属性时，直接利用缓存的对象，避免循环构造产生无限递归。
但是！从上面的spring中对bean的生命周期定义可以看出，
。。。明天接着写
首先，Spring中有三类缓存：
``` java
    /**一级缓存*/
    private final Map<String, Object> singletonObjects = new ConcurrentHashMap<>(256);
    /**三级缓存*/
    private final Map<String, ObjectFactory<?>> singletonFactories = new HashMap<>(16);
    /**二级缓存*/
    private final Map<String, Object> earlySingletonObjects = new ConcurrentHashMap<>(16);
```

