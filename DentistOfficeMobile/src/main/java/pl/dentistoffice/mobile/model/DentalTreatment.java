package pl.dentistoffice.mobile.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class DentalTreatment implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private int id;
	
	@Size(min = 3, max = 100)
	@Pattern(regexp="^[^|'\":%^#~}{\\]\\[;=<>`]*$")
	private String name;
	
	@Size(min = 10, max = 500)
	@Pattern(regexp="^[^|'%^#~}{\\]\\[;=<>`]*$")
	private String description;
	
	@DecimalMax("10000.0") @DecimalMin("0.0") 
	private double price;
	
	private List<TreatmentCategory> treatmentCategory;
	
	private List<Visit> visits;
	
	private User userLogged;

	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
	private LocalDateTime registeredDateTime;

	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
	private LocalDateTime editedDateTime;
}
