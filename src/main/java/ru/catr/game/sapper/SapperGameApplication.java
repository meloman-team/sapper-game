package ru.catr.game.sapper;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class SapperGameApplication {

	public static void main(String[] args) {
		SpringApplication.run(SapperGameApplication.class, args);
	}

}
