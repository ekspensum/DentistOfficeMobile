package pl.dentistoffice.mobile.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.client.HttpClientErrorException;

import pl.dentistoffice.mobile.model.DoctorListWrapper;
import pl.dentistoffice.mobile.model.Patient;
import pl.dentistoffice.mobile.service.UserSrvice;

@Controller
@SessionAttributes(names = {"patient"})
public class DoctorController {

	@Autowired
	private UserSrvice userSrvice;
	
	@GetMapping(path = "/doctors")
	public String getDoctors(@SessionAttribute(name = "patient", required = false) Patient patient, Model model) {

		try {
			if(patient != null) {
				ResponseEntity<DoctorListWrapper> responseEntity = userSrvice.getDoctors(patient.getToken());
				if(responseEntity.getStatusCodeValue() == 200) {
					
					System.out.println("Answer "+responseEntity.getBody().getDoctorList().get(0).getWorkingWeek().getWorkingWeekMapByte().length);
				} else {
					System.out.println("DoctorController - doctors, response: "+responseEntity.getStatusCode());
				}
			} else {
				System.out.println("DoctorController - lack session (not logged)");
			}
			
		} catch (HttpClientErrorException e) {
			if(e.getRawStatusCode() == 403) {
				System.out.println(e.getMessage());
			} else {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return "doctors";
	}
}
