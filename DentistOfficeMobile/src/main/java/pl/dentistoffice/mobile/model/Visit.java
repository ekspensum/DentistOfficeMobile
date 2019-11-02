package pl.dentistoffice.mobile.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import lombok.Getter;
import lombok.Setter;


@Getter @Setter
public class Visit implements Serializable {

	private static final long serialVersionUID = 1L;

	private int id;
	

	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
	@JsonSerialize(using = LocalDateTimeSerializer.class)
	private LocalDateTime visitDateTime;
	private boolean visitConfirmation;
	
	private VisitStatus status;
	
	private Patient patient;
	
	private Doctor doctor;
	
//	private Assistant assistant;
	
	private List<DentalTreatment> treatments;
	
	private List<VisitTreatmentComment> visitTreatmentComment;
	
	private User userLogged;

	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
	@JsonSerialize(using = LocalDateTimeSerializer.class)
	private LocalDateTime reservationDateTime;	

	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
	@JsonSerialize(using = LocalDateTimeSerializer.class)
	private LocalDateTime finalizedVisitDateTime;
}
