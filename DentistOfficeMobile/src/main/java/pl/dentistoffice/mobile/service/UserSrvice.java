package pl.dentistoffice.mobile.service;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import pl.dentistoffice.mobile.model.Doctor;
import pl.dentistoffice.mobile.model.DoctorListWrapper;
import pl.dentistoffice.mobile.model.Patient;

@Service
public class UserSrvice {
	
	@Value(value = "${URL_LOGIN}")
	private String URL_LOGIN;
	
	@Value(value = "${URL_DOCTORS}")
	private String URL_DOCTORS;

	@Value(value = "${URL_EDIT_PATIENT}")
	private String URL_EDIT_PATIENT;
	
	@Autowired
	private Environment env;

	@Autowired
	private CipherService cipherService;
	
	public boolean loginPatient(String username, String password, HttpSession httpSession, Model model) {	
			MultiValueMap<String, String> loginData = new LinkedMultiValueMap<String, String>();
			loginData.add("username", username);
			loginData.add("password", password);
			
			RestTemplate restTemplate = new RestTemplate();
			ResponseEntity<Patient> responseEntity = restTemplate.postForEntity(URL_LOGIN, loginData, Patient.class);
			if(!responseEntity.getStatusCode().isError()) {
				Patient patient = responseEntity.getBody();
				model.addAttribute("patient", patient);
				String encodeTokenBase64 = responseEntity.getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
				String decodeToken = cipherService.decodeToken(encodeTokenBase64);
				model.addAttribute("token", decodeToken);	
				httpSession.setMaxInactiveInterval(Integer.valueOf(env.getProperty("sessionTimeOut")));
				return true;
			} else {
				throw new HttpClientErrorException(responseEntity.getStatusCode());
			}
	}

	public List<Doctor> getAllDoctors() {
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<DoctorListWrapper> responseEntity = restTemplate.getForEntity(URL_DOCTORS, DoctorListWrapper.class);
		if(!responseEntity.getStatusCode().isError()) {
			DoctorListWrapper doctorListWrapper = responseEntity.getBody();
			return doctorListWrapper.getDoctorList();
		} else {
			throw new HttpClientErrorException(responseEntity.getStatusCode());
		}
	}
	
	public Doctor getDoctor(int doctorId, List<Doctor> allDoctors) {
		for(int i=0; i<allDoctors.size(); i++) {
			if(allDoctors.get(i).getId() == doctorId) {
				return allDoctors.get(i);
			}
		}
		return null;
	}
	
	public String [] dayOfWeekPolish() {
		String [] dayOfWeekPolish = {"Zero", "Poniedziałek", "Wtorek", "Środa", "Czwartek", "Piątek", "Sobota", "Niedziela"};
		return dayOfWeekPolish;
	}

	public Boolean editPatient(String token, Patient patient) {	
			HttpHeaders requestHeaders = new HttpHeaders();
			String encodeToken = cipherService.encodeToken(token);
			requestHeaders.add(HttpHeaders.AUTHORIZATION, encodeToken);
			HttpEntity<Patient> requestEntity = new HttpEntity<Patient>(patient, requestHeaders);
			RestTemplate restTemplate = new RestTemplate();
			ResponseEntity<Boolean> responseEntity = restTemplate.exchange(URL_EDIT_PATIENT, HttpMethod.PUT, requestEntity, Boolean.class);
			
			if(!responseEntity.getStatusCode().isError()) {
				Boolean responseBody = responseEntity.getBody();
				if(responseBody) {
					return true;
				} else {
					return false;
				} 					
			}
			throw new HttpClientErrorException(responseEntity.getStatusCode());
	}
}
