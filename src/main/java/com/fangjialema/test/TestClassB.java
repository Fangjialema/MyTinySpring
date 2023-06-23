package com.fangjialema.test;

import com.fangjialema.annotation.Bean;
import com.fangjialema.annotation.Di;

@Bean
public class TestClassB {
    @Di
    private TestClassA testClassA;
}
