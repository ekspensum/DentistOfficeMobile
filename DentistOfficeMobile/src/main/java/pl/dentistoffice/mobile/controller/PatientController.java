package pl.dentistoffice.mobile.controller;

import java.io.IOException;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.multipart.MultipartFile;

import pl.dentistoffice.mobile.model.Patient;
import pl.dentistoffice.mobile.service.UserService;

@Controller
@SessionAttributes(names = {"patient", "token", "image"})
public class PatientController {

	@Autowired
	private UserService userSrvice;
	
	@GetMapping(path = "/patient/patient")
	public String patient() {
		return "/patient/patient";
	}
	
	@GetMapping(path = "/patient/login")
	public String login() {
		return "/patient/login";
	}
	
	@PostMapping(path = "/patient/login")
	public String login(@RequestParam(name = "username") String username, 
								@RequestParam(name = "password") String password, 
								HttpSession httpSession, 
								Model model) {
		
		boolean loggedPatient = userSrvice.loginPatient(username, password, httpSession, model);
		if(loggedPatient) {
			return "redirect:/patient/patient";
		}
		return "/patient/login";
	}
	
	@PostMapping(path = "/patient/success")
	public String success() {
		return "/patient/success";
	}

	@PostMapping(path = "/patient/defeat")
	public String defeat() {
		return "/patient/defeat";
	}
	
	@GetMapping(path = "/patient/logout")
	public String logout(HttpSession httpSession, Model model) {
		model.addAttribute("patient", null);
		model.addAttribute("token", null);
		httpSession.invalidate();
		return "/patient/logout";
	}
	
	@GetMapping(path = "/patient/edit")
	public String editData(@SessionAttribute("patient") Patient patient, Model model) {
		model.addAttribute("image", patient.getPhoto());
		return "/patient/edit";
	}
	
	@PostMapping(path = "/patient/edit")
	public String editData(@Valid Patient patient, 
										BindingResult result, 
										Model model,
										@RequestParam("photo") MultipartFile photo, 
										@SessionAttribute("image") byte [] image,
										@SessionAttribute("token") String token,
										HttpSession httpSession
										) throws IOException {

		
		if(photo.getBytes().length == 0) {
			patient.setPhoto(image);
		}
		if(!result.hasErrors()) {
			Boolean editPatient = userSrvice.editPatient(token, patient);
			if(editPatient) {
				model.addAttribute("success", "patient.success.editPatient");
				model.addAttribute("patient", null);
				model.addAttribute("token", null);
				httpSession.invalidate();
				return "forward:/patient/success";				
			} else {
				model.addAttribute("defeat", "patient.defeat.editPatient");
				return "forward:/patient/defeat";
			}
		}
		return "/patient/edit";
	}
}
