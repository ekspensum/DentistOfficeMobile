package pl.dentistoffice.mobile.test.unit.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

import pl.dentistoffice.mobile.controller.HomeController;
import pl.dentistoffice.mobile.model.ContactUs;
import pl.dentistoffice.mobile.model.Doctor;
import pl.dentistoffice.mobile.model.TreatmentCategory;
import pl.dentistoffice.mobile.service.DentalTreatmentService;
import pl.dentistoffice.mobile.service.ReCaptchaService;
import pl.dentistoffice.mobile.service.SendEmail;
import pl.dentistoffice.mobile.service.UserService;
import pl.dentistoffice.mobile.service.VisitService;

class HomeControllerTest {
	
	private MockMvc mockMvc;
	
	@InjectMocks
	private HomeController homeController;
	@Mock
	private Environment env;
	@Mock
	private UserService userService;
	@Mock
	private SendEmail sendEmail;
	@Mock
	private ReCaptchaService reCaptchaService;
	@Mock
	private DentalTreatmentService dentalTreatmentService;
	@Mock
	private VisitService visitService;

	@BeforeEach
	void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		mockMvc = MockMvcBuilders.standaloneSetup(homeController).build();
	}

	@Test
	void testHome() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/"))
						.andExpect(MockMvcResultMatchers.status().isOk())
						.andExpect(MockMvcResultMatchers.view().name("/home/home"));
	}

	@Test
	void testGetDoctors() throws Exception {
		Doctor doctor = new Doctor();
		doctor.setId(33);
		List<Doctor>	doctorList = new ArrayList<>();
		doctorList.add(doctor);
		when(userService.getAllDoctors()).thenReturn(doctorList);
		mockMvc.perform(MockMvcRequestBuilders.get("/doctors"))
						.andExpect(MockMvcResultMatchers.status().isOk())
						.andExpect(MockMvcResultMatchers.view().name("/home/doctors"))
						.andExpect(MockMvcResultMatchers.model().attribute("doctorList", doctorList));
	}

	@Test
	void testServicesModel() throws Exception {
		TreatmentCategory treatmentCategory = new TreatmentCategory();
		treatmentCategory.setId(77);
		List<TreatmentCategory> treatmentCategoriesList = new ArrayList<>();
		treatmentCategoriesList.add(treatmentCategory);
		when(dentalTreatmentService.getTreatmentCategoriesList()).thenReturn(treatmentCategoriesList);
		@SuppressWarnings("unchecked")
		List<TreatmentCategory> treatmentCategoriesListActual = (List<TreatmentCategory>) mockMvc.perform(MockMvcRequestBuilders.get("/services"))
																										.andExpect(MockMvcResultMatchers.status().isOk())
																										.andExpect(MockMvcResultMatchers.view().name("/home/services"))
																										.andReturn().getModelAndView().getModel().get("treatmentCategoriesList");
		assertEquals(77, treatmentCategoriesListActual.get(0).getId());
	}

	@Test
	void testServicesStringModel() throws Exception {
		TreatmentCategory treatmentCategory = new TreatmentCategory();
		treatmentCategory.setId(77);
		when(dentalTreatmentService.getTreatmentCategory(77)).thenReturn(treatmentCategory);
		List<TreatmentCategory> treatmentCategoriesList = new ArrayList<>();
		treatmentCategoriesList.add(treatmentCategory);
		when(dentalTreatmentService.getTreatmentCategoriesList()).thenReturn(treatmentCategoriesList);
		
		TreatmentCategory treatmentCategoryActual = (TreatmentCategory) mockMvc.perform(MockMvcRequestBuilders.post("/services")
																							.param("categoryId", "77"))
																							.andExpect(MockMvcResultMatchers.status().isOk())
																							.andExpect(MockMvcResultMatchers.view().name("/home/services"))
																							.andReturn().getModelAndView().getModel().get("selectedTreatmentCategory");
		assertEquals(77, treatmentCategoryActual.getId());
	}

	@Test
	void testAgendaModel() throws Exception {
		List<Doctor> allDoctors = userService.getAllDoctors();
		mockMvc.perform(MockMvcRequestBuilders.get("/agenda"))
																				.andExpect(MockMvcResultMatchers.status().isOk())
																				.andExpect(MockMvcResultMatchers.view().name("/home/agenda"))
																				.andExpect(MockMvcResultMatchers.model().attribute("allDoctors", allDoctors));
	}

	@Test
	void testAgendaStringDoctorListOfDoctorStringIntegerModel() throws Exception {
		String doctorId = "5";
		List<Doctor> allDoctors = userService.getAllDoctors();
		Doctor doctor = new Doctor();
		doctor.setId(Integer.valueOf(doctorId));
		
		when(userService.getDoctor(Integer.valueOf(doctorId), allDoctors)).thenReturn(doctor);
		Map<LocalDate, Map<LocalTime, Boolean>> workingWeekFreeTimeMap = new LinkedHashMap<>();
		when(visitService.getWorkingWeekFreeTimeMap(doctor.getId(), 0, 7)).thenReturn(workingWeekFreeTimeMap);
		
		String weekResultDriver = null;
		mockMvc.perform(MockMvcRequestBuilders.post("/agenda")
						.param("doctorId", doctorId)
						.sessionAttr("doctor", doctor)
						.sessionAttr("allDoctors", allDoctors)
						.param("weekResultDriver", weekResultDriver)
						.sessionAttr("dayStart", 0))
						.andExpect(MockMvcResultMatchers.status().isOk())
						.andExpect(MockMvcResultMatchers.view().name("/home/agenda"))
						.andExpect(MockMvcResultMatchers.model().attribute("disableLeftArrow", "YES"));
		
		weekResultDriver = "stepRight";
		mockMvc.perform(MockMvcRequestBuilders.post("/agenda")
						.param("doctorId", doctorId)
						.sessionAttr("doctor", doctor)
						.sessionAttr("allDoctors", allDoctors)
						.param("weekResultDriver", weekResultDriver)
						.sessionAttr("dayStart", 14))
						.andExpect(MockMvcResultMatchers.status().isOk())
						.andExpect(MockMvcResultMatchers.view().name("/home/agenda"))
						.andExpect(MockMvcResultMatchers.model().attribute("dayStart", 21))
						.andExpect(MockMvcResultMatchers.model().attribute("disableRightArrow", "YES"));
		
		weekResultDriver = "stepLeft";
		mockMvc.perform(MockMvcRequestBuilders.post("/agenda")
						.param("doctorId", doctorId)
						.sessionAttr("doctor", doctor)
						.sessionAttr("allDoctors", allDoctors)
						.param("weekResultDriver", weekResultDriver)
						.sessionAttr("dayStart", 7))
						.andExpect(MockMvcResultMatchers.status().isOk())
						.andExpect(MockMvcResultMatchers.view().name("/home/agenda"))
						.andExpect(MockMvcResultMatchers.model().attribute("dayStart", 0))
						.andExpect(MockMvcResultMatchers.model().attribute("disableLeftArrow", "YES"));
	}

	@Test
	void testContactUs() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/contactus"))
						.andExpect(MockMvcResultMatchers.status().isOk())
						.andExpect(MockMvcResultMatchers.view().name("/home/contactus"));
	}

	@Test
	void testSendMessage() throws Exception {
		MockMultipartFile file = new MockMultipartFile("file", "file".getBytes());
		ContactUs contactUs = new ContactUs();
		contactUs.setMessage("message123");
		contactUs.setReplyMail("replyMail@gmail.com");
		contactUs.setSubject("subject");
		contactUs.setAttachment(file.getBytes());
		
		when(reCaptchaService.verify("reCaptchaResponse")).thenReturn(false);
		mockMvc.perform(MockMvcRequestBuilders.multipart("/contactus")
						.file("attachment", file.getBytes())
						.flashAttr("contactUs", contactUs)
						.param("g-recaptcha-response", "reCaptchaResponse"))
						.andExpect(MockMvcResultMatchers.status().isOk())
						.andExpect(MockMvcResultMatchers.view().name("/home/contactus"))
						.andExpect(MockMvcResultMatchers.model().attribute("reCaptchaError", "reCaptchaError"));
	}

}
