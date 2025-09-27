package se.ifmo.origin_backend;

import org.springframework.boot.SpringApplication;

public class TestOriginBackendApplication {

	public static void main(String[] args) {
		SpringApplication.from(OriginBackendApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
