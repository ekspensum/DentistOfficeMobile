package pl.dentistoffice.mobile.model;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class VisitTreatmentComment implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int id;
	
	private String comment;
	
	private DentalTreatment treatment;

}
