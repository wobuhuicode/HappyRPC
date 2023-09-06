package cn.zhaokanglun.myrpc.spring.boot.autoconfigure;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Profile;

@SpringBootApplication
@Profile("Test")
public class Demo {
    public static void main(String[] args) {
        SpringApplication.run(Demo.class);
        System.out.println("hello world");
    }
}
