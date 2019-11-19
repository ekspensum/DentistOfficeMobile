package pl.dentistoffice.mobile.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.client.HttpClientErrorException;

import pl.dentistoffice.mobile.model.Doctor;
import pl.dentistoffice.mobile.model.DoctorListWrapper;
import pl.dentistoffice.mobile.service.UserService;

@Controller
@SessionAttributes(names = {"patient", "token"})
public class DoctorController {

	@Autowired
	private UserService userSrvice;
	
//	@GetMapping(path = "/doctors")
//	public String getDoctors(@SessionAttribute(name = "token", required = false) String token, Model model) {
//
//		try {
//			if(token != null && !token.equals("")) {
//				ResponseEntity<DoctorListWrapper> responseEntity = userSrvice.getDoctors(token);
//				if(responseEntity.getStatusCodeValue() == 200) {
//					List<Doctor> doctorList = responseEntity.getBody().getDoctorList();
//					model.addAttribute("doctorList", doctorList);
//					
//					
//					System.out.println("Answer "+doctorList.get(0).getWorkingWeek().getWorkingWeekMapByte().length);
//				} else {
//					System.out.println("DoctorController - doctors, response: "+responseEntity.getStatusCode());
//				}
//			} else {
//				System.out.println("DoctorController - lack session (not logged)");
//			}
//			
//		} catch (HttpClientErrorException e) {
//			if(e.getRawStatusCode() == 403) {
//				System.out.println(e.getMessage());
//			} else {
//				e.printStackTrace();
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		
//		return "doctors";
//	}
}
