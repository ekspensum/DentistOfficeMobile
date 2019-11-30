package pl.dentistoffice.mobile.test.unit.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

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
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import pl.dentistoffice.mobile.model.DentalTreatment;
import pl.dentistoffice.mobile.model.TreatmentCategory;
import pl.dentistoffice.mobile.model.TreatmentListWrapper;
import pl.dentistoffice.mobile.service.CipherService;
import pl.dentistoffice.mobile.service.DentalTreatmentService;

class DentalTreatmentServiceTest {

	private String URL_SERVICES;
	private String URL_TREATMENTS;
	
	@InjectMocks
	private DentalTreatmentService dentalTreatmentService;
	@Mock
	private RestTemplate restTemplate;
	@Mock
	private CipherService cipherService;
	@Mock
	private TreatmentListWrapper treatmentListWrapper;

	@BeforeEach
	void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	void testGetDentalTreatmentsList() {
		String token = "token123";
		HttpHeaders requestHeaders = new HttpHeaders();
		String encodeToken = "encodeToken";
		requestHeaders.add(HttpHeaders.AUTHORIZATION, encodeToken);
		when(cipherService.encodeToken(token)).thenReturn(encodeToken);
		HttpEntity<TreatmentListWrapper> requestEntity = new HttpEntity<>(requestHeaders);
		
		DentalTreatment dentalTreatment = new DentalTreatment();
		dentalTreatment.setName("name");
		List<DentalTreatment> dentalTreatmentsListExpect = new ArrayList<>();
		dentalTreatmentsListExpect.add(dentalTreatment);
		TreatmentListWrapper treatmentListWrapper = new TreatmentListWrapper();
		treatmentListWrapper.setDentalTreatmentsList(dentalTreatmentsListExpect);
		
		ResponseEntity<TreatmentListWrapper> responseEntity = new ResponseEntity<TreatmentListWrapper>(treatmentListWrapper, HttpStatus.OK); 
		when(restTemplate.exchange(URL_TREATMENTS, HttpMethod.GET, requestEntity, TreatmentListWrapper.class)).thenReturn(responseEntity);
		List<DentalTreatment> dentalTreatmentsListActual = dentalTreatmentService.getDentalTreatmentsList(token);
		assertEquals("name", dentalTreatmentsListActual.get(0).getName());
		
		responseEntity = new ResponseEntity<TreatmentListWrapper>(treatmentListWrapper, HttpStatus.NOT_FOUND); 
		when(restTemplate.exchange(URL_TREATMENTS, HttpMethod.GET, requestEntity, TreatmentListWrapper.class)).thenReturn(responseEntity);
		assertThrows(HttpClientErrorException.class, () -> dentalTreatmentService.getDentalTreatmentsList(token));
	}

	@Test
	void testGetTreatmentCategoriesList() {
		TreatmentCategory treatmentCategory = new TreatmentCategory();
		treatmentCategory.setCategoryName("categoryName");
		List<TreatmentCategory> treatmentCategoriesListExpect = new ArrayList<>();
		treatmentCategoriesListExpect.add(treatmentCategory);
		TreatmentListWrapper treatmentListWrapper = new TreatmentListWrapper();
		treatmentListWrapper.setTreatmentCategoriesList(treatmentCategoriesListExpect);
		
		ResponseEntity<TreatmentListWrapper> responseEntity = new ResponseEntity<TreatmentListWrapper>(treatmentListWrapper, HttpStatus.OK);		
		when(restTemplate.getForEntity(URL_SERVICES, TreatmentListWrapper.class)).thenReturn(responseEntity);
		List<TreatmentCategory> treatmentCategoriesListActual = dentalTreatmentService.getTreatmentCategoriesList();
		assertEquals("categoryName", treatmentCategoriesListActual.get(0).getCategoryName());
		
		responseEntity = new ResponseEntity<TreatmentListWrapper>(treatmentListWrapper, HttpStatus.NOT_FOUND);		
		when(restTemplate.getForEntity(URL_SERVICES, TreatmentListWrapper.class)).thenReturn(responseEntity);
		assertThrows(HttpClientErrorException.class, () -> dentalTreatmentService.getTreatmentCategoriesList());
	}

}
