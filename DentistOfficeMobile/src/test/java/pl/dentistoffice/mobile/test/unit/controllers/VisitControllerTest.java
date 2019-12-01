package pl.dentistoffice.mobile.test.unit.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import pl.dentistoffice.mobile.controller.VisitController;
import pl.dentistoffice.mobile.model.DentalTreatment;
import pl.dentistoffice.mobile.model.Doctor;
import pl.dentistoffice.mobile.model.Patient;
import pl.dentistoffice.mobile.model.Visit;
import pl.dentistoffice.mobile.model.VisitAndStatusListWrapper;
import pl.dentistoffice.mobile.model.VisitStatus;
import pl.dentistoffice.mobile.service.DentalTreatmentService;
import pl.dentistoffice.mobile.service.UserService;
import pl.dentistoffice.mobile.service.VisitService;

class VisitControllerTest {
	
	private MockMvc mockMvc;
	
	@InjectMocks
	private VisitController visitController;
	@Mock
	private VisitService visitService;
	@Mock
	private UserService userService;
	@Mock
	private DentalTreatmentService treatmentService;
	@Mock
	private HttpSession httpSession;
	@Mock
	private Model model;

	@BeforeEach
	void setUp() throws Exception {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/resources/templates/");
        viewResolver.setSuffix(".html"); 
        
		MockitoAnnotations.initMocks(this);
		mockMvc = MockMvcBuilders.standaloneSetup(visitController).setViewResolvers(viewResolver).build();
	}

	@Test
	void testVisitSelectDoctorByPatient() throws Exception {
		List<Doctor> allDoctorsList = userService.getAllDoctors();
		when(userService.getAllDoctors()).thenReturn(allDoctorsList);
		
		mockMvc.perform(MockMvcRequestBuilders.get("/visit/selectDoctor"))
						.andExpect(MockMvcResultMatchers.status().isOk())
						.andExpect(MockMvcResultMatchers.view().name("/visit/selectDoctor"))
						.andExpect(MockMvcResultMatchers.model().attribute("allDoctorsList", allDoctorsList));
	}

	@Test
	void testVisitToReserveByPatient() throws Exception {
		String doctorId = "15";
		List<Doctor> allDoctorsList = userService.getAllDoctors();
		Doctor doctor = new Doctor();
		doctor.setId(Integer.valueOf(doctorId));
		allDoctorsList.add(doctor);
		String weekResultDriver = null;
		String token = "token123";
		
		DentalTreatment dentalTreatment = new DentalTreatment();
		dentalTreatment.setDescription("description");
		List<DentalTreatment> treatments = new ArrayList<>();
		treatments.add(dentalTreatment);
		when(treatmentService.getDentalTreatmentsList(token)).thenReturn(treatments); 
		
		when(userService.getDoctor(Integer.valueOf(doctorId), allDoctorsList)).thenReturn(doctor);
		
		Map<LocalDate, Map<LocalTime, Boolean>> workingWeekFreeTimeMap = new LinkedHashMap<>();
		when(visitService.getWorkingWeekFreeTimeMap(doctor.getId(), 0, 7)).thenReturn(workingWeekFreeTimeMap);
		
		String visitToReserveByPatient = visitController.visitToReserveByPatient(doctorId, weekResultDriver, doctor, 0, allDoctorsList, null, token, model);
		assertEquals("/visit/toReserve", visitToReserveByPatient);
		
		weekResultDriver = null;
		mockMvc.perform(MockMvcRequestBuilders.post("/visit/toReserve")
						.param("doctorId", doctorId)
						.param("weekResultDriver", weekResultDriver)
						.sessionAttr("doctor", doctor)
						.sessionAttr("dayStart", 0)
						.sessionAttr("allDoctorsList", allDoctorsList)
						.sessionAttr("treatments", treatments)
						.sessionAttr("token", token))
						.andExpect(MockMvcResultMatchers.status().isOk())
						.andExpect(MockMvcResultMatchers.view().name("/visit/toReserve"))
						.andExpect(MockMvcResultMatchers.model().attribute("disableLeftArrow", "YES"));
		
		weekResultDriver = "stepRight";
		mockMvc.perform(MockMvcRequestBuilders.post("/visit/toReserve")
						.param("doctorId", doctorId)
						.param("weekResultDriver", weekResultDriver)
						.sessionAttr("doctor", doctor)
						.sessionAttr("dayStart", 14)
						.sessionAttr("allDoctorsList", allDoctorsList)
						.sessionAttr("treatments", treatments)
						.sessionAttr("token", token))
						.andExpect(MockMvcResultMatchers.status().isOk())
						.andExpect(MockMvcResultMatchers.view().name("/visit/toReserve"))
						.andExpect(MockMvcResultMatchers.model().attribute("disableRightArrow", "YES"));
		
		weekResultDriver = "stepLeft";
		mockMvc.perform(MockMvcRequestBuilders.post("/visit/toReserve")
						.param("doctorId", doctorId)
						.param("weekResultDriver", weekResultDriver)
						.sessionAttr("doctor", doctor)
						.sessionAttr("dayStart", 7)
						.sessionAttr("allDoctorsList", allDoctorsList)
						.sessionAttr("treatments", treatments)
						.sessionAttr("token", token))
						.andExpect(MockMvcResultMatchers.status().isOk())
						.andExpect(MockMvcResultMatchers.view().name("/visit/toReserve"))
						.andExpect(MockMvcResultMatchers.model().attribute("disableLeftArrow", "YES"));
	}

	@Test
	void testVisitReservationByPatient() throws Exception {
		String token = "token123";
		Doctor doctor = new Doctor();
		doctor.setId(11);
		Patient patient = new Patient();
		patient.setId(22);
		String [] dateTime = {"dateTime"};
				
		when(visitService.addNewVisitByMobilePatient(doctor.getId(), patient.getId(), dateTime[0], "1", "2", "3", token)).thenReturn(true);
		mockMvc.perform(MockMvcRequestBuilders.post("/visit/reservation")
						.sessionAttr("doctor", doctor)
						.param("dateTime", dateTime)
						.param("treatment1", "1")
						.param("treatment2", "2")
						.param("treatment3", "3")
						.sessionAttr("token", token)
						.sessionAttr("patient", patient))
						.andExpect(MockMvcResultMatchers.status().isOk())
						.andExpect(MockMvcResultMatchers.view().name("forward:/patient/success"));
		
		when(visitService.addNewVisitByMobilePatient(doctor.getId(), patient.getId(), dateTime[0], "1", "2", "3", token)).thenReturn(false);
		mockMvc.perform(MockMvcRequestBuilders.post("/visit/reservation")
						.sessionAttr("doctor", doctor)
						.param("dateTime", dateTime)
						.param("treatment1", "1")
						.param("treatment2", "2")
						.param("treatment3", "3")
						.sessionAttr("token", token)
						.sessionAttr("patient", patient))
						.andExpect(MockMvcResultMatchers.status().isOk())
						.andExpect(MockMvcResultMatchers.view().name("forward:/patient/defeat"));
		
		String [] dateTime2 = {"dateTime1", "dateTime2"};
		mockMvc.perform(MockMvcRequestBuilders.post("/visit/reservation")
						.sessionAttr("doctor", doctor)
						.param("dateTime", dateTime2)
						.param("treatment1", "1")
						.param("treatment2", "2")
						.param("treatment3", "3")
						.sessionAttr("token", token)
						.sessionAttr("patient", patient))
						.andExpect(MockMvcResultMatchers.status().isOk())
						.andExpect(MockMvcResultMatchers.view().name("forward:/patient/defeat"));
	}

	@Test
	void testShowMyVisitsPatientPatientStringModel() throws Exception {
		String token = "token123";
		Patient patient = new Patient();
		patient.setId(22);
		VisitAndStatusListWrapper visitsAndStatusListForPatient = Mockito.mock(VisitAndStatusListWrapper.class);
		when(visitService.getVisitsAndStatusListForPatient(patient.getId(), token)).thenReturn(visitsAndStatusListForPatient);
		List<VisitStatus> visitStatusList = new ArrayList<>(); 
		when(visitsAndStatusListForPatient.getVisitStatusList()).thenReturn(visitStatusList);
		List<Visit> allVisitsList = new ArrayList<>(); 
		when(visitsAndStatusListForPatient.getVisitsList()).thenReturn(allVisitsList);
		
		mockMvc.perform(MockMvcRequestBuilders.get("/visit/myVisits")
						.sessionAttr("patient", patient)
						.sessionAttr("token", token))
						.andExpect(MockMvcResultMatchers.status().isOk())
						.andExpect(MockMvcResultMatchers.view().name("/visit/myVisits"));
	}

	@Test
	void testShowMyVisitsPatientListOfVisitStringListOfVisitStatusModel() throws Exception {
		String statusId = "1";
		List<Visit> allVisitsList = new ArrayList<>();
		List<Visit> visitsListByStatus = new ArrayList<>();
		when(visitService.getVisitsListByStatus(allVisitsList, statusId)).thenReturn(visitsListByStatus);
		List<VisitStatus> visitStatusList = new ArrayList<>();
		
		mockMvc.perform(MockMvcRequestBuilders.post("/visit/myVisits")
						.sessionAttr("allVisitsList", allVisitsList)
						.param("statusId", statusId)
						.sessionAttr("visitStatusList", visitStatusList))
						.andExpect(MockMvcResultMatchers.status().isOk())
						.andExpect(MockMvcResultMatchers.view().name("/visit/myVisits"));
	}

	@Test
	void testDeleteVisit() throws Exception {
		String token = "token123";
		String visitId = "111";
		
		boolean deleteVisit = true;
		when(visitService.deleteVisit(visitId, token)).thenReturn(deleteVisit);
		mockMvc.perform(MockMvcRequestBuilders.post("/visit/deleteVisit")
						.sessionAttr("token", token)
						.param("visitId", visitId))
						.andExpect(MockMvcResultMatchers.status().isOk())
						.andExpect(MockMvcResultMatchers.view().name("forward:/patient/success"));
		
		deleteVisit = false;
		when(visitService.deleteVisit(visitId, token)).thenReturn(deleteVisit);
		mockMvc.perform(MockMvcRequestBuilders.post("/visit/deleteVisit")
						.sessionAttr("token", token)
						.param("visitId", visitId))
						.andExpect(MockMvcResultMatchers.status().isOk())
						.andExpect(MockMvcResultMatchers.view().name("forward:/patient/defeat"));
	}

}
