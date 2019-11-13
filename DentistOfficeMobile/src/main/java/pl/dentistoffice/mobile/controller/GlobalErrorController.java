package pl.dentistoffice.mobile.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;

@Controller
public class GlobalErrorController implements ErrorController {

	@RequestMapping("/error")
	public String handlingException(Model model, Throwable ex) {
		
		System.out.println(ex.getClass().getName()+" "+ex.getCause()+" "+ex.getLocalizedMessage());
		ex.printStackTrace();
		
		if(ex instanceof ResourceAccessException) {
			model.addAttribute("exception", "exception_connect");	
		} else if(ex instanceof HttpClientErrorException) {
			int rawStatusCode = ((HttpClientErrorException) ex).getRawStatusCode();
			if(rawStatusCode == 404) {
				model.addAttribute("exception", "exception_404");				
			} else if(rawStatusCode == 401) {
				model.addAttribute("exception", "exception_401");				
			} else if(rawStatusCode == 403) {
				model.addAttribute("exception", "exception_403");				
			} else if(rawStatusCode >= 500) {
				model.addAttribute("exception", "exception_5xx");
			}			
		} else {
			model.addAttribute("exception", "exception_unknown");
		}
		return "/error";
	}
	
	
	@Override
	public String getErrorPath() {
		return "/error";
	}

}
