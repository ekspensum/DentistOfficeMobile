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
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import pl.dentistoffice.mobile.model.Patient;

@Controller
@SessionAttributes(names = {"token", "patient"})
public class DoctorController {

	@GetMapping(path = "/doctors")
	public String getDoctors(@SessionAttribute(name = "token", required = false) String token, Model model) {
		
		try {
			URL url = new URL("http://localhost:8080/dentistoffice/mobile/doctors");
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoInput(true);
			connection.setRequestMethod("GET");
			connection.setRequestProperty("Accept-Charset", "UTF-8");
			connection.setRequestProperty("Accept", "application/json");
			if(token != null) {
				connection.setRequestProperty("token", token);				
			}
			
			int responseCode = connection.getResponseCode();
			if(responseCode == 200) {
				InputStream inputStream = connection.getInputStream();
				ObjectMapper objectMapper = new ObjectMapper();

				JsonFactory factory = new JsonFactory();
				JsonParser  parser  = factory.createParser(inputStream);

//				String readValueAsTree = parser.readValueAsTree().toString();
				
//				JsonParser jsonParser = factory.createParser(readValueAsTree);
				
				while(!parser.isClosed()){
//				    JsonToken jsonToken = jsonParser.nextToken();
				    JsonToken jsonToken = parser.nextToken();

				    if(JsonToken.START_ARRAY.equals(jsonToken)){
				    	System.out.println("jsonToken = " + jsonToken);
				    	
				    }
				    
				    if(JsonToken.START_OBJECT.equals(jsonToken)){
				    	System.out.println("jsonToken = " + jsonToken);
				    	
				    }
				    
				    if(JsonToken.FIELD_NAME.equals(jsonToken)){
				    	System.out.println("jsonToken = " + jsonToken);

//				    	String currentName = parser.getCurrentName();
//				        parser.nextToken();
//				        switch (currentName) {
//				            case "someObject":
//				                Object someObject = objectMapper.readValue(jsonParser, Object.class);
//				                System.out.println(someObject.toString());
//				                break;
//				        }
				    }
				}
				
				
				
			} else if(responseCode == 403) {
				System.out.println("DoctorControler authentication failed");
			}
			connection.disconnect();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		return "doctors";
	}
}
