package pl.dentistoffice.mobile.test.unit.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.io.IOException;

import javax.servlet.http.HttpSession;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.env.Environment;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import pl.dentistoffice.mobile.controller.PatientController;
import pl.dentistoffice.mobile.model.Patient;
import pl.dentistoffice.mobile.service.ReCaptchaService;
import pl.dentistoffice.mobile.service.UserService;

class PatientControllerTest {
	
	private MockMvc mockMvc;
	
	@InjectMocks
	private PatientController patientController;
	@Mock
	private UserService userService;
	@Mock
	private Environment env;
	@Mock
	private HttpSession httpSession;
	@Mock
	private Model model;
	@Mock
	private BindingResult result;
	@Mock
	private ReCaptchaService reCaptchaService;
	

	@BeforeEach
	void setUp() throws Exception {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/resources/templates/");
        viewResolver.setSuffix(".html"); 
		
		MockitoAnnotations.initMocks(this);
		mockMvc = MockMvcBuilders.standaloneSetup(patientController).setViewResolvers(viewResolver).build();
	}

	@Test
	void testPatient() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/patient/patient"))
						.andExpect(MockMvcResultMatchers.status().isOk())
						.andExpect(MockMvcResultMatchers.view().name("/patient/patient"));
	}

	@Test
	void testLogin() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/patient/login"))
						.andExpect(MockMvcResultMatchers.status().isOk())
						.andExpect(MockMvcResultMatchers.view().name("/patient/login"));
	}

	@Test
	void testLoginStringStringHttpSessionModel() throws Exception {
		String username = "username";
		String password = "password";
		
		mockMvc.perform(MockMvcRequestBuilders.post("/patient/login")
						.param("username", username)
						.param("password", password))
						.andExpect(MockMvcResultMatchers.status().isOk())
						.andExpect(MockMvcResultMatchers.view().name("/patient/login"));
		
		when(userService.loginPatient(username, password, httpSession, model)).thenReturn(true);
		String actual = patientController.login(username, password, httpSession, model);
		assertEquals("redirect:/patient/patient", actual);
	}

	@Test
	void testSuccess() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.post("/patient/success"))
						.andExpect(MockMvcResultMatchers.status().isOk())
						.andExpect(MockMvcResultMatchers.view().name("/patient/success"));
	}

	@Test
	void testDefeat() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.post("/patient/defeat"))
						.andExpect(MockMvcResultMatchers.status().isOk())
						.andExpect(MockMvcResultMatchers.view().name("/patient/defeat"));
	}

	@Test
	void testLogout() throws Exception {
		Patient patient = new Patient();
		patient.setId(1);
		String token = "token";
		
		Object tokenActual = mockMvc.perform(MockMvcRequestBuilders.get("/patient/logout")
															.sessionAttr("patient", patient)
															.sessionAttr("token", token))
															.andExpect(MockMvcResultMatchers.status().isOk())
															.andExpect(MockMvcResultMatchers.view().name("/patient/logout"))
															.andReturn().getModelAndView().getModel().get("token");
		assertNull(tokenActual);
	}

	@Test
	void testRegistrationModel() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/patient/register"))
						.andExpect(MockMvcResultMatchers.status().isOk())
						.andExpect(MockMvcResultMatchers.view().name("/patient/register"));
	}

	@Test
	void testRegistrationPatientBindingResultModelMultipartFile() throws IOException, Exception {
		MockMultipartFile photo = new MockMultipartFile("file", "file".getBytes());
		Patient patient = new Patient();
		patient.setPhoto(photo.getBytes());
		patient.setEmail("email@email.com");
		
		mockMvc.perform(MockMvcRequestBuilders.multipart("/patient/register")
						.file("photo", photo.getBytes())
						.flashAttr("patient", patient)
						.param("g-recaptcha-response", "reCaptchaResponse"))
						.andExpect(MockMvcResultMatchers.status().isOk())
						.andExpect(MockMvcResultMatchers.view().name("/patient/register"));
		
		when(userService.registerPatient(patient)).thenReturn("true");
		when(reCaptchaService.verify("reCaptchaResponse")).thenReturn(true);
		String answerActual = patientController.registration(patient, result, model, photo, "reCaptchaResponse");
		assertEquals("forward:/patient/success", answerActual);
		
		when(userService.registerPatient(patient)).thenReturn("NotDistinctLogin");
		answerActual = patientController.registration(patient, result, model, photo, "reCaptchaResponse");
		assertEquals("forward:/patient/defeat", answerActual);
		
		when(userService.registerPatient(patient)).thenReturn("Timeout");
		answerActual = patientController.registration(patient, result, model, photo, "reCaptchaResponse");
		assertEquals("forward:/patient/defeat", answerActual);
	}

	@Test
	void testEditDataPatientModel() throws Exception {
		Patient patient = new Patient();
		
		mockMvc.perform(MockMvcRequestBuilders.get("/patient/edit")
						.sessionAttr("patient", patient))
						.andExpect(MockMvcResultMatchers.status().isOk())
						.andExpect(MockMvcResultMatchers.view().name("/patient/edit"));
	}

	@Test
	void testEditDataPatientBindingResultModelMultipartFileByteArrayStringHttpSession() throws Exception {
		String token = "token";
		MockMultipartFile photo = new MockMultipartFile("file", "file".getBytes());
		Patient patient = new Patient();
		patient.setPhoto(photo.getBytes());
		patient.setEmail("email@email.com");
		
		mockMvc.perform(MockMvcRequestBuilders.multipart("/patient/edit")
						.file("photo", photo.getBytes())
						.sessionAttr("patient", patient)
						.sessionAttr("image", photo.getBytes())
						.sessionAttr("token", token))
						.andExpect(MockMvcResultMatchers.status().isOk())
						.andExpect(MockMvcResultMatchers.view().name("/patient/edit"));
		
		when(userService.editPatient(token, patient)).thenReturn("true");
		String answerActual = patientController.editData(patient, result, model, photo, photo.getBytes(), token, httpSession);
		assertEquals("forward:/patient/success", answerActual);
		
		when(userService.editPatient(token, patient)).thenReturn("NotDistinctLogin");
		answerActual = patientController.editData(patient, result, model, photo, photo.getBytes(), token, httpSession);
		assertEquals("forward:/patient/defeat", answerActual);
	}

}
