package pl.dentistoffice.mobile.controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.multipart.support.ByteArrayMultipartFileEditor;

@ControllerAdvice
public class AdviceController {
	
	@ExceptionHandler(value = {Exception.class, RuntimeException.class})
	public String exceptionsServing(Exception ex, Model model) {
		
		if(ex instanceof HttpClientErrorException) {
			int rawStatusCode = ((HttpClientErrorException) ex).getRawStatusCode();
			if(rawStatusCode == 404) {
				model.addAttribute("exception", "exception.404");				
			} else if(rawStatusCode == 401) {
				model.addAttribute("exception", "exception.401");				
			} else if(rawStatusCode == 403) {
				model.addAttribute("exception", "exception.403");				
			} else if(rawStatusCode >= 500) {
				model.addAttribute("exception", "exception.5xx");
			}			
		} else if(ex instanceof ResourceAccessException) {
			model.addAttribute("exception", "exception.connect");	
		} else if(ex instanceof HttpServerErrorException) {
			int rawStatusCode = ((HttpServerErrorException) ex).getRawStatusCode();
			if(rawStatusCode == 500) {
				model.addAttribute("exception", "exception.server");
			}			
		}	else if(ex instanceof ServletRequestBindingException) {
			model.addAttribute("exception", "exception.403");
		}
		else {
			model.addAttribute("exception", "exception.unknown");
		}
		
		System.out.println("Advice contr.  "+ex.getClass().getName()+" "+ex.getCause()+" "+ex.getLocalizedMessage());
		
		return "/error";
	}
	
	@InitBinder
	public void dataBinding(WebDataBinder binder) {
		binder.registerCustomEditor(byte[].class, new ByteArrayMultipartFileEditor());
	}

}
