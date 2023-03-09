package com.digitalAlpha.cards;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import reactivefeign.spring.config.EnableReactiveFeignClients;

@SpringBootApplication
@EnableEurekaClient
@EnableReactiveFeignClients
public class CardsServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CardsServiceApplication.class, args);
	}

}
