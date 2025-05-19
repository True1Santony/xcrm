package com.xcrm;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableCaching
public class XcrmApplication {

	public static void main(String[] args) {
		SpringApplication.run(XcrmApplication.class, args);
	}
	
}
