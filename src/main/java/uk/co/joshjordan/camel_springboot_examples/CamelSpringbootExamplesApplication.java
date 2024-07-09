package uk.co.joshjordan.camel_springboot_examples;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan(basePackages ="uk.co.joshjordan.camel_springboot_examples.beans")
public class CamelSpringbootExamplesApplication {

	public static void main(String[] args) {
		SpringApplication.run(CamelSpringbootExamplesApplication.class, args);
	}

}
