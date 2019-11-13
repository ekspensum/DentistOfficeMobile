package pl.dentistoffice.mobile.service;

import java.util.Properties;

import javax.mail.internet.*;

import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;

@Service
@Primary
public class SendEmailInteriaService implements SendEmail {

	@Override
	public void sendEmail(Environment env, String mailTo, String mailSubject, String mailText, String replyMail, byte[] attachment, String fileName) {
		
		MimeMessagePreparator messagePreparator = new MimeMessagePreparator() {
			
			@Override
			public void prepare(MimeMessage mimeMessage) throws Exception {
                MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
                helper.setSubject(mailSubject);
                helper.setFrom(env.getProperty("mailFrom"));
                helper.setTo(mailTo);
                helper.setText(mailText, true);
                helper.addAttachment(fileName, new ByteArrayResource(attachment));
			}
		};
		JavaMailSenderImpl configuredEmail = configureEmail(env);
		configuredEmail.send(messagePreparator);
	}

	@Override
	public void sendEmail(Environment env, String mailTo, String mailSubject, String mailText) {
		MimeMessagePreparator messagePreparator = new MimeMessagePreparator() {
			
			@Override
			public void prepare(MimeMessage mimeMessage) throws Exception {
                MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
                helper.setSubject(mailSubject);
                helper.setFrom(env.getProperty("mailFrom"));
                helper.setTo(mailTo);
                helper.setText(mailText, true);
			}
		};
		JavaMailSenderImpl configuredEmail = configureEmail(env);
		configuredEmail.send(messagePreparator);		
	}
	
	
//	PRIVATE METHODS
	private JavaMailSenderImpl configureEmail(Environment env) {
		JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
		javaMailSender.setHost(env.getProperty("mail.smtp.host"));
		javaMailSender.setPort(Integer.valueOf(env.getProperty("mail.smtp.port")));
//		javaMailSender.setUsername(env.getProperty("mail_user"));
//		javaMailSender.setPassword(env.getProperty("mail_password"));
		
//		for Heroku cloud
		javaMailSender.setUsername(System.getenv("INTERIA_MAIL_ID"));
		javaMailSender.setPassword(System.getenv("INTERIA_MAIL_PASSWORD"));

		Properties javaMailProperties = new Properties();
		javaMailProperties.put("mail.smtp.starttls.enable", env.getProperty("mail.smtp.starttls.enable"));
		javaMailProperties.put("mail.smtp.auth", env.getProperty("mail.smtp.auth"));
		javaMailProperties.put("mail.transport.protocol", env.getProperty("mail.transport.protocol"));
//		javaMailProperties.put("mail.debug", env.getProperty("mail.debug"));

		javaMailSender.setJavaMailProperties(javaMailProperties);
	
		return javaMailSender;
	}
	
}
