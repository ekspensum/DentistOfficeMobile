package pl.dentistoffice.mobile.service;

import org.springframework.beans.factory.annotation.Autowired;
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
	
	@Autowired
	private CipherService cipherService;
	
	public ResponseEntity<Patient> getLoggedPatient(String username, String password) throws HttpClientErrorException {	
					
			MultiValueMap<String, String> loginData = new LinkedMultiValueMap<String, String>();
			loginData.add("username", username);
			loginData.add("password", password);
			
			RestTemplate restTemplate = new RestTemplate();
			return restTemplate.postForEntity("http://localhost:8080/dentistoffice/mobile/login", loginData, Patient.class);
//			return restTemplate.postForEntity("https://dentistoffice.herokuapp.com/mobile/login", loginData, Patient.class);
	}
	
	public ResponseEntity<DoctorListWrapper> getDoctors(String token) throws HttpClientErrorException{
					
			HttpHeaders requestHeaders = new HttpHeaders();
			String encodeToken = cipherService.encodeToken(token);
			requestHeaders.add("token", encodeToken);
			HttpEntity<Patient> requestEntity = new HttpEntity<>(requestHeaders);

			RestTemplate restTemplate = new RestTemplate();
			return restTemplate.exchange("http://localhost:8080/dentistoffice/mobile/doctors", HttpMethod.GET, requestEntity, DoctorListWrapper.class);
//			return restTemplate.exchange("https://dentistoffice.herokuapp.com/mobile/doctors", HttpMethod.GET, requestEntity, DoctorListWrapper.class);
	}
}
