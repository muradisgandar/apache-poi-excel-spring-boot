package com.example.apachepoi;

import com.example.apachepoi.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ApachepoiApplication implements CommandLineRunner {

	@Autowired
	private CustomerService customerService;

	public static void main(String[] args) {
		SpringApplication.run(ApachepoiApplication.class, args);

	}

	@Override
	public void run(String... args) {
//		customerService.getEncodedCustomerExcelFile();
	}
}
