package pl.dentistoffice.mobile.controller;

import java.net.ConnectException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class GlobalErrorController implements ErrorController {

	@RequestMapping("/error")
	public String handlingException(Model model, Throwable ex, HttpServletRequest request) {
		
		System.out.println("Throwable "+ex.getClass().getName()+" "+ex.getCause()+" "+ex.getLocalizedMessage());
//		ex.printStackTrace();
		
		if(request.getAttribute("SecurityFilter").equals("403")) {
			model.addAttribute("exception", "exception.403");	
		} else if(ex instanceof ConnectException) {
			model.addAttribute("exception", "exception.connect");				
		} else {
			model.addAttribute("exception", "exception.unknown");			
		}
		return "/error";
	}
	
	@Override
	public String getErrorPath() {
		return "/error";
	}

}
