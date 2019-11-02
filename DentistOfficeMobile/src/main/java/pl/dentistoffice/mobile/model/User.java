package pl.dentistoffice.mobile.model;

import java.io.Serializable;
import java.util.List;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;


@Getter @Setter
public class User implements Serializable {

	private static final long serialVersionUID = 1L;

	private int id;
	
	@Size(min = 3, max = 12)
	@Pattern(regexp="^[^|'\":%^#~}{\\]\\[;=<>`]*$")
	private String username;
	
	@JsonIgnore
	private String password;
	private boolean enabled;
	
	@Size(min = 4, max = 24)
	@JsonIgnore
	private String passwordField;
	
	private List<Role> roles;

}
