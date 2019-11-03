package pl.dentistoffice.mobile.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.client.HttpClientErrorException;

import pl.dentistoffice.mobile.model.VisitStatus;
import pl.dentistoffice.mobile.service.VisitService;

@Controller
@SessionAttributes(names = {"token", "patient"})
public class VisitController {
	
	@Autowired
	private VisitService visitService;
	
	@GetMapping(path = "/visitStatus")
	public String getVisitStatus() {
		return "visitStatus";
	}

	@PostMapping(path = "/visitStatus")
	public String getVisitStatus(@SessionAttribute(name = "token", required = false) String token, @RequestParam(name = "id") String statusId) {
		
		try {
			ResponseEntity<VisitStatus> responseEntity = visitService.getVisitStatus(token, statusId);

			if(responseEntity.getStatusCodeValue() == 200) {
				
				System.out.println("VisitControler - status "+responseEntity.getBody().getDescription());
				
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
		
		return "visitStatus";
	}
}
