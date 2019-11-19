package pl.dentistoffice.mobile.service;

import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import pl.dentistoffice.mobile.model.DentalTreatment;
import pl.dentistoffice.mobile.model.TreatmentListWrapper;
import pl.dentistoffice.mobile.model.TreatmentCategory;


@Service
public class DentalTreatmentService {
	
	@Value(value = "${URL_SERVICES}")
	private String URL_SERVICES;
	@Value(value = "${URL_TREATMENTS}")
	private String URL_TREATMENTS;
	
	@Autowired
	private RestTemplate restTemplate;
	@Autowired
	private TreatmentListWrapper treatmentListWrapper;
	@Autowired
	private CipherService cipherService;

	public List<DentalTreatment> getDentalTreatmentsList(String token){
		String encodeToken = cipherService.encodeToken(token);
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.AUTHORIZATION, encodeToken);
		HttpEntity<TreatmentListWrapper> requestEntity = new HttpEntity<>(headers);
		ResponseEntity<TreatmentListWrapper> responseEntity = restTemplate.exchange(URL_TREATMENTS, HttpMethod.GET, requestEntity, TreatmentListWrapper.class);
		if(!responseEntity.getStatusCode().isError()) {
			TreatmentListWrapper treatmentsWrapperFromBody = responseEntity.getBody();
			List<DentalTreatment> allDentalTreatments = treatmentsWrapperFromBody.getDentalTreatmentsList();
			allDentalTreatments.sort(new Comparator<DentalTreatment>() {
				
				@Override
				public int compare(DentalTreatment o1, DentalTreatment o2) {
					return o1.getId() - o2.getId();
				}
			});
			return allDentalTreatments;			
		} 
		throw new HttpClientErrorException(responseEntity.getStatusCode());
	}
	
//	public DentalTreatment getDentalTreatment(int id) {
//		return treatmentRepository.readDentalTreatment(id);
//	}
//	
//	public List<DentalTreatment> searchDentalTreatment(String text){
//		List<DentalTreatment> searchedTreatments = searchsService.searchDentalTreatmentNameDescriptionByKeywordQuery(text);
//		searchedTreatments.sort(new Comparator<DentalTreatment>() {
//
//			@Override
//			public int compare(DentalTreatment o1, DentalTreatment o2) {
//				return o1.getName().compareTo(o2.getName());
//			}
//		});
//		return searchedTreatments;
//	}
	
	public List<TreatmentCategory> getTreatmentCategoriesList(){
		ResponseEntity<TreatmentListWrapper> responseEntity = restTemplate.getForEntity(URL_SERVICES, TreatmentListWrapper.class);
		if(!responseEntity.getStatusCode().isError()) {
			TreatmentListWrapper treatmentListWrapperFromBody = responseEntity.getBody();
			List<TreatmentCategory> allTreatmentCategory = treatmentListWrapperFromBody.getTreatmentCategoriesList();
			treatmentListWrapper.setTreatmentCategoriesList(allTreatmentCategory);
			allTreatmentCategory.sort(new Comparator<TreatmentCategory>() {
				
				@Override
				public int compare(TreatmentCategory o1, TreatmentCategory o2) {
					return o1.getId() - o2.getId();
				}
			});
			return allTreatmentCategory;			
		}
		throw new HttpClientErrorException(responseEntity.getStatusCode());
	}
	
	public TreatmentCategory getTreatmentCategory(int id) {
		List<TreatmentCategory> treatmentCategoriesList = treatmentListWrapper.getTreatmentCategoriesList();
		for (int i = 0; i < treatmentCategoriesList.size(); i++) {
			if(id == treatmentCategoriesList.get(i).getId()) {
				return treatmentCategoriesList.get(i);				
			}
		}
		return null;
	}
	
}
