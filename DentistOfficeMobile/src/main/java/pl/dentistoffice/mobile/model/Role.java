package pl.dentistoffice.mobile.model;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Role implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private int id;
	private String role;
	private String roleName;
}
