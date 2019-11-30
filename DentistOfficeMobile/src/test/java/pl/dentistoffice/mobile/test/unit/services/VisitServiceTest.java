package pl.dentistoffice.mobile.test.unit.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import pl.dentistoffice.mobile.model.Visit;
import pl.dentistoffice.mobile.model.VisitAndStatusListWrapper;
import pl.dentistoffice.mobile.model.WorkingWeekMapWrapper;
import pl.dentistoffice.mobile.service.CipherService;
import pl.dentistoffice.mobile.service.VisitService;

class VisitServiceTest {
	
	private String URL_NEW_VISIT;
	private String URL_WORKING_WEEK_MAP;
	private String URL_VISIT_STATUS_LIST;
	private String URL_DELETE_VISIT;
	
	@InjectMocks
	private VisitService visitService;
	@Mock
	private CipherService cipherService;
	@Mock
	private RestTemplate restTemplate;

	@BeforeEach
	void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	void testGetVisitsAndStatusListForPatient() {
		String token = "token123";
		HttpHeaders requestHeaders = new HttpHeaders();
		String encodeToken = "encodeToken";
		requestHeaders.add(HttpHeaders.AUTHORIZATION, encodeToken);
		when(cipherService.encodeToken(token)).thenReturn(encodeToken);
		
		MultiValueMap<String, String> postParam = new LinkedMultiValueMap<String, String>();
		int patientId = 33;
		postParam.add("patientId", String.valueOf(patientId));
		HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(postParam, requestHeaders);
		
		Visit visit = new Visit();
		visit.setId(88);
		List<Visit> visitList = new ArrayList<>();
		visitList.add(visit);
		VisitAndStatusListWrapper wrapperExpect = new VisitAndStatusListWrapper();
		wrapperExpect.setVisitsList(visitList);

		MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
		headers.add(HttpHeaders.WARNING, "warning");
		ResponseEntity<VisitAndStatusListWrapper> responseEntity = new ResponseEntity<VisitAndStatusListWrapper>(wrapperExpect, headers, HttpStatus.OK);
				
		when(restTemplate.exchange(URL_VISIT_STATUS_LIST, HttpMethod.POST,	requestEntity, VisitAndStatusListWrapper.class)).thenReturn(responseEntity);
		
		VisitAndStatusListWrapper wrapperActual = visitService.getVisitsAndStatusListForPatient(patientId, token);
		assertEquals(88, wrapperActual.getVisitsList().get(0).getId());
		
		responseEntity = new ResponseEntity<VisitAndStatusListWrapper>(wrapperExpect, headers, HttpStatus.FORBIDDEN);
		when(restTemplate.exchange(URL_VISIT_STATUS_LIST, HttpMethod.POST,	requestEntity, VisitAndStatusListWrapper.class)).thenReturn(responseEntity);
		assertThrows(HttpClientErrorException.class, () -> visitService.getVisitsAndStatusListForPatient(patientId, token));
		String headerActual = responseEntity.getHeaders().get(HttpHeaders.WARNING).get(0);
		assertEquals("warning", headerActual);
	}

	@Test
	void testAddNewVisitByMobilePatient() {
		String token = "token123";
		HttpHeaders requestHeaders = new HttpHeaders();
		String encodeToken = "encodeToken";
		requestHeaders.add(HttpHeaders.AUTHORIZATION, encodeToken);
		when(cipherService.encodeToken(token)).thenReturn(encodeToken);
		
		MultiValueMap<String, String> postParam = new LinkedMultiValueMap<String, String>();
		postParam.add("doctorId", "11");
		postParam.add("patientId", "22");
		postParam.add("dateTime", "dateTime");
		postParam.add("treatment1Id", "1");
		postParam.add("treatment2Id", "2");
		postParam.add("treatment3Id", "3");
		HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(postParam, requestHeaders);
		
		MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
		headers.add(HttpHeaders.WARNING, "warning");
		
		ResponseEntity<Boolean> responseEntity = new ResponseEntity<Boolean>(true, headers, HttpStatus.OK);
		when(restTemplate.exchange(URL_NEW_VISIT, HttpMethod.POST, requestEntity, Boolean.class)).thenReturn(responseEntity);	
		boolean addNewVisitByMobilePatient = visitService.addNewVisitByMobilePatient(11, 22, "dateTime", "1", "2", "3", token);
		
		assertTrue(addNewVisitByMobilePatient);
		
		responseEntity = new ResponseEntity<Boolean>(false, headers, HttpStatus.OK);
		when(restTemplate.exchange(URL_NEW_VISIT, HttpMethod.POST, requestEntity, Boolean.class)).thenReturn(responseEntity);	
		addNewVisitByMobilePatient = visitService.addNewVisitByMobilePatient(11, 22, "dateTime", "1", "2", "3", token);
		
		assertFalse(addNewVisitByMobilePatient);
		assertEquals("warning", responseEntity.getHeaders().get(HttpHeaders.WARNING).get(0));
	}

	@Test
	void testGetWorkingWeekFreeTimeMap() {
		MultiValueMap<String, String> postParam = new LinkedMultiValueMap<String, String>();
		postParam.add("doctorId", "11");
		postParam.add("dayStart", "0");
		postParam.add("dayEnd", "7");
		
		MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
		headers.add(HttpHeaders.WARNING, "warning");		
		
		Map<LocalDate, Map<LocalTime, Boolean>> workingWeekFreeTimeMapExpect = new LinkedHashMap<>();
		Map<LocalTime, Boolean> workingTimeMap = new LinkedHashMap<>();
		workingTimeMap.put(LocalTime.of(16, 30), true);
		workingWeekFreeTimeMapExpect.put(LocalDate.now(), workingTimeMap);
		WorkingWeekMapWrapper workingWeekMapWrapper = new WorkingWeekMapWrapper();
		workingWeekMapWrapper.setWorkingWeekFreeTimeMap(workingWeekFreeTimeMapExpect);
		
		ResponseEntity<WorkingWeekMapWrapper> responseEntity = new ResponseEntity<WorkingWeekMapWrapper>(workingWeekMapWrapper, headers, HttpStatus.OK);
		when(restTemplate.postForEntity(URL_WORKING_WEEK_MAP, postParam, WorkingWeekMapWrapper.class)).thenReturn(responseEntity);
		Map<LocalDate, Map<LocalTime, Boolean>> workingWeekFreeTimeMapActual = visitService.getWorkingWeekFreeTimeMap(11, 0, 7);
		assertEquals("{16:30=true}", workingWeekFreeTimeMapActual.values().toArray()[0].toString());
		
		responseEntity = new ResponseEntity<WorkingWeekMapWrapper>(workingWeekMapWrapper, headers, HttpStatus.NOT_FOUND);
		when(restTemplate.postForEntity(URL_WORKING_WEEK_MAP, postParam, WorkingWeekMapWrapper.class)).thenReturn(responseEntity);
		assertThrows(HttpClientErrorException.class, () -> visitService.getWorkingWeekFreeTimeMap(11, 0, 7));
		assertEquals("warning", responseEntity.getHeaders().get(HttpHeaders.WARNING).get(0));
	}

	@Test
	void testDeleteVisit() {
		String token = "token123";
		HttpHeaders requestHeaders = new HttpHeaders();
		String encodeToken = "encodeToken";
		requestHeaders.add(HttpHeaders.AUTHORIZATION, encodeToken);
		when(cipherService.encodeToken(token)).thenReturn(encodeToken);
		
		MultiValueMap<String, String> deleteParam = new LinkedMultiValueMap<String, String>();
		deleteParam.add("visitId", "222");
		HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(deleteParam, requestHeaders);
		
		MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
		headers.add(HttpHeaders.WARNING, "warning");	
		
		ResponseEntity<Boolean> responseEntity = new ResponseEntity<Boolean>(true, headers, HttpStatus.OK);
		when(restTemplate.exchange(URL_DELETE_VISIT, HttpMethod.POST, requestEntity, Boolean.class)).thenReturn(responseEntity);
		boolean deleteVisit = visitService.deleteVisit("222", token);
		assertTrue(deleteVisit);
		
		responseEntity = new ResponseEntity<Boolean>(false, headers, HttpStatus.OK);
		when(restTemplate.exchange(URL_DELETE_VISIT, HttpMethod.POST, requestEntity, Boolean.class)).thenReturn(responseEntity);
		deleteVisit = visitService.deleteVisit("222", token);
		assertFalse(deleteVisit);
		assertEquals("warning", responseEntity.getHeaders().get(HttpHeaders.WARNING).get(0));
		
		responseEntity = new ResponseEntity<Boolean>(false, headers, HttpStatus.NOT_FOUND);
		when(restTemplate.exchange(URL_DELETE_VISIT, HttpMethod.POST, requestEntity, Boolean.class)).thenReturn(responseEntity);
		assertThrows(HttpClientErrorException.class, () -> visitService.deleteVisit("222", token));
		assertEquals("warning", responseEntity.getHeaders().get(HttpHeaders.WARNING).get(0));
	}

}
