package pl.dentistoffice.mobile.test;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;

import pl.dentistoffice.mobile.config.DentistOfficeMobileApplication;
import pl.dentistoffice.mobile.model.Doctor;
import pl.dentistoffice.mobile.model.Patient;
import pl.dentistoffice.mobile.model.User;
import pl.dentistoffice.mobile.service.UserService;

@SpringBootTest(classes = DentistOfficeMobileApplication.class)
@AutoConfigureMockMvc
@TestMethodOrder(OrderAnnotation.class)
class DentistOfficeMobileIntegrationTest {
	
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private UserService userService;
	@Autowired
	private HttpServletRequest request;
	@Autowired
	private HttpSession httpSession;
	
	@Test
	@Order(1)
	void homePagesTest() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/"))
						.andExpect(MockMvcResultMatchers.status().isOk())
						.andExpect(MockMvcResultMatchers.view().name("/home/home"));
		mockMvc.perform(MockMvcRequestBuilders.get("/doctors"))
						.andExpect(MockMvcResultMatchers.status().isOk())
						.andExpect(MockMvcResultMatchers.view().name("/home/doctors"))
						.andExpect(MockMvcResultMatchers.model().attributeExists("doctorList"));
		mockMvc.perform(MockMvcRequestBuilders.get("/services"))
						.andExpect(MockMvcResultMatchers.status().isOk())
						.andExpect(MockMvcResultMatchers.view().name("/home/services"))
						.andExpect(MockMvcResultMatchers.model().attributeExists("treatmentCategoriesList"));
		mockMvc.perform(MockMvcRequestBuilders.post("/services")
						.param("categoryId", "2"))
						.andExpect(MockMvcResultMatchers.status().isOk())
						.andExpect(MockMvcResultMatchers.view().name("/home/services"))
						.andExpect(MockMvcResultMatchers.model().attributeExists("selectedTreatmentCategory"))
						.andExpect(MockMvcResultMatchers.model().attributeExists("treatmentCategoriesList"));
		@SuppressWarnings("unchecked")
		List<Doctor> allDoctors = (List<Doctor>) mockMvc.perform(MockMvcRequestBuilders.get("/agenda"))
						.andExpect(MockMvcResultMatchers.status().isOk())
						.andExpect(MockMvcResultMatchers.view().name("/home/agenda"))
						.andExpect(MockMvcResultMatchers.model().attributeExists("allDoctors"))
						.andReturn().getModelAndView().getModel().get("allDoctors");
		String doctorId = "1";
		Doctor doctor = null;
		for(int i=0; i<allDoctors.size(); i++) {
			if(allDoctors.get(i).getId() == Integer.valueOf(doctorId)) {
				doctor = allDoctors.get(i);
			}
		}
		String weekResultDriver = null;
		mockMvc.perform(MockMvcRequestBuilders.post("/agenda")
						.param("doctorId", doctorId)
						.sessionAttr("doctor", doctor)
						.sessionAttr("allDoctors", allDoctors)
						.param("weekResultDriver", weekResultDriver)
						.sessionAttr("dayStart", 0))
						.andExpect(MockMvcResultMatchers.status().isOk())
						.andExpect(MockMvcResultMatchers.view().name("/home/agenda"))
						.andExpect(MockMvcResultMatchers.model().attributeExists("disableLeftArrow"));
		mockMvc.perform(MockMvcRequestBuilders.get("/contactus"))
						.andExpect(MockMvcResultMatchers.status().isOk())
						.andExpect(MockMvcResultMatchers.view().name("/home/contactus"))
						.andExpect(MockMvcResultMatchers.model().attributeExists("contactUs"));
	}

	@Test
	@Order(2)
	void loginTest() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.post("/patient/login")
						.param("username", "pacjent1")
						.param("password", "Pacjent11"))
						.andExpect(MockMvcResultMatchers.status().is3xxRedirection())
						.andExpect(MockMvcResultMatchers.view().name("redirect:/patient/patient"));
	}
	
	@Test
	@Order(3)
	void editPatientData() throws Exception {
//		getting origin patient data:
		Model model = new ConcurrentModel();
		userService.loginPatient("pacjent1", "Pacjent11", httpSession, model);
		String decodeToken = (String) model.getAttribute("token");
		Patient patient = (Patient) model.getAttribute("patient");
		
//		add patient and token to session for security filter
		request.getSession().setAttribute("patient", patient);
		request.getSession().setAttribute("token", decodeToken);

//		change patient data:
//		- get method for patient edit (for assertion)
		mockMvc.perform(MockMvcRequestBuilders.get("/patient/edit")
						.sessionAttr("patient", patient)
						.sessionAttr("token", decodeToken))
						.andExpect(MockMvcResultMatchers.status().isOk())
						.andExpect(MockMvcResultMatchers.view().name("/patient/edit"));
		
//		- post method for patient edit
		patient.setLastName("lastNameTest");
		User user = patient.getUser();
		user.setPasswordField("Pacjent11");
		patient.setUser(user);
		mockMvc.perform(MockMvcRequestBuilders
						.multipart("/patient/edit")
						.file("photo", patient.getPhoto())
						.flashAttr("patient", patient)
						.sessionAttr("image", patient.getPhoto())
						.sessionAttr("token", decodeToken)
						.sessionAttr("patient", patient))
						.andExpect(MockMvcResultMatchers.status().isOk())
						.andExpect(MockMvcResultMatchers.view().name("forward:/patient/success"));

//		assertion new data patient
		Patient patientExpected = (Patient) mockMvc.perform(MockMvcRequestBuilders.post("/patient/login")
																				.param("username", "pacjent1")
																				.param("password", "Pacjent11"))
																				.andReturn().getRequest().getSession().getAttribute("patient");
		assertEquals(patientExpected.getLastName(), "lastNameTest");
		
//		to bring back patient data
//		- patient should be login again because after edit data, patient was logout
		userService.loginPatient("pacjent1", "Pacjent11", httpSession, model);
		decodeToken = (String) model.getAttribute("token");
		patient = (Patient) model.getAttribute("patient");
		patient.setLastName("Warszawska");
		user = patient.getUser();
		user.setPasswordField("Pacjent11");
		patient.setUser(user);
		mockMvc.perform(MockMvcRequestBuilders
						.multipart("/patient/edit")
						.file("photo", patient.getPhoto())
						.flashAttr("patient", patient)
						.sessionAttr("image", patient.getPhoto())
						.sessionAttr("token", decodeToken)
						.sessionAttr("patient", patient))
						.andExpect(MockMvcResultMatchers.status().isOk())
						.andExpect(MockMvcResultMatchers.view().name("forward:/patient/success"));

//		assertion revert patient data
		patientExpected = (Patient) mockMvc.perform(MockMvcRequestBuilders.post("/patient/login")
																				.param("username", "pacjent1")
																				.param("password", "Pacjent11"))
																				.andReturn().getRequest().getSession().getAttribute("patient");
		assertEquals(patientExpected.getLastName(), "Warszawska");
	}
}
