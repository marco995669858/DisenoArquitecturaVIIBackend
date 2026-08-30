package com.pe.consultorio.clinico;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@PropertySource("classpath:routes.properties")
public class ClinicoApplication {

	public static void main(String[] args) {
		SpringApplication.run(ClinicoApplication.class, args);
	}

}
