package pl.dentistoffice.mobile.controller;

import java.net.ConnectException;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class GlobalErrorController implements ErrorController {

	@RequestMapping("/error")
	public String handlingException(Model model, Throwable ex, RuntimeException rex) {
		
		System.out.println("Throwable "+ex.getClass().getName()+" "+ex.getCause()+" "+ex.getLocalizedMessage());
		ex.printStackTrace();
		
		
		if(ex instanceof ConnectException) {
			model.addAttribute("exception", "exception.connect");				
		} else {
			model.addAttribute("exception", "exception.unknown");
			
			System.out.println("RuntimeException "+rex.getClass().getName()+" "+rex.getCause()+" "+rex.getLocalizedMessage());
			rex.printStackTrace();
			
		}
		return "/error";
	}
	
	@Override
	public String getErrorPath() {
		return "/error";
	}

}
