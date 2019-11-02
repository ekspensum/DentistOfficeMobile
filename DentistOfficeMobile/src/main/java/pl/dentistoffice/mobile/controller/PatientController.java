package pl.dentistoffice.mobile.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.fasterxml.jackson.databind.ObjectMapper;

import pl.dentistoffice.mobile.model.Patient;

@Controller
@SessionAttributes(names = {"token", "patient"})
public class PatientController {
	
	
	@GetMapping(path = "/login")
	public String login() {
		return "login";
	}
	
	@PostMapping(path = "/login")
	public String login(@RequestParam(name = "username") String username, @RequestParam(name = "password") String password, Model model) {
		
		String loginParam = "username="+username+"&password="+password;
		try {
			URL url = new URL("http://localhost:8080/dentistoffice/mobile/login");
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Accept-Charset", "UTF-8");
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
			connection.setRequestProperty("Accept", "application/json");
			
			OutputStream outputStream = connection.getOutputStream();
			outputStream.write(loginParam.getBytes("UTF-8"));
			outputStream.close();
			
			int responseCode = connection.getResponseCode();
			if(responseCode == 200) {
				InputStream inputStream = connection.getInputStream();
				ObjectMapper objectMapper = new ObjectMapper();
				Patient patient = objectMapper.readValue(inputStream, Patient.class);
				String token = connection.getHeaderField("token");
				model.addAttribute("token", token);
				
				System.out.println("PatientControler - patient "+patient.getLastName());
				
			} else if(responseCode == 403) {
				System.out.println("PatientControler authentication failed");
			}
			connection.disconnect();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return "login";
	}

}
