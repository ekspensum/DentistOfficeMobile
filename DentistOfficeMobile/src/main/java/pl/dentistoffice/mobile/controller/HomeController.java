package pl.dentistoffice.mobile.controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.multipart.MultipartFile;

import pl.dentistoffice.mobile.model.ContactUs;
import pl.dentistoffice.mobile.model.Doctor;
import pl.dentistoffice.mobile.model.TreatmentCategory;
import pl.dentistoffice.mobile.service.DentalTreatmentService;
import pl.dentistoffice.mobile.service.ReCaptchaService;
import pl.dentistoffice.mobile.service.SendEmail;
import pl.dentistoffice.mobile.service.UserSrvice;
import pl.dentistoffice.mobile.service.VisitService;

@Controller
@SessionAttributes(names = {"patient", "token", "allDoctors", "doctor", "dayStart"})
public class HomeController {

	@Autowired
	private Environment env;
	
	@Autowired
	private UserSrvice userService;

	@Autowired
	private SendEmail sendEmail;
	
	@Autowired
	private ReCaptchaService reCaptchaService;
	
	@Autowired
	private DentalTreatmentService dentalTreatmentService;
	
	@Autowired
	private VisitService visitService;
	
	private int dayStart = 0;
	private int dayEnd = 0;

	@GetMapping(path = "/")
	public String home() {
		return "/home/home";
	}
	
	@GetMapping(path = "/doctors")
	public String getDoctors(Model model) {
		List<Doctor>	doctorList = userService.getAllDoctors();
		model.addAttribute("doctorList", doctorList);
		return "/home/doctors";
	}
	
	@GetMapping(path="/services")
	public String services(Model model) {
		List<TreatmentCategory> treatmentCategoriesList = dentalTreatmentService.getTreatmentCategoriesList();
		model.addAttribute("treatmentCategoriesList", treatmentCategoriesList);		
		return "/home/services";
	}
	
	@PostMapping(path="/services")
	public String services(@RequestParam("categoryId") String categoryId, Model model) {
		TreatmentCategory selectedTreatmentCategory = dentalTreatmentService.getTreatmentCategory(Integer.valueOf(categoryId));
		model.addAttribute("selectedTreatmentCategory", selectedTreatmentCategory);
		List<TreatmentCategory> treatmentCategoriesList = dentalTreatmentService.getTreatmentCategoriesList();
		model.addAttribute("treatmentCategoriesList", treatmentCategoriesList);		
		return "/home/services";
	}
	
	@GetMapping(path="/agenda")
	public String agenda(Model model) {
		List<Doctor> allDoctors = userService.getAllDoctors();
		model.addAttribute("allDoctors", allDoctors);		
		return "/home/agenda";
	}
	
	@PostMapping(path="/agenda")
	public String agenda(@RequestParam(name = "doctorId", required = false) String doctorId,
											@SessionAttribute(name = "doctor", required = false) Doctor doctorFromSession,
											@SessionAttribute(name = "allDoctors") List<Doctor> allDoctors,
											@RequestParam(name = "weekResultDriver", required = false) String weekResultDriver,
											@SessionAttribute(name = "dayStart", required = false) Integer dayStartFromSession,
											Model model) {
		Doctor doctor;
		Map<LocalDate, Map<LocalTime, Boolean>> workingWeekFreeTimeMap = null;
		if (weekResultDriver == null) {
			doctor = userService.getDoctor(Integer.valueOf(doctorId), allDoctors);
			model.addAttribute("doctor", doctor);
			workingWeekFreeTimeMap = visitService.getWorkingWeekFreeTimeMap(doctor.getId(), 0, 7);
			model.addAttribute("dayStart", 0);
			model.addAttribute("disableLeftArrow", "YES");
		} else {
			doctor = doctorFromSession;
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
		return "/home/agenda";
	}
	
	@GetMapping(path = "/contactus")
	public String contactUs(Model model) {
		model.addAttribute("contactUs", new ContactUs());
		return "/home/contactus";
	}
	
	@PostMapping(path = "/contactus")
	public String sendMessage(@Valid @ModelAttribute ContactUs contactUs, BindingResult result,
							  @RequestParam("attachment") MultipartFile file, Model model,
							  @RequestParam(name = "g-recaptcha-response") String reCaptchaResponse
			  					) throws Exception  {
		
		String mailText = "<font color='blue' size='3'>" + contactUs.getMessage() + "<br><br><br>"
						+ "Adres e-mail nadawcy: " + contactUs.getReplyMail() + "\n" + "</font><br><br>";
		boolean verifyReCaptcha = reCaptchaService.verify(reCaptchaResponse);

		if (!result.hasErrors() && verifyReCaptcha) {
			try {
				sendEmail.sendEmail(env, env.getProperty("mailFrom"), // in this case mailTo == mailFrom
						contactUs.getSubject(), mailText, contactUs.getReplyMail(), file.getBytes(),
						file.getOriginalFilename());
				model.addAttribute("alert", "YES");
			} catch (Exception e) {
				e.printStackTrace();
				model.addAttribute("alert", "NO");
			}
		} else {
			if (!verifyReCaptcha) {
				model.addAttribute("reCaptchaError", "reCaptchaError");
			}
		}
		return "/home/contactus";
	}

}
