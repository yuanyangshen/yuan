package com.shenyy.yuan;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(value = "com.shenyy.yuan.dao")
public class YuanApplication {

	public static void main(String[] args) {
		SpringApplication.run(YuanApplication.class, args);
	}
}
