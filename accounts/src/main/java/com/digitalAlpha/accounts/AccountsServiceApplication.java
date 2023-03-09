package com.digitalAlpha.accounts;

import com.digitalAlpha.accounts.exception.ServerErrorException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class AccountsServiceApplication {


	public static void main(String[] args) throws ServerErrorException {
		SpringApplication.run(AccountsServiceApplication.class, args);
	}

}
