package pl.dentistoffice.mobile.controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;

import pl.dentistoffice.mobile.model.DentalTreatment;
import pl.dentistoffice.mobile.model.Doctor;
import pl.dentistoffice.mobile.model.Patient;
import pl.dentistoffice.mobile.model.VisitStatus;
import pl.dentistoffice.mobile.service.DentalTreatmentService;
import pl.dentistoffice.mobile.service.UserService;
import pl.dentistoffice.mobile.service.VisitService;

@Controller
@SessionAttributes(names = {"patient", "token", "allDoctorsList", "treatments", "dayStart", "doctor"})
public class VisitController {

	@Autowired
	private VisitService visitService;
	@Autowired
	private UserService userService;
	@Autowired
	private DentalTreatmentService treatmentService;
	
	private int dayStart = 0;
	private int dayEnd = 0;
	
	@RequestMapping(path = "/visit/selectDoctor")
	public String visitSelectDoctorByPatient(Model model) {
		List<Doctor> allDoctorsList = userService.getAllDoctors();
		model.addAttribute("allDoctorsList", allDoctorsList);
		return "/visit/selectDoctor";
	}	
	
	@PostMapping(path = "/visit/toReserve")
	public String visitToReserveByPatient(@RequestParam(name = "doctorId", required = false) String idDoctor, 
																@RequestParam(name = "weekResultDriver", required = false) String weekResultDriver,
																@SessionAttribute(name = "doctor", required = false) Doctor doctor,
																@SessionAttribute(name = "dayStart", required = false) Integer dayStartFromSession,
																@SessionAttribute(name = "allDoctorsList") List<Doctor> allDoctorsList,
																@SessionAttribute(name = "treatments", required = false) List<DentalTreatment> treatmentsFromSession,
																@SessionAttribute(name = "token") String token,
																Model model) {
				
		if(treatmentsFromSession == null) {
			List<DentalTreatment> treatments = treatmentService.getDentalTreatmentsList(token); 
			model.addAttribute("treatments", treatments);			
		} else {
			model.addAttribute("treatments", treatmentsFromSession);
		}
		
		Map<LocalDate, Map<LocalTime, Boolean>> workingWeekFreeTimeMap = null;
		if (weekResultDriver == null) {
			doctor = userService.getDoctor(Integer.valueOf(idDoctor), allDoctorsList);
			model.addAttribute("doctor", doctor);
			workingWeekFreeTimeMap = visitService.getWorkingWeekFreeTimeMap(doctor.getId(), 0, 7);
			model.addAttribute("dayStart", 0);
			model.addAttribute("disableLeftArrow", "YES");
		} else {
			if (weekResultDriver.equals("stepRight")) {
				if (dayStartFromSession < 21) {
					dayStart = dayStartFromSession + 7;
					dayEnd = dayStart + 7;
					model.addAttribute("dayStart", dayStart);
					if(dayStart == 21) {
						model.addAttribute("disableRightArrow", "YES");						
					}
				} else {
					dayStartFromSession = 21;
					dayEnd = dayStartFromSession + 7;
					model.addAttribute("disableRightArrow", "YES");
				}
				workingWeekFreeTimeMap = visitService.getWorkingWeekFreeTimeMap(doctor.getId(), dayStart, dayEnd);

			} else if (weekResultDriver.equals("stepLeft")) {
				if (dayStartFromSession > 0) {
					dayStart = dayStartFromSession - 7;
					dayEnd = dayStart + 7;
					model.addAttribute("dayStart", dayStart);
					if(dayStart == 0) {
						model.addAttribute("disableLeftArrow", "YES");						
					}
				} else {
					dayStartFromSession = 0;
					dayEnd = dayStartFromSession + 7;
					model.addAttribute("disableLeftArrow", "YES");
				}
				workingWeekFreeTimeMap = visitService.getWorkingWeekFreeTimeMap(doctor.getId(), dayStart, dayEnd);
			}
		}
			
		model.addAttribute("workingWeekFreeTimeMap", workingWeekFreeTimeMap);
		model.addAttribute("dayOfWeekPolish", userService.dayOfWeekPolish());
		return "/visit/toReserve";
	}
	
	@RequestMapping(path = "/visit/reservation", method = RequestMethod.POST)
	public String visitReservationByPatient(@SessionAttribute("doctor") Doctor doctor, 
																@RequestParam(name = "dateTime", required = false) String [] dateTime, 
																@RequestParam("treatment1") String treatment1Id,
																@RequestParam("treatment2") String treatment2Id,
																@RequestParam("treatment3") String treatment3Id,
																@SessionAttribute(name = "token") String token,
																@SessionAttribute(name = "patient") Patient patient,
																Model model) throws Exception {
		
		if (dateTime != null && dateTime.length == 1) {
			boolean newVisit = visitService.addNewVisitByMobilePatient(doctor.getId(), patient.getId(), dateTime[0], treatment1Id, treatment2Id, 
																											treatment3Id,	token);
			if (newVisit) {
				model.addAttribute("success", "patient.success.newVisit");
				return "forward:/patient/success";
			} else {
				model.addAttribute("defeat", "patient.defeat.newVisit");
				return "forward:/patient/defeat";
			}
		} else {
			model.addAttribute("defeat", "patient.defeat.newVisit.checkbox");
			return "forward:/patient/defeat";
		}		
	}

	
	
//======================	
	
	
	@GetMapping(path = "/visitStatus")
	public String getVisitStatus() {
		return "/visit/visitStatus";
	}

	@PostMapping(path = "/visitStatus")
	public String getVisitStatus(@SessionAttribute(name = "token", required = false) String token, 
												@RequestParam(name = "id") String statusId, 
												Model model) {
		
			if(token != null && !token.equals("")) {
				ResponseEntity<VisitStatus> responseEntity = visitService.getVisitStatus(token, statusId);
				
				if(responseEntity.getStatusCodeValue() == 200) {
					System.out.println("VisitControler - status "+responseEntity.getBody().getDescription());					
				} else {
					System.out.println("VisitController - doctors, response: "+responseEntity.getStatusCode());
				}	
			} else {
				System.out.println("VisitController - lack session (not logged)");
			}
		
		return "/visit/visitStatus";
	}
}
