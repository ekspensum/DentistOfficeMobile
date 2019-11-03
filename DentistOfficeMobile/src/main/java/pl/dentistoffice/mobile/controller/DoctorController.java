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
import pl.dentistoffice.mobile.service.UserSrvice;

@Controller
@SessionAttributes(names = {"token", "patient"})
public class DoctorController {

	@Autowired
	private UserSrvice userSrvice;
	
	@GetMapping(path = "/doctors")
	public String getDoctors(@SessionAttribute(name = "token", required = false) String token, Model model) {

		try {
			ResponseEntity<DoctorListWrapper> responseEntity = userSrvice.getDoctors(token);
			if(responseEntity.getStatusCodeValue() == 200) {
				
				System.out.println(responseEntity.getBody().getDoctorList().get(2).getWorkingWeek().getWorkingWeekMapByte().length);
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
