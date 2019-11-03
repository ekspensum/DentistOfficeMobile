package pl.dentistoffice.mobile.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Base64;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.pl.PESEL;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Doctor implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private int id;
	
	@Size(min = 3, max = 15)
	@Pattern(regexp="^[^|'\":%^#~}{\\]\\[;=<>`]*$")
	private String firstName;
	
	@Size(min = 3, max = 25)
	@Pattern(regexp="^[^|'\":%^#~}{\\]\\[;=<>`]*$")
	private String lastName;
	
	@Size(min = 10, max = 1024)
	@Pattern(regexp="^[^|'%^#~}{\\]\\[;=<>`]*$")
	private String description;
	
	@Email
	@NotEmpty
	private String email;
	
	@Size(min = 8, max = 20)
	@Pattern(regexp="^[^|'\":%^#~}{\\]\\[;=<>`]*$")
	private String phone;

	@PESEL
	private String pesel;
	
	@Size(min=0, max=600000)
	private byte [] photo;
	
	@Valid
	private User user;
	
	private WorkingWeek workingWeek;

	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
	@JsonSerialize(using = LocalDateTimeSerializer.class)
	private LocalDateTime registeredDateTime;
	
	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
	@JsonSerialize(using = LocalDateTimeSerializer.class)
	private LocalDateTime editedDateTime;
	
	@JsonIgnore
	public String getBase64Photo() {
		if(photo == null) {
			return "";
		} else {
			return Base64.getEncoder().encodeToString(this.photo);			
		}
	}
	
}
