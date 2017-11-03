package com.controller;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;

/**
 * Created by hdy on 03/11/2017.
 */
//@SpringBootApplication
//public class StartClass extends SpringBootServletInitializer {
//    public static void main(String[] args) {
//        SpringApplication.run(StartClass.class, args);
//    }
//
//    @Override
//    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
//        return application.sources(StartClass.class);
//    }
//
//}
@SpringBootApplication
public class StartClass {
    public static void main(String[] args) {
        SpringApplication.run(StartClass.class, args);
    }

}
