package pl.dentistoffice.mobile.config;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;

import pl.dentistoffice.mobile.model.Patient;

@Component
public class SecurityFilter implements Filter {

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		Filter.super.init(filterConfig);
	}

	@Override
	public void destroy() {
		Filter.super.destroy();
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)	throws IOException, ServletException {
		
		HttpServletRequest httpServletRequest = (HttpServletRequest) request;
		HttpServletResponse httpServletResponse = (HttpServletResponse) response;
		Patient patient = (Patient) httpServletRequest.getSession().getAttribute("patient");
		String token = (String) httpServletRequest.getSession().getAttribute("token");
		String [] urlPatern = {"/visit/selectDoctor", "/visit/toReserve", "/visit/reservation", "/visit/myVisits", "/visit/deleteVisit", "/patient/edit"};
		if(patient == null || token == null) {
			for(int i=0; i<urlPatern.length; i++) {
				if(urlPatern[i].equals(httpServletRequest.getRequestURI())) {
					httpServletRequest.setAttribute("SecurityFilter", "403");
					httpServletRequest.getRequestDispatcher("/error").forward(httpServletRequest, httpServletResponse);
					return;
				}
			}
		}
		chain.doFilter(httpServletRequest, httpServletResponse);
	}

}
