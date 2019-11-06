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

import pl.dentistoffice.mobile.model.VisitStatus;

@Service
public class VisitService {
	
	@Autowired
	private CipherService cipherService;

	public ResponseEntity<VisitStatus> getVisitStatus(String token, String statusId) throws HttpClientErrorException {
					
			HttpHeaders requestHeaders = new HttpHeaders();
			String encodeToken = cipherService.encodeToken(token);
			requestHeaders.add("token", encodeToken);
			
			MultiValueMap<String, String> postParam = new LinkedMultiValueMap<String, String>();
			postParam.add("id", statusId);
				
			HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(postParam, requestHeaders);
			
			RestTemplate restTemplate = new RestTemplate();
			return restTemplate.exchange("http://localhost:8080/dentistoffice/mobile/visitStatus", HttpMethod.POST, requestEntity, VisitStatus.class);
//			return restTemplate.exchange("https://dentistoffice.herokuapp.com/mobile/visitStatus", HttpMethod.POST, requestEntity, VisitStatus.class);
	}
}
