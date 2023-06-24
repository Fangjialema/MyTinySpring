package com.fangjialema.test;

import com.fangjialema.annotation.Bean;
import com.fangjialema.annotation.Di;

@Bean(init_methods = {"init"})
public class TestClassA {
    @Di
    private TestClassB testClassB;
    @Di
    private TestClassC testClassC;

    private void init(){
        System.out.println("TestClassA 的 init 方法");
    }
}
