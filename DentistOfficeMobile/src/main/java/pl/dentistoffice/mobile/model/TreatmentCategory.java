package pl.dentistoffice.mobile.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class TreatmentCategory implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private int id;
	
	private String categoryName;
	
	private List<DentalTreatment> dentalTreatment;
	
	@JsonIgnore
	private User userLogged;

	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
	private LocalDateTime registeredDateTime;

	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
	private LocalDateTime editedDateTime;
}
