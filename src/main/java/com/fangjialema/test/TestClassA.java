package com.fangjialema.test;

import com.fangjialema.annotation.Bean;
import com.fangjialema.annotation.Di;

@Bean
public class TestClassA {
    @Di
    private TestClassB testClassB;
    @Di
    private TestClassC testClassC;
}
