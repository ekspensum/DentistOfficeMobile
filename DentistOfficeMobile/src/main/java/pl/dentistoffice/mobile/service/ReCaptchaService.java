package pl.dentistoffice.mobile.service;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.net.ssl.HttpsURLConnection;

import org.springframework.stereotype.Service;

@Service
public class ReCaptchaService {
	
	private final String url = "https://www.google.com/recaptcha/api/siteverify";
	private final String secret = "6LeiecsUAAAAAL7eYRBnV-hHi4dy99htTMb1PC7f"; //Nazwa.pl
	private final String USER_AGENT = "Mozilla/5.0";

	public boolean verify(String reCaptchaResponse) throws IOException {
		if (reCaptchaResponse == null || "".equals(reCaptchaResponse)) {
			return false;
		}
		
		try{
		URL obj = new URL(url);
		HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

		// add request header
		con.setRequestMethod("POST");
		con.setRequestProperty("User-Agent", USER_AGENT);
		con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

		String postParams = "secret=" + secret + "&response=" + reCaptchaResponse;

		// Send post request
		con.setDoOutput(true);
		DataOutputStream dataOutputStream = new DataOutputStream(con.getOutputStream());
		dataOutputStream.writeBytes(postParams);
		dataOutputStream.flush();
		dataOutputStream.close();

		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		StringBuffer response = new StringBuffer();
		
		while(in.ready()) {
			response.append(in.readLine());
		}
		in.close();
		
		//parse JSON response and return 'success' value
		JsonReader jsonReader = Json.createReader(new StringReader(response.toString()));
		JsonObject jsonObject = jsonReader.readObject();
		jsonReader.close();
		
		return jsonObject.getBoolean("success");
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
}
