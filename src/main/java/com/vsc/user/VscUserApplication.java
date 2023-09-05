package com.vsc.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication
public class VscUserApplication {

	public static void main(String[] args) {
		SpringApplication.run(VscUserApplication.class, args);
	}

}
