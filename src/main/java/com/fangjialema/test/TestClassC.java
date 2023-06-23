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
        System.out.println("this is a test method!");
    }
}
