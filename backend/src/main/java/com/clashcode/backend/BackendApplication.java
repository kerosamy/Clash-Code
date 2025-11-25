package com.clashcode.backend;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class BackendApplication {
	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
        String value = System.getenv("JWT_SECRET_KEY");
        System.out.println("MY_ENV_VAR = " + value);


	}
}






