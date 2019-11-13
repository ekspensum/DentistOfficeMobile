package pl.dentistoffice.mobile.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import pl.dentistoffice.mobile.service.UserSrvice;

@Controller
@SessionAttributes(names = {"patient", "token"})
public class PatientController {
	
	@Autowired
	private UserSrvice userSrvice;
	
	@GetMapping(path = "/patient/patient")
	public String patient() {
		return "/patient/patient";
	}
	
	@GetMapping(path = "/patient/login")
	public String login() {
		return "/patient/login";
	}
	
	@PostMapping(path = "/patient/login")
	public String login(@RequestParam(name = "username") String username, @RequestParam(name = "password") String password, HttpSession httpSession, Model model) {
		boolean loggedPatient = userSrvice.loginPatient(username, password, httpSession, model);
		if(loggedPatient) {
			return "redirect:/patient/patient";
		}
		return "/patient/login";
	}

	@GetMapping(path = "/patient/logout")
	public String logout(HttpSession httpSession, Model model) {
		model.addAttribute("patient", null);
		model.addAttribute("token", null);
		httpSession.invalidate();
		return "/patient/logout";
	}
}
