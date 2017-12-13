package com.algaworks.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.algaworks.api.config.property.AlgaworksApiProperty;

@SpringBootApplication
@EnableConfigurationProperties(AlgaworksApiProperty.class)
public class AlgaworksApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(AlgaworksApiApplication.class, args);
	}
}
