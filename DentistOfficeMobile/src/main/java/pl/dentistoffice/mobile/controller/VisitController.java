package pl.dentistoffice.mobile.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.fasterxml.jackson.databind.ObjectMapper;

import pl.dentistoffice.mobile.model.VisitStatus;

@Controller
@SessionAttributes(names = {"token", "patient"})
public class VisitController {
	
	@GetMapping(path = "/visitStatus")
	public String getVisitStatus() {
		return "visitStatus";
	}

	@PostMapping(path = "/visitStatus")
	public String getVisitStatus(@SessionAttribute(name = "token", required = false) String token, @RequestParam(name = "id") String statusId) {
		String loginParam = "id="+statusId;
		try {
			URL url = new URL("http://localhost:8080/dentistoffice/mobile/visitStatus");
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Accept-Charset", "UTF-8");
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
			connection.setRequestProperty("Accept", "application/json");
			if(token != null) {
				connection.setRequestProperty("token", token);				
			}
			
			OutputStream outputStream = connection.getOutputStream();
			outputStream.write(loginParam.getBytes("UTF-8"));
			outputStream.close();
			
			int responseCode = connection.getResponseCode();
			if(responseCode == 200) {
				InputStream inputStream = connection.getInputStream();
				ObjectMapper objectMapper = new ObjectMapper();
				VisitStatus visitStatus = objectMapper.readValue(inputStream, VisitStatus.class);
				
				System.out.println("VisitControler - status "+visitStatus.getDescription());
				
			} else if(responseCode == 403) {
				System.out.println("VisitControler authentication failed");
			}
			connection.disconnect();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "visitStatus";
	}
}
