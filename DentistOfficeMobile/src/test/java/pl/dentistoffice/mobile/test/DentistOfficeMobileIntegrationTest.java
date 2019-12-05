package pl.dentistoffice.mobile.test;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

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
import pl.dentistoffice.mobile.model.Visit;
import pl.dentistoffice.mobile.model.VisitStatus;
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
		
//		change patient data:
//		- add patient and token to session for security filter
//		- get method for patient edit (for assertion)
		mockMvc.perform(MockMvcRequestBuilders.get("/patient/edit")
						.sessionAttr("patient", patient)
						.sessionAttr("token", decodeToken))
						.andExpect(MockMvcResultMatchers.status().isOk())
						.andExpect(MockMvcResultMatchers.view().name("/patient/edit"));
		
//		- post method for patient edit and add patient and token to session for security filter
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
//		- add patient and token to session for security filter
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
	
	@Test
	@Order(4)
	void logoutPatientTest() throws Exception {
		Model model = new ConcurrentModel();
		userService.loginPatient("pacjent1", "Pacjent11", httpSession, model);
		String decodeToken = (String) model.getAttribute("token");
		Patient patient = (Patient) model.getAttribute("patient");

//		add patient and token to session for security filter
		mockMvc.perform(MockMvcRequestBuilders.get("/patient/logout")
						.sessionAttr("patient", patient)
						.sessionAttr("token", decodeToken))
						.andExpect(MockMvcResultMatchers.status().isOk())
						.andExpect(MockMvcResultMatchers.view().name("/patient/logout"));
	}
	
	@Test
	@Order(5)
	void addNewVisitAndDeleteThem() throws Exception {
//		getting origin patient data:
		Model modelForSession = new ConcurrentModel();
		userService.loginPatient("pacjent1", "Pacjent11", httpSession, modelForSession);
		String decodeToken = (String) modelForSession.getAttribute("token");
		Patient patient = (Patient) modelForSession.getAttribute("patient");
		
//		checking controller action and getting doctor list 
//		add patient and token to session for security filter
		@SuppressWarnings("unchecked")
		List<Doctor> allDoctorsList = (List<Doctor>) mockMvc.perform(MockMvcRequestBuilders
																							.get("/visit/selectDoctor")
																							.sessionAttr("patient", patient)
																							.sessionAttr("token", decodeToken))
																							.andExpect(MockMvcResultMatchers.status().isOk())
																							.andExpect(MockMvcResultMatchers.view().name("/visit/selectDoctor"))
																							.andReturn().getModelAndView().getModel().get("allDoctorsList");
		
//		prepare to add new visit and checking controller action as well getting model
//		add patient and token to session for security filter
		String doctorId = "1";
		String weekResultDriver = null;
		Integer dayStart = 0;	
		Map<String, Object> modelForNewVisit = mockMvc.perform(MockMvcRequestBuilders.post("/visit/toReserve")
																		.param("doctorId", doctorId)
																		.param("weekResultDriver", weekResultDriver)
																		.sessionAttr("dayStart", dayStart)
																		.sessionAttr("patient", patient)
																		.sessionAttr("token", decodeToken)
																		.sessionAttr("allDoctorsList", allDoctorsList))
																		.andExpect(MockMvcResultMatchers.status().isOk())
																		.andExpect(MockMvcResultMatchers.view().name("/visit/toReserve"))
																		.andExpect(MockMvcResultMatchers.model().attribute("disableLeftArrow", "YES"))
																		.andReturn().getModelAndView().getModel();
		@SuppressWarnings("unchecked")
		Map<LocalDate, Map<LocalTime, Boolean>> workingWeekFreeTimeMap = (Map<LocalDate, Map<LocalTime, Boolean>>) modelForNewVisit.get("workingWeekFreeTimeMap");
		Doctor doctor = (Doctor) modelForNewVisit.get("doctor");
		
		String [] dateTime = new String[1];
		Map<LocalTime, Boolean> mapFreeTime;
		for(int i=0; i<workingWeekFreeTimeMap.size(); i++) {
			mapFreeTime = workingWeekFreeTimeMap.get(LocalDate.now().plusDays(i));
			if(!mapFreeTime.isEmpty()) {
				Object [] array = mapFreeTime.keySet().toArray();
				dateTime[0] = LocalDate.now().plusDays(i).toString()+";"+array[0].toString();
				System.out.println(dateTime[0]);
				break;
			}
		}
		
//		reservation new visit
//		add patient and token to session for security filter
		mockMvc.perform(MockMvcRequestBuilders.post("/visit/reservation")
						.param("dateTime", dateTime)
						.param("treatment1", "1")
						.param("treatment2", "2")
						.param("treatment3", "3")
						.sessionAttr("doctor", doctor)
						.sessionAttr("token", decodeToken)
						.sessionAttr("patient", patient))
						.andExpect(MockMvcResultMatchers.status().isOk())
						.andExpect(MockMvcResultMatchers.view().name("forward:/patient/success"));
		
//		checking controller action and getting model for delete visit
		Map<String, Object> modelForDeleteVisit = mockMvc.perform(MockMvcRequestBuilders.get("/visit/myVisits")																		
																								.sessionAttr("patient", patient)
																								.sessionAttr("token", decodeToken))
																								.andExpect(MockMvcResultMatchers.status().isOk())
																								.andExpect(MockMvcResultMatchers.view().name("/visit/myVisits"))
																								.andReturn().getModelAndView().getModel();
		@SuppressWarnings("unchecked")
		List<Visit> allVisitsList = (List<Visit>) modelForDeleteVisit.get("allVisitsList");
		@SuppressWarnings("unchecked")
		List<VisitStatus> visitStatusList = (List<VisitStatus>) modelForDeleteVisit.get("visitStatusList");
		
		@SuppressWarnings("unchecked")
		List<Visit> visitsListByStatus = (List<Visit>) mockMvc.perform(MockMvcRequestBuilders.post("/visit/myVisits")
																								.param("statusId", "1")
																								.sessionAttr("allVisitsList", allVisitsList)
																								.sessionAttr("visitStatusList", visitStatusList)
																								.sessionAttr("token", decodeToken)
																								.sessionAttr("patient", patient))
																								.andExpect(MockMvcResultMatchers.status().isOk())
																								.andExpect(MockMvcResultMatchers.view().name("/visit/myVisits"))
																								.andReturn().getModelAndView().getModel().get("visitsListByStatus");
		
//		find visit to delete
		String[] splitedDateAndTimeArray = dateTime[0].split(";");
		LocalDateTime visitDateTimeToDelete = LocalDateTime.of(LocalDate.parse(splitedDateAndTimeArray[0]), LocalTime.parse(splitedDateAndTimeArray[1]));
		int visitIdToDelete = 0;
		for(int i=0; i<visitsListByStatus.size(); i++) {
			if(visitsListByStatus.get(i).getVisitDateTime().equals(visitDateTimeToDelete)) {
				visitIdToDelete = visitsListByStatus.get(i).getId();
				break;
			}
		}
		
//		delete visit of found id
		mockMvc.perform(MockMvcRequestBuilders.post("/visit/deleteVisit")
						.param("visitId", String.valueOf(visitIdToDelete))
						.sessionAttr("token", decodeToken)
						.sessionAttr("patient", patient))
						.andExpect(MockMvcResultMatchers.status().isOk())
						.andExpect(MockMvcResultMatchers.view().name("forward:/patient/success"));
	}
}
