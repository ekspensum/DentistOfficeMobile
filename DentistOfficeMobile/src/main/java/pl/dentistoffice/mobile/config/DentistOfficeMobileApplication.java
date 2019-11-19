package pl.dentistoffice.mobile.config;

import java.io.IOException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import nz.net.ultraq.thymeleaf.LayoutDialect;

@SpringBootApplication
@ComponentScan(basePackages = "pl.dentistoffice.mobile")
public class DentistOfficeMobileApplication {

	public static void main(String[] args) {
		SpringApplication.run(DentistOfficeMobileApplication.class, args);
	}

	//	for Thymeleaf layout
	@Bean
	public LayoutDialect layoutDialect() {
	    return new LayoutDialect();
	}

	@Bean
	public CommonsMultipartResolver multipartResolver() throws IOException {
	    CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
//	    Constraints below must resolve Exception (throw runtime error) - see TaskController  
//	    multipartResolver.setMaxUploadSizePerFile(200000);
//	    multipartResolver.setMaxUploadSize(200000);
	    
//	    Not to use on Heroku cloud
//	    multipartResolver.setUploadTempDir(new FileSystemResource(System.getenv("TMP")));
	    return multipartResolver;
	}
	
	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
}
