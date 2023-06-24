package com.fangjialema.test;

import com.fangjialema.annotation.Aop;
import com.fangjialema.annotation.Bean;
import com.fangjialema.annotation.Di;
@Bean
public class TestClassC {
    @Di
    private TestClassA testClassA;
    @Aop
    public void testMethod(){
        System.out.println("this is testMethod which has an aop!");
    }
    @Aop
    public void testMethod2(){
        System.out.println("this is testMethod2 which has an aop!");
    }
    public void testMethod3(){
        System.out.println("this is testMethod3 which doesn't have an aop!");
    }
}
