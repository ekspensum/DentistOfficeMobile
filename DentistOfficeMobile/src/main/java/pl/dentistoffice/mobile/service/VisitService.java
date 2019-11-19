package pl.dentistoffice.mobile.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
	@Autowired
	private RestTemplate restTemplate;

	private static final Logger logger = LoggerFactory.getLogger(VisitService.class);
	
	public ResponseEntity<VisitStatus> getVisitStatus(String token, String statusId) {

		HttpHeaders requestHeaders = new HttpHeaders();
		String encodeToken = cipherService.encodeToken(token);
		requestHeaders.add(HttpHeaders.AUTHORIZATION, encodeToken);

		MultiValueMap<String, String> postParam = new LinkedMultiValueMap<String, String>();
		postParam.add("id", statusId);

		HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(postParam, requestHeaders);

		return restTemplate.exchange("http://localhost:8080/dentistoffice/mobile/visitStatus", HttpMethod.POST,	requestEntity, VisitStatus.class);
//			return restTemplate.exchange("https://dentistoffice.herokuapp.com/mobile/visitStatus", HttpMethod.POST, requestEntity, VisitStatus.class);
	}
	
	public boolean addNewVisitByMobilePatient(int doctorId, int patientId, String dateTime, String treatment1Id, String treatment2Id, String treatment3Id, 
																			String token) {
		HttpHeaders requestHeaders = new HttpHeaders();
		String encodeToken = cipherService.encodeToken(token);
		requestHeaders.add(HttpHeaders.AUTHORIZATION, encodeToken);
		
		MultiValueMap<String, String> postParam = new LinkedMultiValueMap<String, String>();
		postParam.add("doctorId", String.valueOf(doctorId));
		postParam.add("patientId", String.valueOf(patientId));
		postParam.add("dateTime", dateTime);
		postParam.add("treatment1Id", treatment1Id);
		postParam.add("treatment2Id", treatment2Id);
		postParam.add("treatment3Id", treatment3Id);
		HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(postParam, requestHeaders);
		
		ResponseEntity<Boolean> responseEntity = restTemplate.exchange(URL_NEW_VISIT, HttpMethod.POST, requestEntity, Boolean.class);
		if(!responseEntity.getStatusCode().isError()) {
			if(responseEntity.getBody()) {
				return true;
			} else {
				if(responseEntity.getHeaders().get(HttpHeaders.WARNING) != null) {
					String warning = responseEntity.getHeaders().get(HttpHeaders.WARNING).get(0);
					logger.error("ERROR MESSAGE FROM REST SERVICE: {}", warning);					
				}
				return false;	
			}			
		}
		throw new HttpClientErrorException(responseEntity.getStatusCode());
	}

	public Map<LocalDate, Map<LocalTime, Boolean>> getWorkingWeekFreeTimeMap(int doctorId, int dayStart, int dayEnd){
		
		MultiValueMap<String, String> postParam = new LinkedMultiValueMap<String, String>();
		postParam.add("doctorId", String.valueOf(doctorId));
		postParam.add("dayStart", String.valueOf(dayStart));
		postParam.add("dayEnd", String.valueOf(dayEnd));
			
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
