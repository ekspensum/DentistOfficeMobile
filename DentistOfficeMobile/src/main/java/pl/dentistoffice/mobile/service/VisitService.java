package pl.dentistoffice.mobile.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import pl.dentistoffice.mobile.model.DentalTreatment;
import pl.dentistoffice.mobile.model.VisitStatus;
import pl.dentistoffice.mobile.model.WorkingWeekMapWrapper;

@Service
public class VisitService {
	
	@Value(value = "${URL_NEW_VISIT}")
	private String URL_NEW_VISIT;
	
	@Value(value = "${URL_WORKING_WEEK_MAP}")
	private String URL_WORKING_WEEK_MAP;
	
	@Autowired
	private CipherService cipherService;


	public ResponseEntity<VisitStatus> getVisitStatus(String token, String statusId) {

		HttpHeaders requestHeaders = new HttpHeaders();
		String encodeToken = cipherService.encodeToken(token);
		requestHeaders.add(HttpHeaders.AUTHORIZATION, encodeToken);

		MultiValueMap<String, String> postParam = new LinkedMultiValueMap<String, String>();
		postParam.add("id", statusId);

		HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(postParam, requestHeaders);

		RestTemplate restTemplate = new RestTemplate();
		return restTemplate.exchange("http://localhost:8080/dentistoffice/mobile/visitStatus", HttpMethod.POST,	requestEntity, VisitStatus.class);
//			return restTemplate.exchange("https://dentistoffice.herokuapp.com/mobile/visitStatus", HttpMethod.POST, requestEntity, VisitStatus.class);
	}
	
	public boolean addNewVisitByMobilePatient(int doctorId, int patientId, String [] dateTime, List<DentalTreatment> treatments, String token) {
		HttpHeaders requestHeaders = new HttpHeaders();
		String encodeToken = cipherService.encodeToken(token);
		requestHeaders.add(HttpHeaders.AUTHORIZATION, encodeToken);
		
		MultiValueMap<String, String> postParam = new LinkedMultiValueMap<String, String>();
		postParam.add("doctorId", String.valueOf(doctorId));
		postParam.add("patientId", String.valueOf(patientId));
		postParam.add("dateTime", dateTime[0]);			
		HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(postParam, requestHeaders);
		
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<Boolean> response = restTemplate.exchange(URL_NEW_VISIT, HttpMethod.POST, requestEntity, Boolean.class);
		if(response.getBody()) {
			return true;
		}
		return false;
	}

	public Map<LocalDate, Map<LocalTime, Boolean>> getWorkingWeekFreeTimeMap(int doctorId, int dayStart, int dayEnd){
		
		MultiValueMap<String, String> postParam = new LinkedMultiValueMap<String, String>();
		postParam.add("doctorId", String.valueOf(doctorId));
		postParam.add("dayStart", String.valueOf(dayStart));
		postParam.add("dayEnd", String.valueOf(dayEnd));
			
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<WorkingWeekMapWrapper> responseEntity = restTemplate.postForEntity(URL_WORKING_WEEK_MAP, postParam, WorkingWeekMapWrapper.class);
		
		if(!responseEntity.getStatusCode().isError()) {
			WorkingWeekMapWrapper workingWeekMapWrapper = responseEntity.getBody();
			Map<LocalDate, Map<LocalTime, Boolean>> workingWeekFreeTimeMap = workingWeekMapWrapper.getWorkingWeekFreeTimeMap();
			return workingWeekFreeTimeMap;			
		} else {
			throw new HttpClientErrorException(responseEntity.getStatusCode());
		}		
	}
	
}
