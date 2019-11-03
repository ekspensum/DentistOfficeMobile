package pl.dentistoffice.mobile.model;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class VisitStatus implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private int id;
	
	private String statusName;
	private String description;
	
	private User userLogged;
}
