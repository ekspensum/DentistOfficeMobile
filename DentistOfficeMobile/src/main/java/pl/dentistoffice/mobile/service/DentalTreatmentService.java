package pl.dentistoffice.mobile.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import pl.dentistoffice.mobile.model.DentalTreatment;
import pl.dentistoffice.mobile.model.DoctorListWrapper;
import pl.dentistoffice.mobile.model.TreatmenCategorytListWrapper;
import pl.dentistoffice.mobile.model.TreatmentCategory;


@Service
public class DentalTreatmentService {
	
	@Value(value = "${URL_SERVICES}")
	private String URL_SERVICES;
	
	@Autowired
	private TreatmenCategorytListWrapper treatmenCategorytListWrapper;

//	public List<DentalTreatment> getDentalTreatmentsList(){
//		List<DentalTreatment> allDentalTreatments = treatmentRepository.readAllDentalTreatment();
//		allDentalTreatments.sort(new Comparator<DentalTreatment>() {
//
//			@Override
//			public int compare(DentalTreatment o1, DentalTreatment o2) {
//				return o1.getId() - o2.getId();
//			}
//		});
//		return allDentalTreatments;
//	}
//	
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
		
		RestTemplate restTemplate = new RestTemplate();
		TreatmenCategorytListWrapper treatmenCategorytListWrapperFromBody = restTemplate
				.getForEntity(URL_SERVICES, TreatmenCategorytListWrapper.class).getBody();
		List<TreatmentCategory> allTreatmentCategory = treatmenCategorytListWrapperFromBody.getTreatmentCategoriesList();
		treatmenCategorytListWrapper.setTreatmentCategoriesList(allTreatmentCategory);
		allTreatmentCategory.sort(new Comparator<TreatmentCategory>() {

			@Override
			public int compare(TreatmentCategory o1, TreatmentCategory o2) {
				return o1.getId() - o2.getId();
			}
		});
		return allTreatmentCategory;
	}
	
	public TreatmentCategory getTreatmentCategory(int id) {
		List<TreatmentCategory> treatmentCategoriesList = treatmenCategorytListWrapper.getTreatmentCategoriesList();
		for (int i = 0; i < treatmentCategoriesList.size(); i++) {
			if(id == treatmentCategoriesList.get(i).getId()) {
				return treatmentCategoriesList.get(i);				
			}
		}
		return null;
	}
	
}
