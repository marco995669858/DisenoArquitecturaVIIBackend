package com.dataverse.rentals;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;

// @EnableScheduling habilita las tareas programadas diarias que revisan
// vencimientos sin depender de que alguien abra el dashboard: la de RN-03
// (ImplContrato.tareaProgramadaVerificarVencimientos, contratos por vencer)
// y la de liquidaciones vencidas (ImplLiquidacion.tareaProgramadaVerificarVencidas).
@SpringBootApplication
@PropertySource("classpath:routes.properties")
@EnableScheduling
public class DataverseRentalsApplication {

	public static void main(String[] args) {
		SpringApplication.run(DataverseRentalsApplication.class, args);
	}

}
