package pl.dentistoffice.mobile.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.client.HttpClientErrorException;

import pl.dentistoffice.mobile.model.Patient;
import pl.dentistoffice.mobile.service.UserSrvice;

@Controller
@SessionAttributes(names = {"patient"})
public class PatientController {
	
	@Autowired
	private UserSrvice userSrvice;
	

	@Autowired
	private Environment env;
	
	@GetMapping(path = "/login")
	public String login() {
		return "login";
	}
	
	@PostMapping(path = "/login")
	public String login(@RequestParam(name = "username") String username, @RequestParam(name = "password") String password, HttpSession httpSession) {
		
		try {
			ResponseEntity<Patient> responseEntityPatient = userSrvice.getLoggedPatient(username, password);
			if(responseEntityPatient.getStatusCodeValue() == 200) {
				Patient patient = responseEntityPatient.getBody();
				httpSession.setAttribute("patient", patient);
				httpSession.setMaxInactiveInterval(Integer.valueOf(env.getProperty("sessionTimeOut")));
							
				System.out.println("Patient "+patient.getLastName()+" token: "+patient.getToken());
				
			} 
		} catch (HttpClientErrorException e) {
			if(e.getRawStatusCode() == 401) {
				System.out.println(e.getMessage());
			} else {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return "login";
	}

}
