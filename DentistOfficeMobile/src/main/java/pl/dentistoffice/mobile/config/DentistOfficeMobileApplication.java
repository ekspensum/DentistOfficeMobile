package pl.dentistoffice.mobile.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "pl.dentistoffice.mobile")
public class DentistOfficeMobileApplication {

	public static void main(String[] args) {
		SpringApplication.run(DentistOfficeMobileApplication.class, args);
	}

}
