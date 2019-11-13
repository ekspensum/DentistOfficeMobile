package pl.dentistoffice.mobile.model;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ContactUs {

	@Size(min=2, max=30)
	@Pattern(regexp="^[^|'\":%^#~}{\\]\\[;=<>`]*$")
	private String subject;
	
	@Size(min=2, max=200)
	@Pattern(regexp="^[^|'\":%^#~}{\\]\\[;=<>`]*$")
	private String message;
	
	@Email
	@NotEmpty
	private String replyMail;
	
	@Size(min=0, max=200000)
	private byte[] attachment;

}
