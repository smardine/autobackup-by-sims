/*
 * This code is used for send the mail to the any users.</div> It is done by
 * Java Mail Api. By using this code we can send mail with out enter into your
 * mail Account.
 * @author muneeswaran
 */
package Dialogue;

import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class SendMailUsingAuthenticationWithAttachement {

	private final String HOST_NAME = "smtp.laposte.net";
	String messageBody;

	public boolean postMail(final String recipients[], final String subject,
			final String message, final String from,
			final String emailPassword, final String[] files)
			throws MessagingException {
		final boolean debug = false;
		// java.security.Security.addProvider(new
		// com.sun.net.ssl.internal.ssl.Provider());

		// Set the host smtp address
		final Properties props = new Properties();
		props.put("mail.transport.protocol", "smtp");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", HOST_NAME);
		props.put("mail.smtp.auth", "true");

		final Authenticator authenticator = new SMTPAuthenticator(from,
				emailPassword);
		final Session session = Session
				.getDefaultInstance(props, authenticator);

		session.setDebug(debug);

		// create a message
		final Message msg = new MimeMessage(session);

		// set the from and to address
		final InternetAddress addressFrom = new InternetAddress(from);
		msg.setFrom(addressFrom);

		final InternetAddress[] addressTo = new InternetAddress[recipients.length];
		for (int i = 0; i < recipients.length; i++) {
			addressTo[i] = new InternetAddress(recipients[i]);
		}
		msg.setRecipients(Message.RecipientType.TO, addressTo);

		// Setting the Subject and Content Type
		msg.setSubject(subject);
		msg.setContent(message, "text/plain");

		final BodyPart messageBodyPart = new MimeBodyPart();
		messageBodyPart.setText(message);

		final Multipart multipart = new MimeMultipart();

		// add the message body to the mime message
		multipart.addBodyPart(messageBodyPart);

		// add any file attachments to the message
		addAtachments(files, multipart);
		// Put all message parts in the message
		msg.setContent(multipart);
		Transport.send(msg);
		System.out.println("Sucessfully Sent mail to All Users");
		return true;
	}

	protected void addAtachments(final String[] attachments,
			final Multipart multipart) throws MessagingException,
			AddressException {
		for (int i = 0; i <= attachments.length - 1; i++) {
			final String filename = attachments[i];
			final MimeBodyPart attachmentBodyPart = new MimeBodyPart();
			// use a JAF FileDataSource as it does MIME type detection
			final DataSource source = new FileDataSource(filename);
			attachmentBodyPart.setDataHandler(new DataHandler(source));
			attachmentBodyPart.setFileName(filename);
			// add the attachment
			multipart.addBodyPart(attachmentBodyPart);
		}
	}

	private class SMTPAuthenticator extends javax.mail.Authenticator {

		String username;
		String password;

		private SMTPAuthenticator(final String authenticationUser,
				final String authenticationPassword) {
			username = authenticationUser;
			password = authenticationPassword;
		}

		@Override
		public PasswordAuthentication getPasswordAuthentication() {

			return new PasswordAuthentication(username, password);
		}
	}
}
