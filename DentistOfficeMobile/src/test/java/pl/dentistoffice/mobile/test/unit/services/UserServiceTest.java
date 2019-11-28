package pl.dentistoffice.mobile.test.unit.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.util.reflection.FieldSetter;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import pl.dentistoffice.mobile.model.Doctor;
import pl.dentistoffice.mobile.model.DoctorListWrapper;
import pl.dentistoffice.mobile.model.Patient;
import pl.dentistoffice.mobile.service.CipherService;
import pl.dentistoffice.mobile.service.UserService;

class UserServiceTest {
	
	private String URL_LOGIN;
	private String URL_LOGOUT;
	private String URL_DOCTORS;
	private String URL_REGISTER_PATIENT;
	
	@InjectMocks
	private UserService userService;

	@Mock
	private Environment env;
	@Mock
	private CipherService cipherService;
	@Mock
	private RestTemplate restTemplate;
	@Mock
	private HttpSession httpSession;
	@Mock
	private Model model;

	
	@BeforeEach
	void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	void testLoginPatient() {
		Patient patient = new Patient();
	    patient.setId(77);
	    String username = "username";
	    String password = "password";
		MultiValueMap<String, String> loginData = new LinkedMultiValueMap<String, String>();
		loginData.add("username", username);
		loginData.add("password", password);
		
		MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();
		headers.add(HttpHeaders.AUTHORIZATION, "123");
		ResponseEntity<Patient> responseEntity = new ResponseEntity<Patient>(patient, headers, HttpStatus.OK);
	    
		when(restTemplate.postForEntity(URL_LOGIN, loginData, Patient.class)).thenReturn(responseEntity);
	    when(env.getProperty("sessionTimeOut")).thenReturn("20");
	    
	    boolean loginPatient = userService.loginPatient(username, password, httpSession, model);
	    
	    assertEquals(patient.getId(), responseEntity.getBody().getId());
	    assertTrue(loginPatient);
	}

	@Test
	void testLogoutPatient() {
		int patientId = 77;
		String token = "token";
		
		String encodeToken = "123";
		when(cipherService.encodeToken(token)).thenReturn(encodeToken);		
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.add(HttpHeaders.AUTHORIZATION, encodeToken);
		
		MultiValueMap<String, String> patchParam = new LinkedMultiValueMap<String, String>();
		patchParam.add("patientId", String.valueOf(patientId));
		HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(patchParam, requestHeaders);
				
		ResponseEntity<Boolean> responseEntity = new ResponseEntity<Boolean>(true, HttpStatus.OK);
		when(restTemplate.exchange(URL_LOGOUT, HttpMethod.POST, requestEntity, Boolean.class)).thenReturn(responseEntity);
		
		userService.logoutPatient(patientId, token);
		assertTrue(responseEntity.getBody());
		
		MultiValueMap<String, String> header = new LinkedMultiValueMap<String, String>();
		header.add(HttpHeaders.WARNING, "warning");
		responseEntity = new ResponseEntity<Boolean>(false, header, HttpStatus.OK);
		when(restTemplate.exchange(URL_LOGOUT, HttpMethod.POST, requestEntity, Boolean.class)).thenReturn(responseEntity);
		
		userService.logoutPatient(patientId, token);
		assertFalse(responseEntity.getBody());
		assertEquals("warning", responseEntity.getHeaders().get(HttpHeaders.WARNING).get(0));
	}
	
	@Test
	void testGetAllDoctors() {
		Doctor doctor = new Doctor();
		doctor.setPesel("123456");
		List<Doctor> doctorList = new ArrayList<>();
		doctorList.add(doctor);
		DoctorListWrapper doctorListWrapper = new DoctorListWrapper();
		doctorListWrapper.setDoctorList(doctorList);
		ResponseEntity<DoctorListWrapper> responseEntity = new ResponseEntity<DoctorListWrapper>(doctorListWrapper, HttpStatus.OK);
		when(restTemplate.getForEntity(URL_DOCTORS, DoctorListWrapper.class)).thenReturn(responseEntity);
		
		List<Doctor> allDoctors = userService.getAllDoctors();
		
		assertTrue(!responseEntity.getStatusCode().isError());
		assertEquals("123456", allDoctors.get(0).getPesel());
	}

	@Test
	void testRegisterPatient() throws NoSuchFieldException, SecurityException {
		String timeoutForTokenForAddNewPatient = "22";
		String tokenForAddNewPatient = "token123456";
		String tokenWithTimeout = LocalDateTime.now()
																			.plusSeconds(Integer.valueOf(timeoutForTokenForAddNewPatient))
																			.withNano(0).toString()
																			+tokenForAddNewPatient;
		FieldSetter.setField(userService, userService.getClass().getDeclaredField("timeoutForTokenForAddNewPatient"), timeoutForTokenForAddNewPatient);
		FieldSetter.setField(userService, userService.getClass().getDeclaredField("tokenForAddNewPatient"), tokenForAddNewPatient);
		String encodeToken = "123";
		when(cipherService.encodeToken(tokenWithTimeout)).thenReturn(encodeToken);		
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.add(HttpHeaders.AUTHORIZATION, encodeToken);
		Patient patient = new Patient();
		patient.setLastName("lastName");
		HttpEntity<Patient> requestEntity = new HttpEntity<>(patient, requestHeaders);
		
		MultiValueMap<String, String> header = new LinkedMultiValueMap<String, String>();
		header.add(HttpHeaders.WARNING, "warning");
		ResponseEntity<String> responseEntity = new ResponseEntity<String>("true", header, HttpStatus.OK);
		when(restTemplate.exchange(URL_REGISTER_PATIENT, HttpMethod.POST, requestEntity, String.class)).thenReturn(responseEntity);
		
		userService.registerPatient(patient);
		assertTrue(!responseEntity.getStatusCode().isError());
		assertEquals("true", responseEntity.getBody());
		
		responseEntity = new ResponseEntity<String>("error", header, HttpStatus.OK);
		when(restTemplate.exchange(URL_REGISTER_PATIENT, HttpMethod.POST, requestEntity, String.class)).thenReturn(responseEntity);
		
		userService.registerPatient(patient);
		assertTrue(!responseEntity.getStatusCode().isError());
		assertEquals("error", responseEntity.getBody());
		assertEquals("warning", responseEntity.getHeaders().get(HttpHeaders.WARNING).get(0));
	}

	@Test
	void testEditPatient() {
		fail("Not yet implemented");
	}

}
