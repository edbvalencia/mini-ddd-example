package com.alutarb.apps;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
@ComponentScan(includeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION), value = {
    "com.alutarb.apps",
    "com.alutarb.analytics",
    "com.alutarb.shared",
})
public class Starter {

    public static void main(String[] args) {
        SpringApplication.run(Starter.class, args);
    }

}
