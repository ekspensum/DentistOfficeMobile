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

import pl.dentistoffice.mobile.model.VisitStatus;

@Service
public class VisitService {

	public ResponseEntity<VisitStatus> getVisitStatus(String token, String statusId) throws HttpClientErrorException {
		
			RestTemplate restTemplate = new RestTemplate();
			
			HttpHeaders requestHeaders = new HttpHeaders();
			requestHeaders.add("token", token);
			
			MultiValueMap<String, String> postParam = new LinkedMultiValueMap<String, String>();
			postParam.add("id", statusId);
				
			HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(postParam, requestHeaders);
			
			return restTemplate.exchange("http://localhost:8080/dentistoffice/mobile/visitStatus", HttpMethod.POST, requestEntity, VisitStatus.class);
	}
}
