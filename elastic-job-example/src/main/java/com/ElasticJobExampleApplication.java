package com;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;


@SpringBootApplication
@ComponentScan
public class ElasticJobExampleApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(ElasticJobExampleApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("aaa");

    }
}
