package pl.dentistoffice.mobile.service;

import org.springframework.core.env.Environment;

public interface SendEmail {

	void sendEmail(Environment env, String mailTo, String mailSubject, String mailText, String replyMail, byte[] attachment, String fileName);
	void sendEmail(Environment env, String mailTo, String mailSubject, String mailText);
}
