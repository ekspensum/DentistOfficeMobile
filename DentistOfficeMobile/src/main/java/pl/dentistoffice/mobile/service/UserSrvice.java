package pl.dentistoffice.mobile.service;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import pl.dentistoffice.mobile.model.DoctorListWrapper;
import pl.dentistoffice.mobile.model.Patient;

@Service
public class UserSrvice {

	
	public ResponseEntity<Patient> getLoggedPatient(String username, String password) throws HttpClientErrorException {	
		
			RestTemplate restTemplate = new RestTemplate();
			
			MultiValueMap<String, String> loginData = new LinkedMultiValueMap<String, String>();
			loginData.add("username", username);
			loginData.add("password", password);
			
			return restTemplate.postForEntity("http://localhost:8080/dentistoffice/mobile/login", loginData, Patient.class);
	}
	
	public ResponseEntity<DoctorListWrapper> getDoctors(String token) throws HttpClientErrorException{
		
			RestTemplate restTemplate = new RestTemplate();
			
			HttpHeaders requestHeaders = new HttpHeaders();
			requestHeaders.add("token", token);
			HttpEntity<Patient> requestEntity = new HttpEntity<>(requestHeaders);

			return restTemplate.exchange("http://localhost:8080/dentistoffice/mobile/doctors", HttpMethod.GET, requestEntity, DoctorListWrapper.class);
	}
}
